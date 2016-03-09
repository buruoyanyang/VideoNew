package com.example.biezhi.videonew.CustomerClass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xhf.
 * BiezhiVideo
 * 2015/11/2 14:35
 */
public class VideoPlayNetServer {

    public String Url = "http://115.29.190.54:99/Videos.aspx?page=0&cat=26&size=30&order=2&district=0&kind=0&appid=1&version=1.0";
    public String unlockJson = "";
    public String key = "C169F435FEA3530E";
    public void getVideoPlayUrl()
    {
        //// TODO: 2015/10/30 连接服务器，获取视频列表
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = Url;
        try
        {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null)
            {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
            result = result.trim();
            AES aes = new AES();
            byte[] tempResult = aes.Decrypt(result,key);
            unlockJson = new String(tempResult,"UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

}
