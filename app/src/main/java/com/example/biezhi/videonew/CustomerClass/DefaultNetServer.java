package com.example.biezhi.videonew.CustomerClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xhf.
 * BiezhiVideo
 * 2015/11/5 12:59
 */
public class DefaultNetServer {
    public String Url = "";
    public String result = "";

    public void getPlayUrl() {
        BufferedReader reader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            URL url = new URL(Url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                stringBuffer.append(strRead);
                stringBuffer.append("\r\n");
            }
            reader.close();
            result = stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //解析Json
    public String getValue() {
        String value = "";
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            jsonObject = jsonArray.getJSONObject(0);
            value = jsonObject.optString("value");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    //解析34的Json
    public String get34Value(String videoQuality)
    {
        String value = "";
        try {
            JSONObject jsonObject = new JSONObject(result);
            jsonObject = jsonObject.getJSONObject("data").getJSONObject("video_list").getJSONObject(videoQuality);
            value = jsonObject.optString("main_url");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }
}
