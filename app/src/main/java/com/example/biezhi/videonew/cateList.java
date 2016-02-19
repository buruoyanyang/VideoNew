package com.example.biezhi.videonew;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;

import com.example.biezhi.videonew.CustomerClass.SysApplication;
import com.example.biezhi.videonew.CustomerView.itemVIew;

import java.util.ArrayList;
import java.util.List;


/**
 * 频道页面
 */
public class cateList extends AppCompatActivity {

    ScrollView scrollView;
    ImageButton searchButton;
    ImageButton downloadButton;
    GridLayout cateGrid;
    Data appData;
    //跳转页面
    private String sourcePage;
    //屏幕大小
    int width;
    int height;
    //分类图片
    List<Bitmap> bitmapList = new ArrayList<>();
    //分类名
    List<String> nameList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cate_list);
        initClass();
        initCate();
    }

    /**
     * 其他初始化
     */
    private void initClass() {
        appData = (Data) this.getApplicationContext();
        SysApplication.getInstance().addActivity(this);
        bitmapList = appData.getBitmapList();
        nameList = appData.getNameList();
        width = appData.getWidth();
        height = appData.getHeight();
        sourcePage = appData.getSourcePage();
        scrollView = (ScrollView) findViewById(R.id.scrollViewCate);
        searchButton = (ImageButton) findViewById(R.id.searchButton);
        downloadButton = (ImageButton) findViewById(R.id.downloadButton);
//        cateGrid = (GridLayout) findViewById(R.id.cateGrid);
    }

    /**
     * 初始化cate
     */
    private void initCate() {
        //计算有多少需要被初始化的频道
        //临时变量
        //为什么显示出来呢?
//        int rows = bitmapList.size() / 3 + 1;
//        cateGrid.setRowCount(rows);
//        cateGrid.setColumnCount(3);
//        int tempLoop = 0;
//        for (Bitmap bm : bitmapList) {
//            itemVIew item = new itemVIew(this);
//            item.setImageBitmap(bm);
//            item.setTitleText(nameList.get(tempLoop++));
//            item.setPadding(3, 3, 3, 3);
//            GridLayout.Spec rowSpec = GridLayout.spec(tempLoop / 3);
//            GridLayout.Spec columnSpec = GridLayout.spec(tempLoop % 3);
//            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
//            params.setGravity(Gravity.FILL);
//            cateGrid.addView(item, params);
//        }



    }
}
