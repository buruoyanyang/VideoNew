package com.example.biezhi.videonew.CustomerClass;

import org.bouncycastle.util.encoders.Base64;

/**
 * Created by xhf.
 * BiezhiVideo
 * 2015/11/5 13:27
 */


public class Base64Get {
    public String getUrl(String sSrc)
    {
        String tempString = "";
        try {
            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
            tempString = new String(encrypted1, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return tempString;

    }
}
