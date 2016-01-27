package com.example.biezhi.videonew.CustomerClass;

/**
 * Created by biezhi on 2016/1/26.
 */

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapResize {
    public Bitmap setBitmapSize(Bitmap bm, int w, int h) {
        Bitmap bitmapOrg = bm;
        int _width = bm.getWidth();
        int _height = bm.getHeight();
        int newW = w;
        int newH = h;
        float scaleWidth = ((float) newH) / _width;
        float scaleHeight = ((float) newW) / _height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, _width, _height, matrix, true);
        return resizeBitmap;
    }
}
