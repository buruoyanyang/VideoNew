package com.example.biezhi.videonew.NetWorkServer;

/**
 * Created by biezhi on 2016/1/25.
 */
import com.example.biezhi.videonew.CustomerClass.AES;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class initNetWork {
    //URL
    private static final String serverURL = "http://www.biezhi360.cn:99/category.aspx?appid=1&version=1.0";
    //服务器状态
    public boolean serverOnline = true;

    //服务端timeout
    private int SERVER_TIME_OUT = 5000;

    //解密后的json
    public String unlockJson;

    //连接服务端，获取分类列表
    public void getTabulation() {
        //// TODO: 2015/10/29 连接服务器，获取分类列表
        BufferedReader reader;
        String result;
        StringBuilder sbf = new StringBuilder();
        String httpUrl = serverURL;
        try
        {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(SERVER_TIME_OUT);
            connection.connect();
            connection.setReadTimeout(SERVER_TIME_OUT);
            InputStream inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String strRead;
            while ((strRead = reader.readLine()) != null)
            {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
            result = result.trim();
            result = result.substring(10, result.length());
            result = result.substring(0, result.length() - 2);
            AES aes = new AES();
            byte[] tempResult = aes.Decrypt(result,"C169F435FEA3530E");
            unlockJson = new String(tempResult,"UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            unlockJson = "";
        }
    }



}
