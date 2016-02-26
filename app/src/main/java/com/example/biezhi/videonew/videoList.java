package com.example.biezhi.videonew;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.biao.pulltorefresh.PtrLayout;
import com.example.biezhi.videonew.CustomerClass.SysApplication;
import com.example.biezhi.videonew.DataModel.VideoModel;
import com.example.biezhi.videonew.NetWorkServer.GetServer;
import com.google.gson.Gson;

import java.util.List;


public class videoList extends AppCompatActivity {

    private LayoutInflater inflater;
    GridView gridView;
    PtrLayout ptrLayout;
    Data appData;
    TextView titleText;
    private String sourcePage;
    int currentPageNum = 0;
    int defaultPageSize = 30;
    int defaultOrder = 2;
    int defaultDistrict = 0;
    int defaultKind = 0;
    int defaultAppid = 74;
    double defaultVersion = 1.0;
    List<VideoModel.ChannelsEntity> channelsEntityList;
    List<VideoModel.ContentEntity> contentEntityList;
    Boolean has_next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        initClass();
        initVideo();

    }

    private void initClass() {
        appData = (Data) this.getApplicationContext();
        SysApplication.getInstance().addActivity(this);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gridView = (GridView) findViewById(R.id.video_grid);
        ptrLayout = (PtrLayout) findViewById(R.id.main_ptr);
        titleText = (TextView)findViewById(R.id.title_text);
        titleText.setText("123");
        sourcePage = appData.getSourcePage();
    }

    private void initVideo() {
        //todo 请求数据，加载图片
        //http://115.29.190.54:99/Videos.aspx?page=0&cat=26&size=30&order=2&district=0&kind=0&appid=74&version=1.0
        //dd358748fcabdda1
        new Thread(new getVideoList()).start();

    }

    protected class getVideoList implements Runnable {
        @Override
        public void run() {
            GetServer getServer = new GetServer();
            getServer.getUrl = "http://www.biezhi360.cn:99/Videos.aspx?page=" + currentPageNum + "&cat=" +
                    appData.getClickedCateId() + "&size=" + defaultPageSize + "&order=" + defaultOrder +
                    "&district=" + defaultDistrict + "&kind=" + defaultKind + "&appid=" + defaultAppid + "&version=" + defaultVersion;
            getServer.aesSecret = "dd358748fcabdda1";
            String json = getServer.getInfoFromServer();
            if (json.length() < 10)
            {
                //网络请求异常或者其他错误
                switch (json)
                {
                    case "0":
                        //服务器连接失败
                        break;
                    case "1":
                        //io读写错误
                        break;
                    case "2":
                        //解密错误
                        break;
                    default:
                        break;
                }

            }
            else
            {
                final Gson gson = new Gson();
                VideoModel videoModel = gson.fromJson(json,VideoModel.class);
                //获取所有的list
                channelsEntityList = videoModel.getChannels();
                contentEntityList = videoModel.getContent();
                has_next = Boolean.valueOf(videoModel.getHas_next());
                Message message = Message.obtain();
                message.what = 1;
                //通知准备修改ui
                getListOk.sendMessage(message);
            }
        }
    }

    private Handler getListOk = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == 1)
            {
                //准备异步加载图片
                GridViewAdapter gridViewAdapter = new GridViewAdapter();
                gridView.setAdapter(gridViewAdapter);
                //先将所有的名字和页面加载出来，然后在来加载videoBitmap

            }
        }
    };



    private class GridViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contentEntityList.size();
        }

        @Override
        public Object getItem(int position) {
            return BitmapFactory.decodeResource(getResources(), R.drawable.item_bg_loading);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
//                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.cate_adpter,parent, false);

//                holder.imageView = (ImageView) convertView.findViewById(R.id.cate_image);
//                holder.textView = (TextView) convertView.findViewById(R.id.cate_name);
//                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//            holder.imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.item_bg_loading));
//            holder.textView.setText(contentEntityList.get(position).getName());
//            holder.textView.setText("111");
            return convertView;
        }

    }

    public class ViewHolder
    {
        @SuppressWarnings("unchecked")
        public <T extends View> T get(View view, int id)
        {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null)
            {
                viewHolder = new SparseArray<>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null)
            {
                childView = view.findViewById(id);
                viewHolder.put(id,childView);

            }
            return (T) childView;
        }
    }
    /*
    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
    */

}
