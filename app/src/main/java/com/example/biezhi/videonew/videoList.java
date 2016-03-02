package com.example.biezhi.videonew;

import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.biao.pulltorefresh.PtrLayout;
import com.example.biezhi.videonew.CustomerClass.BitmapCut;
import com.example.biezhi.videonew.CustomerClass.SysApplication;
import com.example.biezhi.videonew.DataModel.VideoModel;
import com.example.biezhi.videonew.NetWorkServer.GetServer;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
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
    int currentLoadedImage = 0;
    int viewTag = 0;
    int imageWidth = 0;
    int imageHeight = 0;
    List<VideoModel.ChannelsEntity> channelsEntityList;
    List<VideoModel.ContentEntity> contentEntityList;
    Boolean has_next;
    BitmapCut bitmapCut;
    static int screenWidth;
    static int screenHeight;

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
        titleText = (TextView) findViewById(R.id.title_text);
        sourcePage = appData.getSourcePage();
        titleText.setText(appData.getCateName());
        channelsEntityList = new ArrayList<>();
        contentEntityList = new ArrayList<>();
        bitmapCut = new BitmapCut();
        screenHeight = appData.getHeight();
        screenWidth = appData.getWidth();

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
            if (json.length() < 10) {
                //网络请求异常或者其他错误
                switch (json) {
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

            } else {
                final Gson gson = new Gson();
                VideoModel videoModel = gson.fromJson(json, VideoModel.class);
                //获取所有的list
                channelsEntityList.addAll(videoModel.getChannels());
                contentEntityList.addAll(videoModel.getContent());
                has_next = Boolean.valueOf(videoModel.getHas_next());
                Message message = Message.obtain();
                message.what = 1;
                //通知准备修改ui
                getListOk.sendMessage(message);
            }
        }
    }

    private Handler getListOk = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //想将所有的video加上默认的背景
                //准备异步加载图片
                final GridViewAdapter gridViewAdapter = new GridViewAdapter();
                gridView.setAdapter(gridViewAdapter);
                //先将所有的名字和页面加载出来，然后在来加载videoBitmap 解决
                //然后重新异步加载所有的图片 解决
                //添加菊花动画 解决
                // TODO: 16/3/2 图片错位 已解决

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
            return BitmapFactory.decodeResource(getResources(), R.drawable.item_bg);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.cate_adpter, parent, false);
            }

            final ImageView imageView = ViewHolder.get(convertView, R.id.cate_image);
            TextView textView = ViewHolder.get(convertView, R.id.cate_name);
            final ProgressBar progressBar = ViewHolder.get(convertView, R.id.lodingProgressBar);
            textView.setText(contentEntityList.get(position).getName());
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.drawable.item_bg)
                    .showImageOnLoading(R.drawable.item_bg)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageLoader.getInstance().displayImage(contentEntityList.get(position).getCover(), imageView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.INVISIBLE);
                    loadedImage = bitmapCut.setBitmapSize(loadedImage, screenHeight / 7, screenWidth / 3);
                    imageView.setImageBitmap(loadedImage);
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }


}
