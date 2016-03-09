package com.example.biezhi.videonew.CustomerClass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xhf.
 * BiezhiVideo
 * 2015/11/5 17:06
 */
public class GetaiqiyiUrl {
    public boolean isFirst = true;
    String result = "";
    BufferedReader reader = null;
    StringBuffer sbf = new StringBuffer();
    public String playUrl(String _url)
    {
        String _playUrl ="";
        try {
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (isFirst)
            {
                connection.setRequestMethod("POST");
                connection.setRequestProperty("UserAgent", "QIYI/5.6 (iPhone;en-US;Build:20140828190560)");
                connection.setRequestProperty("t", "391555535");
                connection.setRequestProperty("sign", "0ec033dc968665f3c80779fa140e0cef");
            }
            else
            {
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "VerticalVideo/5.1 CFNetwork/758.1.6 Darwin/15.0.0");
                connection.setRequestProperty("Accept-Language", "zh-cn");
                connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
                //connection.addRequestProperty("Accept-Encoding", "deflate");
            }
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String strRead;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
            byte[] bs = result.getBytes();
            result = new String(bs, "US-ASCII");
            _playUrl = result;
            //第二次解析
            /*
            JSONObject jsonObject =new JSONObject(result);
            jsonObject = jsonObject.getJSONObject("tv").getJSONObject("0");
            _playUrl = jsonObject.getString("_durl");
            URL url_ = new URL(_playUrl);
            HttpURLConnection _connection = (HttpURLConnection) url_.openConnection();
            _connection.setRequestMethod("GET");
            _connection.connect();
            InputStream _inputStream = _connection.getInputStream();
            //_inputStream = _connection.getInputStream();
            _reader = new BufferedReader(new InputStreamReader(_inputStream, "UTF-8"));
            while ((strRead = _reader.readLine()) != null)
            {
                _sbf.append(strRead);
                _sbf.append("\r\n");
            }
            _reader.close();
            result = _sbf.toString();
            result = result.trim();
            result = result.substring(13, result.length());
            result = result.substring(0, result.length() - 1);
            result = result.replaceAll("code", "\"code\'");
            result = result.replaceAll("data", "\"data\"");
            jsonObject = new JSONObject(result);
            jsonObject = jsonObject.getJSONObject("data");
            _playUrl = jsonObject.getString("l");*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return _playUrl;
    }




}
