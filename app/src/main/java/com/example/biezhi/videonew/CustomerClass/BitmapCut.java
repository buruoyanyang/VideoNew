package com.example.biezhi.videonew.CustomerClass;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by biezhi on 2016/1/26.
 */
public class BitmapCut {

    public Bitmap setBitmapSize(Bitmap bm, int widthNew, int heightNew) {
        int widthOrg = bm.getWidth();
        int heightOrg = bm.getHeight();
        int scaleWidth = (widthOrg - widthNew) / 2;
        int scaleHeight = (heightOrg - heightNew) / 2;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        //简单的裁剪方法
        return Bitmap.createBitmap(bm, scaleWidth, scaleHeight, widthNew, heightNew);
//        return Bitmap.createBitmap(bm, scaleWidth, scaleHeight, widthNew, heightNew, matrix, true);

    }
}
