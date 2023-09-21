package com.example.dictionary;

import android.os.Environment;
import android.renderscript.ScriptGroup;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpUrlConnectionUtils {
    public static String getSignUrl = "http://175.178.47.182:80/getSign/";
    public static String getAudioUrl = "http://175.178.47.182:80/getAudio?location=";
    public static String getGroup = "http://175.178.47.182:80/getGroup/";


    public static JSONObject getSign(String language, String str) throws IOException, JSONException {
        URL url = new URL(getSignUrl+language+"/"+str);
        return get(url);
    }

    public static JSONObject getGroup(String column) throws IOException, JSONException {
        URL url = new URL(getGroup+column);
        return get(url);
    }

    public static JSONObject get(URL url) throws IOException, JSONException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(3000);
        httpURLConnection.setRequestMethod("GET");

        int responseCode = httpURLConnection.getResponseCode();
        InputStream inputStream = null;
        JSONObject json = null;

        if(responseCode == HttpURLConnection.HTTP_OK){
            inputStream = httpURLConnection.getInputStream();
            json = new JSONObject(readInputStream(inputStream));
        }
        httpURLConnection.disconnect();
        return json;
    }



    public static String getAudio(String location) throws Exception {
        String path = null;
        
        URL url = new URL(getAudioUrl+location);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(3000);
        httpURLConnection.setRequestMethod("GET");
        String fileName = location.substring(location.length()-19);

        int responseCode = httpURLConnection.getResponseCode();
        InputStream inputStream = null;

        if(responseCode == HttpURLConnection.HTTP_OK){
            File dest = new File(Environment.getExternalStorageDirectory()+"/voice_cache/");
            if(!dest.exists()) {
                dest.mkdir();
            }
            path = Environment.getExternalStorageDirectory()+"/voice_cache/";
            inputStream = httpURLConnection.getInputStream();// 通过输入流获取html数据
            byte[] data = readInputStream2(inputStream);// 得到html的二进制数据
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(path+fileName));//把byte写入文件
            dataOutputStream.write(data);
            dataOutputStream.flush();
            path += fileName;
        }
        httpURLConnection.disconnect();
        return path;
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String tmp;
        StringBuilder sb = new StringBuilder();
        while ((tmp = reader.readLine()) != null) {
            sb.append(tmp).append("\n");
        }
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
            sb.setLength(sb.length() - 1);
        }
        reader.close();
        System.out.println(sb);
        return sb.toString();
    }


    public static byte[] readInputStream2(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

}
