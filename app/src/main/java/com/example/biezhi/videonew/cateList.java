package com.example.biezhi.videonew;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biezhi.videonew.CustomerClass.SysApplication;
import com.example.biezhi.videonew.CustomerView.itemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 频道页面
 */
public class cateList extends AppCompatActivity {

    ImageButton searchButton;
    ImageButton downloadButton;
    GridView gridView;
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
    //分类id
    List<String> cateIdList = new ArrayList<>();

    private LayoutInflater inflater;

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
        cateIdList = appData.getCateIdList();
        searchButton = (ImageButton) findViewById(R.id.searchButton);
        downloadButton = (ImageButton) findViewById(R.id.downloadButton);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gridView = (GridView) findViewById(R.id.cate_list);
    }

    /**
     * 初始化cate
     */
    private void initCate() {
        //计算有多少需要被初始化的频道
        GridViewAdapter gridViewAdapter = new GridViewAdapter();
        gridView.setAdapter(gridViewAdapter);

    }

    private class GridViewAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return bitmapList.size();
        }
        @Override
        public Object getItem(int position)
        {
            return  bitmapList.get(position);
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.cate_adpter,null);
                holder.imageView = (ImageView)convertView.findViewById(R.id.cate_image);
                holder.textView = (TextView)convertView.findViewById(R.id.cate_name);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.imageView.setImageBitmap(bitmapList.get(position));
            holder.textView.setText(nameList.get(position));
            final int postNum = position;
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加点击事件获取cateId
                    //准备跳转页面的内容
                    appData.setClickedCateId(String.valueOf(cateIdList.get(postNum)));
                    appData.setSourcePage("CateList");
                    startActivity(new Intent(cateList.this, videoList.class));
                }
            });
            return convertView;
        }

    }


    static class ViewHolder
    {
        ImageView imageView;
        TextView textView;
    }






}
























