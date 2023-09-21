package com.example.dictionary;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CardAdapter extends BaseAdapter {
    private Context mContext;//上下文环境
    /**
     * 主要用于加载item_msg的布局
     */
    private LayoutInflater mInflater;
    private final List<Card> mData;
    private MediaPlayer mMediaPlayer;

    /**
     * 构造方法
     */
    public CardAdapter(Context context, List<Card> data) {

        /**
         * 赋值
         */
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mData = data;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null){

            convertView=mInflater.inflate(R.layout.item_msg,parent,false);

            viewHolder=new ViewHolder();

            viewHolder.sign_text=convertView.findViewById(R.id.sign_text);
            viewHolder.btn_voice=convertView.findViewById(R.id.btn_voice);

            convertView.setTag(viewHolder);
        }

        else {

            viewHolder= (ViewHolder) convertView.getTag();
        }

        Card card= mData.get(position);

        viewHolder.sign_text.setText(card.getSign());
        viewHolder.btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String path = HttpUrlConnectionUtils.getAudio(card.getLocation());
                            if(mMediaPlayer == null)
                                mMediaPlayer = new MediaPlayer();
                            else
                                mMediaPlayer.reset();
                            mMediaPlayer.setDataSource(path);
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        return convertView;
    }

    /**
     * 内部类：可省去findViewById的时间
     */
    public static class ViewHolder {
        TextView sign_text;
        ImageButton btn_voice;
    }


}
