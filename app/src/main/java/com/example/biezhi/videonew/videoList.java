package com.example.biezhi.videonew;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.biezhi.videonew.CustomerClass.BitmapCut;
import com.example.biezhi.videonew.CustomerClass.BitmapResize;
import com.example.biezhi.videonew.CustomerClass.SysApplication;
import com.example.biezhi.videonew.DataModel.SearchModel;
import com.example.biezhi.videonew.DataModel.VideoModel;
import com.example.biezhi.videonew.NetWorkServer.GetServer;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreGridViewContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;


public class videoList extends AppCompatActivity implements View.OnClickListener {

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
    List<SearchModel.ContentEntity> searchContentEntityList;
    Boolean has_next;
    BitmapCut bitmapCut;
    static int screenWidth;
    static int screenHeight;
    PtrClassicFrameLayout ptrFrame;
    GridViewWithHeaderAndFooter mGridView;
    LoadMoreGridViewContainer loadMoreContainer;
    BitmapResize bitmapResize;
    boolean isLoadMore = false;
    BitmapDrawable holdBD;
    Bitmap holdBM;
    ImageButton back_button;
    ImageButton search_button;
    ImageButton cache_button;
    ImageButton download_button;

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
        titleText = (TextView) findViewById(R.id.title_appName);
        back_button = (ImageButton) findViewById(R.id.title_icon);
        search_button = (ImageButton) findViewById(R.id.title_search);
        cache_button = (ImageButton) findViewById(R.id.title_cache);
        download_button = (ImageButton) findViewById(R.id.title_download);
        back_button.setOnClickListener(this);
        search_button.setOnClickListener(this);
        cache_button.setOnClickListener(this);
        download_button.setOnClickListener(this);
        sourcePage = appData.getSourcePage();
        titleText.setText(appData.getCateName());
        channelsEntityList = new ArrayList<>();
        contentEntityList = new ArrayList<>();
        bitmapCut = new BitmapCut();
        screenHeight = appData.getHeight();
        screenWidth = appData.getWidth();
        bitmapResize = new BitmapResize();
        holdBM = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.item_bg), screenHeight / 6, screenWidth * 2 / 5, false);
        holdBD = new BitmapDrawable(holdBM);
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
//        defaultAdapter gridViewAdapter = new defaultAdapter();
//        mGridView.setAdapter(gridViewAdapter);
    }

    private void initVideo() {
        //todo 请求数据，加载图片
        //http://115.29.190.54:99/Videos.aspx?page=0&cat=26&size=30&order=2&district=0&kind=0&appid=74&version=1.0
        //dd358748fcabdda1
        new Thread(new getVideoList()).start();
    }

    @Override
    public void onClick(View v) {
        if (v == back_button) {
            appData.setSourcePage("VideoList");
            startActivity(new Intent(videoList.this, defaultActivity.class));
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Glide.get(videoList.this).clearMemory();
        if (mGridView.getAdapter() != null) {
            mGridView.setAdapter(null);
        }
    }


    protected class getVideoList implements Runnable {
        @Override
        public void run() {
            GetServer getServer = new GetServer();
            if (appData.sourcePage == "Search") {
                getServer.getUrl = "http://115.29.190.54:99/search.aspx?keyword=" + appData.getSearchVideo()
                        + "&page=" + currentPageNum + "&size=" + defaultPageSize + "&appid=" + defaultAppid + "&version=" + defaultVersion;
            } else {
                getServer.getUrl = "http://www.biezhi360.cn:99/Videos.aspx?page=" + currentPageNum + "&cat=" +
                        appData.getClickedCateId() + "&size=" + defaultPageSize + "&order=" + defaultOrder +
                        "&district=" + defaultDistrict + "&kind=" + defaultKind + "&appid=" + defaultAppid + "&version=" + defaultVersion;
            }
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
                if (appData.sourcePage == "Search") {
                    SearchModel searchModel = gson.fromJson(json, SearchModel.class);
                    if (isLoadMore) {
                        searchContentEntityList.addAll(searchModel.getContent());
                    } else {
                        searchContentEntityList = searchModel.getContent();
                    }
                    has_next = Boolean.valueOf(searchModel.getHas_next());
                } else {
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
                }
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
                // TODO: 16/3/2 图片错位 解决
                ptrFrame.refreshComplete();
                loadMoreContainer.loadMoreFinish(false, has_next);
                if (isLoadMore) {
                    mGridView.setSelection((currentPageNum - 1) * 30 - 3);
                } else {
                    mGridView.setSelection(0);
                }
            }
        }
    };

    private class GridViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (appData.sourcePage == "Search") {
                return searchContentEntityList.size();
            } else {
                return contentEntityList.size();
            }
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

            ImageView imageView = ViewHolder.get(convertView, R.id.cate_image);
            TextView textView = ViewHolder.get(convertView, R.id.cate_name);

            if (appData.sourcePage == "Search") {
                Glide.with(videoList.this)
                        .load(searchContentEntityList.get(position).getCover())
                        .override(screenHeight / 6, screenWidth * 2 / 5)
                        .centerCrop()
                        .placeholder(holdBD)
                        .crossFade(100)
                        .error(holdBD)
                        .into(imageView);
                textView.setText(searchContentEntityList.get(position).getName());
                //添加ImageView的点击事件
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //准备跳转内容和数据
                        appData.setClickedVideoID(String.valueOf(searchContentEntityList.get(position).getId()));
                        appData.setVideoCover(String.valueOf(searchContentEntityList.get(position).getCover()));
                        appData.setAppId("43");
                        appData.setVersion("6.0");
                        appData.setSourcePage("VideoList");
                        startActivity(new Intent(videoList.this, videoPlay.class));
//                        finish();
                    }
                });
            } else {
                Glide.with(videoList.this)
                        .load(contentEntityList.get(position).getCover())
                        .override(screenHeight / 6, screenWidth * 2 / 5)
                        .centerCrop()
                        .placeholder(holdBD)
                        .crossFade(100)
                        .error(holdBD)
                        .into(imageView);
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
                        appData.setSourcePage("VideoList");
                        startActivity(new Intent(videoList.this, videoPlay.class));
//                        finish();
                    }
                });
            }
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
            startActivity(new Intent(videoList.this, defaultActivity.class));
            finish();
        }
        return false;

    }


}
