package com.example.biezhi.videonew.MessageBox;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import com.example.biezhi.videonew.CustomerClass.SysApplication;
import com.example.biezhi.videonew.Data;
import com.example.biezhi.videonew.DataModel.EpisodeModel;
import com.example.biezhi.videonew.DataModel.SourceModel;
import com.example.biezhi.videonew.DataModel.VideoInfoModel;
import com.example.biezhi.videonew.NetWorkServer.GetServer;
import com.example.biezhi.videonew.R;
import com.example.biezhi.videonew.downloadActivity;
import com.example.biezhi.videonew.fullScreenPlay;
import com.example.biezhi.videonew.videoList;
import com.google.gson.Gson;
import com.rey.material.widget.TabPageIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;



/*
* vip判断
* */

public class videoPlay extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener
        , MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, View.OnClickListener, AdapterView.OnItemClickListener, EpisodeListFragment.OnEpisodeClickListener {
    private VideoView videoView;
    TextView videoSourceLabel;
    ImageButton videoDownloadButton;
    ImageButton videoFavoriteButton;
    Data appData;
    List<SourceModel.DataEntity> sourceData;
    int sourceCount;
    RelativeLayout videoSourceLayout;
    int[] bitmapResource;
    int[] sourceClicked;
    BitmapResize bitmapResize = new BitmapResize();
    ProgressBar mProgressBar;
    int[] sourceButtonsId;
    String[] adapterKeys;
    int[] adapterIds;
    ImageButton titleDownloadButton;
    ImageButton titleBackButton;
    TextView titleName;



    /**
     * 是否需要暂停
     */
    private boolean needResume = false;


    /**
     * 是否处于全屏状态
     */
    private boolean isFullScreen = false;


    /**
     * 屏幕的宽度和高度
     */
    private int screenWidth, screenHeight;


    /**
     * appId
     */
    String appId;

    /**
     * appVersion
     */
    String appVersion;

    /**
     * 当前选中的Video
     */
    String currentVideo;

    /**
     * 保存了当前video的信息
     */
    List<VideoInfoModel.ContentEntity> contentEntity;

    /**
     * video的简介
     */
    String videoIntro;

    /**
     * video的CateId
     */
    String videoCateId;

    /**
     * episode信息
     */

    List<EpisodeModel.ContentEntity> episodeContent;

    /**
     * 视频是否是vip
     */
    boolean isVipVideo;

    /**
     * 视频的episodeId
     */
    String episodeId;

    /**
     * 当前视频的播放地址
     */
    String currentVideoPlayUrl;

    String videoSiteId;

    /**
     * 解析播放地址
     */
    GetPlayUrl getPlayUrl;

    /**
     * 当前的episode，就是第几集
     */
    int episodeNum;

    String videoQuality;

    String path = "";

    boolean userIsVip;

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
    RelativeLayout video_control;

    /**
     * 当前播放时间
     */
    TextView video_currentTime;

    /**
     * 总时间
     */
    TextView video_totalTime;

    /**
     * 当前时间String
     */
    String videoTimeString;

    /**
     * 总时间String
     */
    String videoTotalString;

    private boolean seekBarAutoFlag = true;

    boolean isShow = true;

    int currentPosition = 0;
    /**
     * 线程池
     */
    ExecutorService executor;

    Thread sourceThread;

    Thread playUrlThread;

    Thread getChangeSeekBar;

    Thread afterUrlThread;

    private static final String[] episode_name = new String[]{"剧集", "简介"};

    ArrayList<String> vipArray = new ArrayList<>();

    ArrayList<String> episodeNameArray = new ArrayList<>();

    ArrayList<String> episodeNumArray = new ArrayList<>();

    /**
     * 当前级数
     */
    int currentEpisodePosition;

    /**
     * 当前视屏来源
     */
    int currentVideoSourcePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        SysApplication.getInstance().addActivity(this);
        appData = (Data) this.getApplicationContext();
        if (appData.getSourcePage() == "FullScreen") {
            path = appData.getPlayUrl();
            currentPosition = appData.getCurrentPosition();
        }
        initPlayer();
        initClass();

    }

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

    private void initClass() {
        EventBus.getDefault().register(this);
        bitmapResource = new int[]{R.drawable.other_on1, R.drawable.other_on2, R.drawable.other_on3, R.drawable.other_on4, R.drawable.other_on5};
        sourceClicked = new int[]{R.drawable.other_on1_clicked, R.drawable.other_on2_clicked, R.drawable.other_on3_clicked, R.drawable.other_on4_clicked, R.drawable.other_on5_clicked};
        bitmapResize = new BitmapResize();
        videoSourceLabel = (TextView) findViewById(R.id.video_sourceLabel);
        videoDownloadButton = (ImageButton) findViewById(R.id.video_download);
        videoFavoriteButton = (ImageButton) findViewById(R.id.video_favorate);
        videoSourceLayout = (RelativeLayout) findViewById(R.id.video_source);
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
        titleBackButton = (ImageButton) findViewById(R.id.title_icon);
        titleName = (TextView)findViewById(R.id.title_appName);
        appId = appData.getAppid();
        appVersion = appData.getVersion();
        sourceData = new ArrayList<>();
        currentVideo = appData.getClickedVideoID();
        contentEntity = new ArrayList<>();
        videoIntro = " ";
        videoCateId = "";
        videoTimeString = "";
        videoTotalString = "";
        episodeContent = new ArrayList<>();
        isVipVideo = false;
        episodeId = "";
        currentVideoPlayUrl = "";
        videoSiteId = "";
        getPlayUrl = new GetPlayUrl();
        episodeNum = 1;
        videoQuality = "normal";
        currentEpisodePosition = -1;
        currentVideoSourcePosition = -1;
//        userIsVip = appData.userVip;
        //获取播放地址
        //获取来源信息
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        videoDownloadButton.setOnClickListener(this);
//        videoCommentButton.setOnClickListener(this);
        videoFavoriteButton.setOnClickListener(this);
//        videoEpisodeButton.setOnClickListener(this);
        fullScreenButton.setOnClickListener(this);
        videoPlayOrPauseButton.setOnClickListener(this);
        video_seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
        titleBackButton.setOnClickListener(this);
        isShow = true;
        sourceThread = new Thread(new getVideoSource());
        playUrlThread = new Thread(new getPlayUrl());
        getChangeSeekBar = new Thread(changeSeekBar);
        afterUrlThread = new Thread(new getAfterUrl());
        executor = Executors.newCachedThreadPool();
        executor.execute(sourceThread);
        executor.execute(playUrlThread);
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onShowDataEvent(TestMessage testMessage)
    {
        int videoId = Integer.valueOf(appData.getClickedVideoID());
        GetServer getServer = new GetServer();
        getServer.getUrl = "http://115.29.190.54:99/MyVideo.aspx?videoid=" + videoId + "&appid=" + appId + "&version=" + appVersion;
        getServer.aesSecret = "dd358748fcabdda1";
        String json = getServer.getInfoFromServerWithNoData();
        testMessage.setMessage(json);
        EventBus.getDefault().post(testMessage);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetDataEvent(TestMessage testMessage)
    {
        titleName.setText(testMessage.getMessage());
    }



    private void initPlayer() {
//        Vitamio.initialize(videoPlay.this);
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
        isFullScreen = true;
        startActivity(new Intent(videoPlay.this, fullScreenPlay.class));
        finish();
    }

    /**
     * listview itemclick事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("123", "" + position);
        //重新获取playUrl
        episodeNum = position + 1;
        //异步解析videoUrl
//        new Thread(new getAfterUrl()).start();
        executor.execute(afterUrlThread);
    }

    @Override
    public void OnEpisodeClickListener(int episodeSelect) {
        episodeNum = episodeSelect + 1;
        //异步解析videoUrl
//        new Thread(new getAfterUrl()).start();
        executor.execute(afterUrlThread);

    }


    private class getAfterUrl implements Runnable {
        @Override
        public void run() {
            getPlayUrl.setValue(Integer.parseInt(videoSiteId), episodeNum, screenWidth, screenHeight);
            getPlayUrl.ua = "iPhone";
            getPlayUrl.originPlayUrl = episodeContent.get(episodeNum - 1).getPlayUrl();
            getPlayUrl.quality = videoQuality;
            path = getPlayUrl.getUrl();
            if (path != "") {
                Message msg = Message.obtain();
                msg.what = 3;
                playUrlOK.sendMessage(msg);
            }
        }
    }

    private class getVideoSource implements Runnable {
        @Override
        public void run() {
            int videoId = Integer.valueOf(appData.getClickedVideoID());
            //请求所有的videoId以及所有的图标
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
                sourceData.addAll(sourceModel.getData());
                sourceCount = sourceModel.getCount();
                Message msg = Message.obtain();
                msg.what = 2;
                //通知主线程修改ui
                sourceOK.sendMessage(msg);
            }

        }
    }

    private Handler sourceOK = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                for (int i = 0; i < sourceCount; i++) {

                    final ImageButton sourceButton = (ImageButton) findViewById(sourceButtonsId[i]);
                    sourceButton.setVisibility(View.VISIBLE);
                    if (i == 0) {
                        //设置当前按钮选中
                        sourceButton.setImageResource(R.drawable.other_on1_clicked);

                    }
                    final int finalI = i;
                    sourceButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (finalI != currentVideoSourcePosition) {
                                changeSource(finalI);
                                sourceButton.setImageResource(sourceClicked[finalI]);
                                //遍历所有的button
                                for (int loop = 0; loop < sourceCount; loop++) {
                                    if (loop != finalI) {
                                        //遍历所有的按钮，将非选中的按钮重置成原来的图片
                                        ImageButton unClickedButton = (ImageButton) findViewById(sourceButtonsId[loop]);
                                        unClickedButton.setImageResource(bitmapResource[loop]);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
    };

    /**
     * 修改来源
     *
     * @param sourceId 来源ID
     */
    private void changeSource(int sourceId) {
        Log.e("123", sourceId + "");
        if (sourceId + 1 > sourceCount) {
            //说明点击的来源按钮有问题
            //直接提示该来源视频无法播放
            Toast.makeText(videoPlay.this, "该来源视频无法播放，请尝试其他来源！", Toast.LENGTH_LONG).show();
        } else {
            //清空原来的剧集表
            if (episodeContent.size() != 0) {
                episodeContent.clear();
            }
            currentVideoSourcePosition = sourceId;
            episodeNum = 1;
            currentVideo = String.valueOf(sourceData.get(sourceId).getVideoId());
            executor.execute(playUrlThread);

//            videoView.stopPlayback();
//            videoView.setVideoURI(Uri.parse("http://ws.acgvideo.com/d/29/6126227-1.mp4?wsTime=1458249117&wsSecret2=17000a3262cd8c831880d0d6d1935a9b&oi=2014991661&appkey=452d3958f048c02a&or=1034170279" ));
//            //展示菊花
//            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    //请求所有的episode
    private class getPlayUrl implements Runnable {
        @Override
        public void run() {
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
                //获取所有的episode
                Gson gson = new Gson();
                VideoInfoModel videoInfoModel = gson.fromJson(json, VideoInfoModel.class);
                contentEntity.addAll(videoInfoModel.getContent());
                videoCateId = String.valueOf(videoInfoModel.getCateId());
                videoIntro = videoInfoModel.getIntro();
                videoSiteId = String.valueOf(contentEntity.get(0).getId());
                GetServer getServer1 = new GetServer();
                getServer1.getUrl = "http://115.29.190.54:99/Episode.aspx?videoid=" + currentVideo + "&siteid=" + contentEntity.get(0).getId() + "&appid=" + appId + "&version=" + appVersion;
                getServer1.aesSecret = "dd358748fcabdda1";
                String json1 = getServer1.getInfoFromServer();
                //获取完毕，直接修改UI
                if (json1.length() < 10) {
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
                    Gson gson1 = new Gson();
                    EpisodeModel episodeModel = gson1.fromJson(json1, EpisodeModel.class);
                    episodeContent.addAll(episodeModel.getContent());
                    videoSiteId = String.valueOf(episodeModel.getSiteId());
                    //getPlayUrl.episode就是1 2 3 4 5 集
                    getPlayUrl.setValue(Integer.parseInt(videoSiteId), episodeNum, screenWidth, screenHeight);
                    getPlayUrl.ua = "iPhone";
                    getPlayUrl.originPlayUrl = episodeContent.get(episodeNum - 1).getPlayUrl();
                    getPlayUrl.quality = videoQuality;
                    path = getPlayUrl.getUrl();
                    Message msg = Message.obtain();
                    msg.what = 1;
                    episodeOK.sendMessage(msg);

//                    Message msg_1 = Message.obtain();
//                    msg_1.what = 3;
//                    playUrlOK.sendMessage(msg);
                }
            }
        }
    }

    private Handler episodeOK = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //修改剧集列表
                getData();
                initFragment();
//                SimpleAdapter simpleAdapter = new SimpleAdapter(videoPlay.this, getData(), R.layout.episode_adapter, adapterKeys, adapterIds);
//                videoEpisodeList.setAdapter(simpleAdapter);
                if (videoView.isPlaying()) {
                    videoView.stopPlayback();
                }
                videoView.setVideoURI(Uri.parse(path));
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    };
    private Handler playUrlOK = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 3) {
                if (videoView.isPlaying()) {
                    videoView.stopPlayback();
                }
                //设置播放地址
                videoView.setVideoURI(Uri.parse(path));
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    };

    private List<HashMap<String, Object>> getData() {
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

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放结束

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

    @Override
    public void onPrepared(MediaPlayer mp) {
        videoTotalString = getShowTime(videoView.getDuration());
        video_totalTime.setText(videoTotalString);
        video_currentTime.setText(getShowTime(0));
        video_seekBar.setMax((int) videoView.getDuration());
//        new Thread(changeSeekBar).start();
        executor.execute(getChangeSeekBar);
        if (isVipVideo) {
            if (userIsVip && appData.getUserName() != "") {
                videoView.start();
            } else if (!userIsVip && appData.getUserName() !=""){
                //通知用户添加微信
                videoView.pause();
            }
            else
            {
                //提示登录
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
    public void onClick(View v) {

        if (v == videoDownloadButton) {
            addToDownload();
        }
        if (v == videoFavoriteButton) {
            addToFavorite();
        }
//        if (v == videoEpisodeButton) {
//            episodeButtonClicked();
//        }
//        if (v == videoCommentButton) {
//            videoCommentButtonClicked();
//        }
        if (v == fullScreenButton) {
            gotoFullScreen();
        }
        if (v == titleBackButton) {
            backToCateList();
        }
    }

    private void addToDownload() {
        onShowDataEvent(new TestMessage("123"));

    }

    private void addToFavorite() {
        //写入本地缓存

    }

    private void episodeButtonClicked() {

    }

    private void videoCommentButtonClicked() {

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
            if (!appData.getFromSearch())
            {
                appData.setSourcePage("VideoPlay");
                finish();
            }
            else {
                appData.setSourcePage("Search");
                startActivity(new Intent(videoPlay.this, videoList.class));
                finish();
            }
        } else if (appData.getSourcePage() == "Download") {
            appData.setSourcePage("VideoPlay");
            startActivity(new Intent(videoPlay.this, downloadActivity.class));
            finish();
        }
    }

    /**
     * seekBar拖动监听类
     */
    @SuppressWarnings("unused")
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

    /**
     * 转换播放时间
     *
     * @param milliseconds 传入毫秒值
     * @return 返回 hh:mm:ss或mm:ss格式的数据
     */
    @SuppressLint("SimpleDateFormat")
    public String getShowTime(long milliseconds) {
        // 获取日历函数
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = null;
        // 判断是否大于60分钟，如果大于就显示小时。设置日期格式
        if (milliseconds / 60000 > 60) {
            dateFormat = new SimpleDateFormat("hh:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("mm:ss");
        }
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 滑动条变化线程
     */
    private Runnable changeSeekBar = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                while (seekBarAutoFlag) {
                    if (null != videoPlay.this.videoView
                            && videoPlay.this.videoView.isPlaying()) {
                        video_seekBar.setProgress((int) videoView.getCurrentPosition());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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
            }
            else
            {
                appData.setSourcePage("VideoPlay");
//                startActivity(new Intent(videoPlay.this, videoList.class));
                finish();
            }
        }
        return false;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
        if (executor != null) {
            executor = null;
        }
        episodeOK.removeCallbacks(playUrlThread);
        sourceOK.removeCallbacks(sourceThread);
        playUrlOK.removeCallbacks(afterUrlThread);
        episodeOK = null;
        sourceOK = null;
        playUrlOK = null;
        sourceThread.interrupt();
        playUrlThread.interrupt();
        getChangeSeekBar.interrupt();
        afterUrlThread.interrupt();
        sourceThread = null;
        playUrlThread = null;
        getChangeSeekBar = null;
        afterUrlThread = null;
        bitmapResource = null;
        sourceClicked = null;
        bitmapResize = null;
        videoSourceLabel = null;
        videoDownloadButton = null;
        videoFavoriteButton = null;
        videoSourceLayout = null;
        sourceButtonsId = null;
        mProgressBar = null;
        adapterKeys = null;
        adapterIds = null;
        fullScreenButton = null;
        videoPlayOrPauseButton = null;
        video_seekBar = null;
        video_control = null;
        video_currentTime = null;
        titleBackButton = null;
        appId = null;
        appVersion = null;
        sourceData = null;
        currentVideo = null;
        contentEntity = null;
        videoIntro = null;
        videoIntro = null;
        videoCateId = null;
        videoTimeString = null;
        videoTotalString = null;
        episodeContent = null;
        episodeId = null;
        currentVideoPlayUrl = null;
        videoSiteId = null;
        getPlayUrl = null;
        videoQuality = null;
        setContentView(R.layout.view_null);

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