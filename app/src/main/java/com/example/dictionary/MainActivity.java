package com.example.dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SearchView searchView;
    private Spinner language_spinner;
    public RequestPermission requestPermission;
    final Handler handler = new Handler();
    private String[] language_array;
    private int language_selection;


    private ListView mList;
    private CardAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList = findViewById(R.id.listview);

        if(requestPermission == null) {
            requestPermission = new RequestPermission();
        }
        requestPermission.RequestPermission(this);



        initializeSpinner();
        initializeSearchView();

    }

    private void initializeSearchView(){
        searchView = findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("请输入字或词语");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = null;
                        try {

                            jsonObject = HttpUrlConnectionUtils.getSign(language_array[language_selection], query);

                            JSONObject data = jsonObject.getJSONObject("data");
                            List<Card> list = new ArrayList<>();
                            int counter = 0;
                            for (Iterator<String> it = data.keys(); it.hasNext(); ) {
                                String key = it.next();
                                JSONArray value = data.getJSONArray(key);
                                for(int i = 0 ; i < value.length();i++) {
                                    list.add(new Card(counter, key, value.get(i).toString()));
                                    counter++;
                                }
                            }

                            initializeListView(list);

                        } catch (IOException e) {
                            show();
                            e.printStackTrace();
                        } catch (JSONException e) {
                            show();
                            e.printStackTrace();
                        }
                    }
                }).start();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initializeSpinner(){
        language_spinner = findViewById(R.id.language_spinner);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = HttpUrlConnectionUtils.getGroup("language");
                    String array = jsonObject.get("data").toString();
                    language_array = stringToArray(array);
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, language_array);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            language_spinner.setAdapter(adapter);
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language_selection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initializeListView(List<Card> list){
        mAdapter = new CardAdapter(this, list);


        handler.post(new Runnable() {
            @Override
            public void run() {
                mList.setAdapter(mAdapter);
            }
        });


    }


    private String[] stringToArray(String str){
        str = str.substring(1, str.length()-1);
        String[] answer = str.split(",");
        for(int i = 0; i<answer.length;i++){
            answer[i] = answer[i].substring(1, answer[i].length()-1);
        }
        return answer;
    }

    private void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("查询失败")
                .setMessage("目前这个语言没有收录这个字").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情

                    }
                });
        Looper.prepare();
        builder.create().show();
        Looper.loop();
    }


}