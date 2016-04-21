package com.example.biezhi.videonew;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biezhi.videonew.CustomerClass.BitmapResize;

import com.example.biezhi.videonew.CustomerClass.CommentFragment;
import com.example.biezhi.videonew.CustomerClass.EpisodeListFragment;
import com.example.biezhi.videonew.CustomerClass.GetPlayUrl;
import com.example.biezhi.videonew.DataModel.EpisodeModel;
import com.example.biezhi.videonew.DataModel.SourceModel;
import com.example.biezhi.videonew.DataModel.VideoInfoModel;
import com.example.biezhi.videonew.MessageBox.AfterUrlMessage;
import com.example.biezhi.videonew.MessageBox.EpisodeMessage;
import com.example.biezhi.videonew.MessageBox.PlayUrlMessage;
import com.example.biezhi.videonew.MessageBox.SeekBarChangeMessage;
import com.example.biezhi.videonew.MessageBox.SeekBarChangedMessage;
import com.example.biezhi.videonew.MessageBox.SourceMessage;
import com.example.biezhi.videonew.MessageBox.VideoSourceMessage;
import com.example.biezhi.videonew.NetWorkServer.GetServer;
import com.google.gson.Gson;
import com.rey.material.widget.TabPageIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;



/*
* vip判断
* */

public class videoPlay extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener
        , MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, View.OnClickListener, AdapterView.OnItemClickListener, EpisodeListFragment.OnEpisodeClickListener {
    private VideoView videoView;
    //    private TextView videoSourceTv;
//    private ImageButton videoDownloadBt;
//    private ImageButton videoFavoriteBt;
    Data appData;
    private List<SourceModel.DataEntity> sourceDataLs;
    private int sourceCount;
    //    private RelativeLayout videoSourceRl;
    private int[] bitmapResource;
    private int[] sourceClicked;
    private BitmapResize bitmapResize = new BitmapResize();
    private ProgressBar mProgressBar;
    private int[] sourceButtonsId;
    private String[] adapterKeys;
    private int[] adapterIds;
    //    private ImageButton titleDownloadIb;
    private ImageButton titleBackIb;
    private TextView titleNameTv;

    /*videoview所用变量*/
    /**
     * 是否需要暂停
     */
    private boolean needResume = false;


    /**
     * 是否处于全屏状态
     */
//    private boolean isFullScreen = false;


    /**
     * 屏幕的宽度和高度
     */
    private int screenWidth, screenHeight;


    /**
     * appId
     */
    private String appId;

    /**
     * appVersion
     */
    private String appVersion;

    /**
     * 当前选中的Video
     */
    private String currentVideo;

    /**
     * 保存了当前video的信息
     */
    private List<VideoInfoModel.ContentEntity> contentEntity;

    /**
     * video的简介
     */
    private String videoIntro;

    /**
     * video的CateId
     */
//    private String videoCateId;

    /**
     * episode信息
     */
    private List<EpisodeModel.ContentEntity> episodeContent;

    /**
     * 视频是否是vip
     */
    private boolean isVipVideo;

    /**
     * 视频的episodeId
     */
//    private String episodeId;

    /**
     * 当前视频的播放地址
     */
//    private String currentVideoPlayUrl;

    private String videoSiteId;

    /**
     * 解析播放地址
     */
    private GetPlayUrl getPlayUrl;

    /**
     * 当前的episode，就是第几集
     */
    private int episodeNum;

    private String videoQuality;

    private String path = "";

    private boolean userIsVip;

    /**
     * 全屏按钮
     */
    private ImageButton fullScreenButton;

    /**
     * 播放按钮
     */
    private ImageButton videoPlayOrPauseButton;

    /**
     * 进度条
     */
    private SeekBar video_seekBar;

    /**
     * 控制栏
     */

    private RelativeLayout video_control;

    /**
     * 当前播放时间
     */

    private TextView video_currentTime;

    /**
     * 总时间
     */

    private TextView video_totalTime;

    /**
     * 当前时间String
     */
    private static String videoTimeString;

    /**
     * 总时间String
     */
    private String videoTotalString;

    private boolean seekBarAutoFlag = true;

    private boolean isShow = true;

    private int currentPosition = 0;

    private final String[] episode_name = new String[]{"剧集", "简介"};


    private ArrayList<String> vipArray = new ArrayList<>();


    private ArrayList<String> episodeNameArray = new ArrayList<>();

    private ArrayList<String> episodeNumArray = new ArrayList<>();

    /**
     * 当前级数
     */
    private int currentEpisodePosition;

    /**
     * 当前视屏来源
     */
    private int currentVideoSourcePosition;

    private boolean canDownload = false;


    @Override
    protected void onStart() {
        super.onStart();
        if (appData.getExsit()) {
            finish();
        }
    }

    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(this.getApplication());
        setContentView(R.layout.activity_video_play);
//        SysApplication.getInstance().addActivity(this);
        appData = (Data) this.getApplicationContext();
        if (appData.getSourcePage() == "FullScreen") {
            path = appData.getPlayUrl();
            currentPosition = appData.getCurrentPosition();
        }
        initClass();
        initPlayer();
    }

    public void initClass() {
        EventBus.getDefault().register(this);
        bitmapResource = new int[]{R.drawable.other_on1, R.drawable.other_on2, R.drawable.other_on3, R.drawable.other_on4, R.drawable.other_on5};
        sourceClicked = new int[]{R.drawable.other_on1_clicked, R.drawable.other_on2_clicked, R.drawable.other_on3_clicked, R.drawable.other_on4_clicked, R.drawable.other_on5_clicked};
        bitmapResize = new BitmapResize();
//        videoSourceTv = (TextView) findViewById(R.id.video_sourceLabel);
//        videoDownloadBt = (ImageButton) findViewById(R.id.video_download);
//        videoDownloadBt.setVisibility(View.INVISIBLE);
//        videoFavoriteBt = (ImageButton) findViewById(R.id.video_favorate);
//        videoSourceRl = (RelativeLayout) findViewById(R.id.video_source);
        sourceButtonsId = new int[]{R.id.source_1, R.id.source_2, R.id.source_3, R.id.source_4, R.id.source_5};
        mProgressBar = (ProgressBar) findViewById(R.id.video_loadingBar);
        adapterKeys = new String[]{"vipImage", "notShow", "episodeName", "episodeNum"};
        adapterIds = new int[]{R.id.vip_imageView, R.id.not_show, R.id.episode_name, R.id.episode_num};
        fullScreenButton = (ImageButton) findViewById(R.id.fullscreen_button);
        videoPlayOrPauseButton = (ImageButton) findViewById(R.id.video_playOrPause);
        video_seekBar = (SeekBar) findViewById(R.id.video_seekBar);
        video_control = (RelativeLayout) findViewById(R.id.video_control);
        video_totalTime = (TextView) findViewById(R.id.video_totalTime);
        video_currentTime = (TextView) findViewById(R.id.video_currentTime);
        titleBackIb = (ImageButton) findViewById(R.id.title_icon);
        titleNameTv = (TextView) findViewById(R.id.title_appName);
        appId = appData.getAppid();
        appVersion = appData.getVersion();
        sourceDataLs = new ArrayList<>();
        currentVideo = appData.getClickedVideoID();
        contentEntity = new ArrayList<>();
        videoIntro = " ";
//        videoCateId = "";
        videoTimeString = "";
        videoTotalString = "";
        episodeContent = new ArrayList<>();
        isVipVideo = false;
//        episodeId = "";
//        currentVideoPlayUrl = "";
        videoSiteId = "";
        getPlayUrl = new GetPlayUrl();
        episodeNum = 1;
        videoQuality = "normal";
        currentEpisodePosition = -1;
        currentVideoSourcePosition = -1;
        userIsVip = appData.userVip;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
//        videoDownloadBt.setOnClickListener(this);
//        videoFavoriteBt.setOnClickListener(this);
        fullScreenButton.setOnClickListener(this);
        videoPlayOrPauseButton.setOnClickListener(this);
        video_seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
        titleBackIb.setOnClickListener(this);
        isShow = true;
        EventBus.getDefault().post(new SourceMessage());
        EventBus.getDefault().post(new PlayUrlMessage());

    }

    private void initPlayer() {
        videoView = (VideoView) findViewById(R.id.video_surface);
        videoView.setOnPreparedListener(this);
        videoView.setOnInfoListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnBufferingUpdateListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnSeekCompleteListener(this);
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isShow) {
                    video_control.setVisibility(View.INVISIBLE);
                    isShow = !isShow;
                } else {
                    video_control.setVisibility(View.VISIBLE);
                    isShow = !isShow;
                }
                return false;
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getVideoSource(SourceMessage sourceMessage) {
        int videoId = Integer.valueOf(appData.getClickedVideoID());
        GetServer getServer = new GetServer();
        getServer.getUrl = "http://115.29.190.54:99/MyVideo.aspx?videoid=" + videoId + "&appid=" + appId + "&version=" + appVersion;
        getServer.aesSecret = "dd358748fcabdda1";
        String json = getServer.getInfoFromServerWithNoData();
        if (json.length() < 10) {
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
            Gson gson = new Gson();
            SourceModel sourceModel = gson.fromJson(json, SourceModel.class);
            sourceDataLs.addAll(sourceModel.getData());
            sourceCount = sourceModel.getCount();
            EventBus.getDefault().post(new VideoSourceMessage(sourceModel));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSourceOK(VideoSourceMessage videoSourceMessage) {
        for (int i = 0; i < sourceCount; i++) {
            final ImageButton sourceButton = (ImageButton) findViewById(sourceButtonsId[i]);
            sourceButton.setVisibility(View.VISIBLE);
            if (i == 0) {
                sourceButton.setImageResource(R.drawable.other_on1_clicked);
            }
            final int finalI = i;
            sourceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalI != currentVideoSourcePosition) {
                        changeSource(finalI);
                        sourceButton.setImageResource(sourceClicked[finalI]);
                        //遍历所有button,重置未选中按钮
                        for (int loop = 0; loop < sourceCount; loop++) {
                            if (loop != finalI) {
                                ImageButton unClickedButton = (ImageButton) findViewById(sourceButtonsId[loop]);
                                unClickedButton.setImageResource(bitmapResource[loop]);
                            }
                        }

                    }

                }
            });
        }
    }

    private void changeSource(int sourceId) {
        if (sourceId + 1 > sourceCount) {
            //说明瞎比点
            Toast.makeText(videoPlay.this, "该来源视频无法播放，请尝试其他来源！", Toast.LENGTH_LONG).show();
        } else {
            //清空原来的剧集表
            if (episodeContent.size() != 0) {
                episodeContent.clear();
            }
            currentVideoSourcePosition = sourceId;
            episodeNum = 1;
            currentVideo = String.valueOf(sourceDataLs.get(sourceId).getVideoId());
            //请求播放地址
            EventBus.getDefault().post(new PlayUrlMessage());
        }


    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getPlayUrl(PlayUrlMessage playUrlMessage) {
        GetServer getServer = new GetServer();
        getServer.getUrl = "http://115.29.190.54:99/Video.aspx?videoid=" + currentVideo + "&appid=" + appId + "&version=" + appVersion;
        getServer.aesSecret = "dd358748fcabdda1";
        String json = getServer.getInfoFromServer();
        if (json.length() < 10) {
            switch (json) {
                case "0":
                    break;
                case "1":
                    break;
                case "2":
                    break;
                default:
                    break;
            }
        } else {
            Gson gson = new Gson();
            VideoInfoModel videoInfoModel = gson.fromJson(json, VideoInfoModel.class);
            contentEntity.addAll(videoInfoModel.getContent());
//            videoCateId = String.valueOf(videoInfoModel.getCateId());
            videoIntro = videoInfoModel.getIntro();
            videoSiteId = String.valueOf(contentEntity.get(0).getId());
            getServer.getUrl = "http://115.29.190.54:99/Episode.aspx?videoid=" + currentVideo + "&siteid=" + contentEntity.get(0).getId() + "&appid=" + appId + "&version=" + appVersion;
            json = getServer.getInfoFromServer();
            if (json.length() < 10) {
                switch (json) {
                    case "0":
                        break;
                    case "1":
                        break;
                    case "2":
                        break;
                    default:
                        break;
                }
            } else {
                EpisodeModel episodeModel = gson.fromJson(json, EpisodeModel.class);
                canDownload = episodeModel.isCanDownload();
                episodeContent.addAll(episodeModel.getContent());
                videoSiteId = String.valueOf(episodeModel.getSiteId());
                isVipVideo = episodeContent.get(episodeNum - 1).isVIP();
                //getPlayUrl.episode就是1 2 3 4 5 集
                getPlayUrl.setValue(Integer.parseInt(videoSiteId), episodeNum, screenWidth, screenHeight);
                getPlayUrl.ua = "iPhone";
                getPlayUrl.originPlayUrl = episodeContent.get(episodeNum - 1).getPlayUrl();
                getPlayUrl.quality = videoQuality;
                path = getPlayUrl.getUrl();
                EventBus.getDefault().post(new EpisodeMessage(episodeModel));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayUrlOK(EpisodeMessage episodeMessage) {
        getData();
        initFragment();
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
        videoView.setVideoURI(Uri.parse(path));
        mProgressBar.setVisibility(View.VISIBLE);
        //是否显示下载按钮
//        if (canDownload) {
//            videoDownloadBt.setVisibility(View.INVISIBLE);
//        } else {
//            videoDownloadBt.setVisibility(View.INVISIBLE);
//        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getAfterUrl(AfterUrlMessage afterUrlMessage) {
        isVipVideo = episodeContent.get(episodeNum - 1).isVIP();
        getPlayUrl.setValue(Integer.parseInt(videoSiteId), episodeNum, screenWidth, screenHeight);
        getPlayUrl.ua = "iPhone";
        getPlayUrl.originPlayUrl = episodeContent.get(episodeNum - 1).getPlayUrl();
        getPlayUrl.quality = videoQuality;
        path = getPlayUrl.getUrl();
        if (path != "") {
            EventBus.getDefault().post(new EpisodeMessage(null));
        }
    }

    /**
     * 滑动条变化线程
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void changeSeekBarMessage(SeekBarChangeMessage seek) {
        while (seekBarAutoFlag) {
            if (videoPlay.this.videoView != null && videoPlay.this.videoView.isPlaying()) {
//                video_seekBar.setProgress((int) videoView.getCurrentPosition());
                //发送一个消息
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                } finally {
                    EventBus.getDefault().post(new SeekBarChangedMessage());
                }
//                EventBus.getDefault().post(new SeekBarChangedMessage());

            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changSeekBar(SeekBarChangedMessage seek) {
        video_seekBar.setProgress((int) videoView.getCurrentPosition());
    }


//    private Runnable changeSeekBar = new Runnable() {
//        @Override
//        public void run() {
//
//            try {
//                while (seekBarAutoFlag) {
//                    if (null != videoPlay.this.videoView
//                            && videoPlay.this.videoView.isPlaying()) {
//                        video_seekBar.setProgress((int) videoView.getCurrentPosition());
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };


    private void initFragment() {
        FragmentPagerAdapter adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        //实例化TabPageIndicator然后设置ViewPager与之关联
        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        //如果我们要对ViewPager设置监听，用indicator设置就行了
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                Toast.makeText(getApplicationContext(), episode_name[arg0], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }


    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // TODO Auto-generated method stub
            if (progress >= 0) {
                // 如果是用户手动拖动控件，则设置视频跳转。
                if (fromUser) {
                    videoView.seekTo(progress);
                }
                // 设置当前播放时间
                video_currentTime.setText(getShowTime(progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private String getShowTime(int progress) {
        if (progress <= 0) {
            return "00:00";
        }
        int second = progress / 1000;
        int minute = second / 60;
        int hour = minute / 60;
        int lastSecond = second % 60;
        int lastMinute = minute % 60;
        int lastHour = hour;
        String timeString = String.format("%02d:", lastHour) + String.format("%02d:", lastMinute) + String.format("%02d", lastSecond);
        return timeString;

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name))
                    return getApplicationContext().getSystemService(name);
                return super.getSystemService(name);
            }
        });
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onClick(View v) {
//        if (v == videoDownloadBt) {
//            addToDownload();
//        }
//        if (v == videoFavoriteBt) {
//            addToFavorite();
//        }
        if (v == fullScreenButton) {
            gotoFullScreen();
        }
        if (v == titleBackIb) {
            backToCateList();
        }
        if (v == videoPlayOrPauseButton) {
            if (videoView != null) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    videoPlayOrPauseButton.setImageResource(R.drawable.video_play);
                } else {
                    if (isVipVideo) {
                        if (userIsVip && appData.getUserName() != "") {
                            videoView.start();
                        } else if (!userIsVip && appData.getUserName() != "") {
                            //通知用户添加微信
                            videoView.pause();
                            if (appData.getSourcePage() != "AddWeiXin") {
                                startActivity(new Intent(videoPlay.this, addWeiXinActivity.class));
                            }
                            appData.setSourcePage("VideoPlay");
                        } else {
                            //提示登录
                            dialog();
                            videoView.pause();
                        }
                    } else {
                        //投放广告
                        videoView.start();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                    videoPlayOrPauseButton.setImageResource(R.drawable.video_pause);
                }
            }
        }

    }

    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        if (KeyCode == KeyEvent.KEYCODE_BACK) {
            if (appData.getSourcePage() == "FullScreen") {
                if (!appData.getFromSearch()) {
                    appData.setSourcePage("VideoPlay");
                    startActivity(new Intent(videoPlay.this, videoList.class));
                    finish();
                } else {
                    appData.setSourcePage("Search");
                    startActivity(new Intent(videoPlay.this, videoList.class));
                    finish();
                }
            } else if (appData.getSourcePage() == "Search") {
                appData.setSourcePage("Search");
                startActivity(new Intent(videoPlay.this, videoList.class));
                finish();
            } else if (appData.getSourcePage() == "Download") {
                appData.setSourcePage("VideoPlay");
                startActivity(new Intent(videoPlay.this, downloadActivity.class));
                finish();
            } else {
                appData.setSourcePage("VideoPlay");
//                startActivity(new Intent(videoPlay.this, videoList.class));
                finish();
//                onFinish();
            }
        }
        return false;

    }

    public void onFinish()
    {
        finish();
        videoView.setOnPreparedListener(null);
        videoView.setOnInfoListener(null);
        videoView.setOnErrorListener(null);
        videoView.setOnBufferingUpdateListener(null);
        videoView.setOnCompletionListener(null);
        videoView.setOnSeekCompleteListener(null);
        videoView.setOnTouchListener(null);
        //移除seekbar
        video_seekBar = null;
        //progressbar
        mProgressBar = null;
        videoPlayOrPauseButton.setOnClickListener(null);
        videoPlayOrPauseButton = null;
        titleBackIb.setOnClickListener(null);
        titleBackIb = null;
        titleNameTv = null;
        episodeContent.clear();
        episodeContent = null;
        episodeNumArray.clear();
        episodeNumArray = null;
        sourceDataLs.clear();
        sourceDataLs = null;
        contentEntity.clear();
        contentEntity = null;
        fullScreenButton.setOnClickListener(null);
        fullScreenButton = null;
        video_control = null;
        video_currentTime = null;
        video_totalTime = null;
        vipArray.clear();
        vipArray = null;
        EventBus.getDefault().unregister(this);
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
        seekBarAutoFlag = false;
        setContentView(R.layout.view_null);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void OnEpisodeClickListener(int episodeSelect) {
        episodeNum = episodeSelect + 1;
        //异步解析videoUrl
        EventBus.getDefault().post(new AfterUrlMessage());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                //开始缓存，暂停播放
                if (mp.isPlaying()) {
                    mp.pause();
                    needResume = true;
                }
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                //缓存完成，继续播放
                if (needResume)
                    mp.start();
                mProgressBar.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                //显示 下载速度
                Log.e("loadspeed", extra + "");
                break;
        }
        return true;
    }

    /**
     * listitem点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //重新获取playUrl
        episodeNum = position + 1;
        //异步解析videoUrl
        EventBus.getDefault().post(new AfterUrlMessage());
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        videoTotalString = getShowTime((int) videoView.getDuration());
        video_totalTime.setText(videoTotalString);
        video_currentTime.setText(getShowTime(0));
        video_seekBar.setMax((int) videoView.getDuration());
        // TODO: 16/4/19 使用eventbus重写
        EventBus.getDefault().post(new SeekBarChangeMessage());
//        new Thread(changeSeekBar).start();
        if (isVipVideo) {
            if (userIsVip && appData.getUserName() != "") {
                videoView.start();
            } else if (!userIsVip && appData.getUserName() != "") {
                //通知用户添加微信
                videoView.pause();
                if (appData.getSourcePage() != "AddWeiXin") {
                    startActivity(new Intent(videoPlay.this, addWeiXinActivity.class));
                }
                appData.setSourcePage("VideoPlay");
            } else {
                //提示登录
                dialog();
                videoView.pause();
            }
        } else {
            //投放广告
            videoView.start();
            mProgressBar.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public void onSeekComplete(MediaPlayer mp) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //移除所有监听
        videoView.setOnPreparedListener(null);
        videoView.setOnInfoListener(null);
        videoView.setOnErrorListener(null);
        videoView.setOnBufferingUpdateListener(null);
        videoView.setOnCompletionListener(null);
        videoView.setOnSeekCompleteListener(null);
        videoView.setOnTouchListener(null);
        //移除seekbar
        video_seekBar = null;
        //progressbar
        mProgressBar = null;
        videoPlayOrPauseButton.setOnClickListener(null);
        videoPlayOrPauseButton = null;
        titleBackIb.setOnClickListener(null);
        titleBackIb = null;
        titleNameTv = null;
        episodeContent.clear();
        episodeContent = null;
        episodeNumArray.clear();
        episodeNumArray = null;
        sourceDataLs.clear();
        sourceDataLs = null;
        contentEntity.clear();
        contentEntity = null;
        fullScreenButton.setOnClickListener(null);
        fullScreenButton = null;
        video_control = null;
        video_currentTime = null;
        video_totalTime = null;
        vipArray.clear();
        vipArray = null;
        EventBus.getDefault().unregister(this);
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
        seekBarAutoFlag = false;
        setContentView(R.layout.view_null);

//        RefWatcher refWatcher = Data.getRefWatcher(this);
//        refWatcher.watch(this);
    }

    private void backToCateList() {
        if (appData.getSourcePage() == "FullScreen") {
            if (!appData.getFromSearch()) {
                appData.setSourcePage("VideoPlay");
                finish();
            } else {
                appData.setSourcePage("Search");
                startActivity(new Intent(videoPlay.this, videoList.class));
                finish();
            }
        } else if (appData.getSourcePage() == "VideoList") {
            if (!appData.getFromSearch()) {
                appData.setSourcePage("VideoPlay");
                finish();
            } else {
                appData.setSourcePage("Search");
                startActivity(new Intent(videoPlay.this, videoList.class));
                finish();
            }
        } else if (appData.getSourcePage() == "Download") {
            appData.setSourcePage("VideoPlay");
            startActivity(new Intent(videoPlay.this, downloadActivity.class));
            finish();
        } else {
            finish();
        }
    }

    private void addToDownload() {
        //path就是播放地址
//        DownloadManager.getInstance().startDownload(
//                url, label,
//                "/sdcard/xUtils/" + label + ".aar", true, false, null);
//
        //开启一个新的选择界面，包含所有的下载选择框

        ArrayList<String> pathList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();
        int episodeCount = 0;
        for (int i = 0; i < episodeContent.size(); i++) {
            //这个playUrl是要重新解析的
            pathList.add(episodeContent.get(i).getPlayUrl());
            nameList.add(episodeContent.get(i).getName());
        }
        episodeCount = episodeContent.size();

        Intent intent = new Intent();
        intent.setClass(this.getApplication(), downloadEpisodeActivity.class);
        intent.putStringArrayListExtra("downloadUrls", pathList);
        intent.putExtra("episodeCount", episodeCount);
        intent.putExtra("videoName", contentEntity.get(0).getName());
        intent.putExtra("siteId", Integer.parseInt(videoSiteId));
        intent.putStringArrayListExtra("episodeNameList", nameList);
        startActivity(intent);


    }

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("现在就去登陆？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //跳转到登陆界面
                startActivity(new Intent(videoPlay.this, loginActivity.class));
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void addToFavorite() {
        //写入本地缓存

    }


    private void gotoFullScreen() {

        if (videoView.isPlaying()) {
            videoView.pause();
            currentPosition = (int) videoView.getCurrentPosition();
            videoView.stopPlayback();
        }
        //准备数据
        //当前播放地址 path
        //当前进度 currentPosition
        //当前视频的标题 episodeContent.get(episodeNum).getName();
        //当前视频是否是vip episodeContent.get(episodeNum).isVIP();
        appData.setVideoVip(isVipVideo);
        appData.setPlayUrl(path);
        appData.setCurrentPosition(currentPosition);
        appData.setVideoName(episodeContent.get(episodeNum - 1).getName());
//        isFullScreen = true;
        //把当前视频的videoId以及EpisodeId对应存起来
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < episodeContent.size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put("episodeId", episodeContent.get(i).getId() + "");
            map.put("videoId", currentVideo);
            list.add(map);
        }
        appData.setVideoEpisode(list);
        startActivity(new Intent(videoPlay.this, fullScreenPlay.class));
        finish();
    }

    private List<HashMap<String, Object>> getData() {
        //先清除
        vipArray.clear();
        episodeNameArray.clear();
        episodeNumArray.clear();
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < episodeContent.size(); i++) {
            if (episodeContent.get(i).isVIP()) {
                vipArray.add("true");
            } else {
                vipArray.add("false");
            }
            episodeNameArray.add(episodeContent.get(i).getName());
            episodeNumArray.add("第" + episodeContent.get(i).getEpisode() + "集");
        }
        return list;
    }

    /**
     * ViewPager适配器
     */
    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                Fragment fragment = new EpisodeListFragment();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("vipArray", vipArray);
                bundle.putStringArrayList("nameArray", episodeNameArray);
                bundle.putStringArrayList("numArray", episodeNumArray);
                bundle.putInt("currentEpisode", currentEpisodePosition);
                fragment.setArguments(bundle);
                return fragment;
            } else {
                Fragment fragment = new CommentFragment();
                Bundle bundle = new Bundle();
                bundle.putString("comment", videoIntro);
                fragment.setArguments(bundle);
                return fragment;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return episode_name[position % episode_name.length];
        }

        @Override
        public int getCount() {
            return episode_name.length;
        }
    }

}