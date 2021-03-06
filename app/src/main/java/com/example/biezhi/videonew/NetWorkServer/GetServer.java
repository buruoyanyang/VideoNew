package com.example.biezhi.videonew.NetWorkServer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.example.biezhi.videonew.CustomerClass.AES;
import com.example.biezhi.videonew.DataModel.TrachModel;
import com.google.gson.Gson;

import java.io.IOException;

/**
 * Created by xiaofeng on 16/2/23.
 */


public class GetServer {
    //服务器状态
    public boolean serverIsOnline = true;
    //    服务器timeout
//    public int serverTimeout = 5000;
    //密钥
    public String aesSecret = "";
    //URL
    public String getUrl = "";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();


    public String getInfoFromServerWithNoSecret()
    {
        Request request = new Request.Builder().url(getUrl).build();
        Response response;
        try {


            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                serverIsOnline = false;
                return "0";
            } else {
                return response.body().string();
            }
        }
        catch (Exception ignored)
        {

        }
        return "3";
    }

    public String getInfoFromServerWithNoData() {
        Request request = new Request.Builder().url(getUrl).build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                serverIsOnline = false;
                return "0";
            } else {
                //处理
                String result = response.body().string();
                byte[] tempResult = AES.Decrypt(result, aesSecret);
                if (tempResult != null) {
                    return new String(tempResult, "UTF-8");
                }
            }
        } catch (IOException e) {
            //io读写错误
            e.printStackTrace();
            return "1";
        } catch (Exception e) {
            //解密失败
            e.printStackTrace();
            return "2";
        }

        return "3";
    }

    /**
     * 获取信息
     *
     * @return
     */
    public String getInfoFromServer() {
        Request request = new Request.Builder().url(getUrl).build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                //获取失败，可能是服务器异常
                serverIsOnline = false;
                return "0";
            } else {
                //处理
                String result = response.body().string();
                final Data resultData = gson.fromJson(result, Data.class);
                //解密
                byte[] tempResult = AES.Decrypt(resultData.data, aesSecret);
                if (tempResult != null) {
//                    String result1 = new String(tempResult,"UTF-8");
                    return new String(tempResult, "UTF-8");
                }

            }
        } catch (IOException e) {
            //io读写错误
            e.printStackTrace();
            return "1";
        } catch (Exception e) {
            //解密失败
            e.printStackTrace();
            return "2";
        }
        return "3";
    }

    public String getTrachFromServer()
    {
        Request request = new Request.Builder().url(getUrl).build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                //获取失败，可能是服务器异常
                serverIsOnline = false;
                return "0";
            } else {
                //处理
                String result = response.body().string();
                final TrachModel resultData = gson.fromJson(result, TrachModel.class);
                //解密
                byte[] tempResult = AES.Decrypt(resultData.getResponseData(), aesSecret);
                if (tempResult != null) {
//                    String result1 = new String(tempResult,"UTF-8");
                    return new String(tempResult, "UTF-8");
                }

            }
        } catch (IOException e) {
            //io读写错误
            e.printStackTrace();
            return "1";
        } catch (Exception e) {
            //解密失败
            e.printStackTrace();
            return "2";
        }

        return "3";
    }

    static class Data {
        String data;
    }



}
