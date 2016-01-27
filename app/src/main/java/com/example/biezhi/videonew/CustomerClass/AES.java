package com.example.biezhi.videonew.CustomerClass;

/**
 * Created by xhf.
 * BiezhiDebug
 * 2015/11/10 14:12
 */
import org.bouncycastle.util.encoders.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    public static byte[] Decrypt(String sSrc, String sKey) throws Exception {

        byte[] keyBytes = sKey.getBytes("UTF-8");
        byte[] cipherData=null;
        //AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey);
        byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
        //byte[] decodedVal = Base64.decode(sSrc.getBytes("UTF-8"));
        try {
            cipherData = cipher.doFinal(encrypted1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return cipherData;
    }
}
