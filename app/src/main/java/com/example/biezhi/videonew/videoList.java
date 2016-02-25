package com.example.biezhi.videonew;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.biao.pulltorefresh.PtrLayout;
import com.example.biezhi.videonew.CustomerClass.SysApplication;

public class videoList extends AppCompatActivity {

    private LayoutInflater inflater;
    GridView gridView;
    PtrLayout ptrLayout;
    Data appData;
    private String sourcePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        initClass();
        initVideo();

    }

    private void initClass()
    {
        appData = (Data) this.getApplicationContext();
        SysApplication.getInstance().addActivity(this);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gridView = (GridView) findViewById(R.id.video_grid);
        GridViewAdapter gridViewAdapter = new GridViewAdapter();
        gridView.setAdapter(gridViewAdapter);
        ptrLayout = (PtrLayout) findViewById(R.id.main_ptr);
        sourcePage = appData.getSourcePage();
    }

    private void initVideo()
    {
        //todo 请求数据，加载图片
        //http://115.29.190.54:99/Videos.aspx?page=0&cat=26&size=30&order=2&district=0&kind=0&appid=74&version=1.0
        //dd358748fcabdda1
    }

    private class GridViewAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return 20;
        }
        @Override
        public Object getItem(int position)
        {
            return  BitmapFactory.decodeResource(getResources(), R.drawable.item_bg);
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
            holder.imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.item_bg));
            holder.textView.setText("111");
            return convertView;
        }

    }
    static class ViewHolder
    {
        ImageView imageView;
        TextView textView;
    }
}
