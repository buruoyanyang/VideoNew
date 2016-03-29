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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.biezhi.videonew.CustomerClass.BitmapCut;
import com.example.biezhi.videonew.CustomerClass.BitmapResize;
import com.example.biezhi.videonew.CustomerClass.SysApplication;
import com.example.biezhi.videonew.DataModel.VideoModel;
import com.example.biezhi.videonew.NetWorkServer.GetServer;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreGridViewContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;


public class videoList extends AppCompatActivity {

    private LayoutInflater inflater;
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
    BitmapCut bitmapCut;
    static int screenWidth;
    static int screenHeight;
    PtrClassicFrameLayout ptrFrame;
    GridViewWithHeaderAndFooter mGridView;
    LoadMoreGridViewContainer loadMoreContainer;
    BitmapResize bitmapResize;
    boolean isLoadMore = false;

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
        mGridView = (GridViewWithHeaderAndFooter) findViewById(R.id.load_more_grid_view);
        titleText = (TextView) findViewById(R.id.title_text);
        sourcePage = appData.getSourcePage();
        titleText.setText(appData.getCateName());
        channelsEntityList = new ArrayList<>();
        contentEntityList = new ArrayList<>();
        bitmapCut = new BitmapCut();
        screenHeight = appData.getHeight();
        screenWidth = appData.getWidth();
        bitmapResize = new BitmapResize();
        ptrFrame = (PtrClassicFrameLayout) findViewById(R.id.ptr_refresh);
        ptrFrame.setLastUpdateTimeRelateObject(this);
        ptrFrame.disableWhenHorizontalMove(false);
        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //异步请求网络
                        isLoadMore = false;
                        currentPageNum = 1;
                        new Thread(new getVideoList()).start();
                    }
                }, 1000);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mGridView, header);
            }
        });
        loadMoreContainer = (LoadMoreGridViewContainer) findViewById(R.id.load_more_grid_view_container);
        loadMoreContainer.setAutoLoadMore(true);
        loadMoreContainer.useDefaultHeader();
        loadMoreContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                //处理加载更多函数
                isLoadMore = true;
                currentPageNum++;
                new Thread(new getVideoList()).start();
            }
        });
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
                if (isLoadMore) {
                    channelsEntityList.addAll(videoModel.getChannels());
                    contentEntityList.addAll(videoModel.getContent());

                } else {
                    channelsEntityList = videoModel.getChannels();
                    contentEntityList = videoModel.getContent();
                }
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
                mGridView.setAdapter(gridViewAdapter);
                //先将所有的名字和页面加载出来，然后在来加载videoBitmap 解决
                //然后重新异步加载所有的图片 解决
                //添加菊花动画 解决
                // TODO: 16/3/2 图片错位 未解决
                ptrFrame.refreshComplete();
                loadMoreContainer.loadMoreFinish(false, has_next);
                mGridView.setSelection(currentPageNum * 20);
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
//            final ProgressBar progressBar = ViewHolder.get(convertView, R.id.lodingProgressBar);
            textView.setText(contentEntityList.get(position).getName());
            //添加ImageView的点击事件
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //准备跳转内容和数据
                    appData.setClickedVideoID(String.valueOf(contentEntityList.get(position).getId()));
                    appData.setVideoCover(String.valueOf(contentEntityList.get(position).getCover()));
                    appData.setAppId("43");
                    appData.setVersion("6.0");
                    startActivity(new Intent(videoList.this, videoPlay.class));
//                    startActivity(new Intent(videoList.this, videoInfo.class));
//                    startActivity(new Intent(videoList.this, videoTest.class));

                }
            });
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageLoader.getInstance().displayImage(contentEntityList.get(position).getCover(), imageView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    progressBar.setVisibility(View.INVISIBLE);
                    loadedImage = bitmapCut.setBitmapSize(loadedImage, screenHeight / 7, screenWidth / 3);
                    imageView.setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    imageView.setImageBitmap(bitmapCut.setBitmapSize(BitmapFactory.decodeResource(getResources(), R.drawable.item_bg), screenHeight / 7, screenWidth / 3));
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

    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        if (KeyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(videoList.this,cateList.class));
        }
        return false;

    }



}
