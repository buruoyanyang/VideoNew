package com.example.biezhi.videonew.CustomerClass;

/**
 * Created by biezhi on 2016/1/26.
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageService {
    public static byte[] getImage(String path) throws IOException {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");   //设置请求方法为GET
        conn.setReadTimeout(5*1000);    //设置请求过时时间为5秒
        InputStream inputStream = conn.getInputStream();   //通过输入流获得图片数据
        byte[] data = StreamTool.readInputStream(inputStream);     //获得图片的二进制数据
        return data;

    }
}
