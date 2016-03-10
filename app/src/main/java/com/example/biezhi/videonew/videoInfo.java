package com.example.biezhi.videonew;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.biezhi.videonew.CustomerClass.BitmapResize;
import com.example.biezhi.videonew.CustomerClass.Constants;
import com.example.biezhi.videonew.CustomerClass.GetPlayUrl;
import com.example.biezhi.videonew.DataModel.EpisodeModel;
import com.example.biezhi.videonew.DataModel.SourceModel;
import com.example.biezhi.videonew.DataModel.VideoInfoModel;
import com.example.biezhi.videonew.NetWorkServer.GetServer;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class videoInfo extends AppCompatActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, View.OnClickListener {
    SurfaceView videoSurface;
    ImageButton videoPlayOrPauseButton;
    TextView videoCurrentTimeLabel;
    SeekBar videoSeekBar;
    TextView videoTotalTimeLabel;
    ImageView videoFullScreen;
    TextView videoSourceLabel;
    ImageButton videoDownloadButton;
    ImageButton videoFavorateButton;
    Button videoEpisodeButton;
    Button videoCommnetButton;
    ListView videoEpisodeList;
    TextView videoCommentText;
    ProgressBar videoProgressBar;
    Data appData;
    MediaPlayer mediaPlayer;
    Uri uri;
    RelativeLayout videoControlLayout;
    List<SourceModel.DataEntity> sourceData;
    int sourceCount;
    RelativeLayout videoSourceLayout;
    int[] bitmapResource;
    BitmapResize bitmapResize = new BitmapResize();

    /**
     * 播放或者暂停标识
     */
    private boolean isFullScreen = false;

    //设置seekBar是否可以拖动
    private boolean seekBarAutoFlag = false;

    /**
     * 视频时间显示
     */
    private TextView vedioTiemTextView;

    /**
     * 播放总时间
     */
    private String videoTimeString;

    private long videoTimeLong;

    /**
     * surfaceView播放控制
     */
    private SurfaceHolder surfaceHolder;

    /**
     * 屏幕的宽度和高度
     */
    private int screenWidth, screenHeight;

    /**
     * 控制栏显示标志
     */
    private boolean controlShow;

    /**
     * appId
     */
    String appId;

    /**
     * appVersion
     */
    String version;

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
    boolean isVipVidep;

    /**
     * 视频的episodeId
     */
    String episodeId;

    /**
     * 当前视频的播放地址
     */
    String currentVideoPlayUrl;

    String videoSitdId;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_info);
        initClass();
    }

    private void initClass() {
        appData = (Data) this.getApplicationContext();
        bitmapResource = new int[]{R.drawable.other_on1, R.drawable.other_on2, R.drawable.other_on3, R.drawable.other_on4, R.drawable.other_on5};
        bitmapResize = new BitmapResize();
        videoSurface = (SurfaceView) findViewById(R.id.video_surface);
        videoPlayOrPauseButton = (ImageButton) findViewById(R.id.videoPlayOrPause);
        videoCurrentTimeLabel = (TextView) findViewById(R.id.video_currentTime);
        videoSeekBar = (SeekBar) findViewById(R.id.video_seekBar);
        videoTotalTimeLabel = (TextView) findViewById(R.id.video_totalTime);
        videoFullScreen = (ImageView) findViewById(R.id.video_fullScreen);
        videoSourceLabel = (TextView) findViewById(R.id.video_sourceLabel);
        videoDownloadButton = (ImageButton) findViewById(R.id.video_download);
        videoFavorateButton = (ImageButton) findViewById(R.id.video_favorate);
        videoEpisodeButton = (Button) findViewById(R.id.video_button_episode);
        videoCommentText = (TextView) findViewById(R.id.video_comment);
        videoCommnetButton = (Button) findViewById(R.id.video_button_comment);
        videoEpisodeList = (ListView) findViewById(R.id.video_episode_list);
        videoProgressBar = (ProgressBar) findViewById(R.id.video_loadingBar);
        videoControlLayout = (RelativeLayout) findViewById(R.id.video_control);
        videoSourceLayout = (RelativeLayout) findViewById(R.id.video_source);
        appId = appData.getAppid();
        version = appData.getVersion();
        sourceData = new ArrayList<>();
        currentVideo = appData.getClickedVideoID();
        contentEntity = new ArrayList<>();
        videoIntro = "";
        videoCateId = "";
        episodeContent = new ArrayList<>();
        isVipVidep = false;
        episodeId = "";
        currentVideoPlayUrl = "";
        videoSitdId = "";
        getPlayUrl = new GetPlayUrl();
        episodeNum = 1;
        videoQuality = "normal";
        //获取播放地址
        //获取来源信息
        controlShow = true;

        // 获取屏幕的宽度和高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        //设置surfaceHolder
        surfaceHolder = videoSurface.getHolder();
        //设置Holder类型，该类型表示surface自己不管理缓存区
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //设置surface的回调
        surfaceHolder.addCallback(new SurfaceCallback());
        videoProgressBar.setVisibility(View.VISIBLE);
        new Thread(new getPlaySource()).start();
        new Thread(new getPlayUrl()).start();

    }


    private class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            //销毁mediaPlayer
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

        }
    }

    //获取播放源VideoId
    private class getPlaySource implements Runnable {

        @Override
        public void run() {
            //源videoId
            int videoId = Integer.valueOf(appData.getClickedVideoID());
            //请求所有的videoId以及所有的图标
            GetServer getServer = new GetServer();
            getServer.getUrl = "http://115.29.190.54:99/MyVideo.aspx?videoid=" + videoId + "&appid=" + appId + "&version=" + version;
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
                //获取所有来源完毕
                //通知修改UI
                sourceData.addAll(sourceModel.getData());
                sourceCount = sourceModel.getCount();
                Message msg = Message.obtain();
                msg.what = 2;
                sourceOK.sendMessage(msg);
            }
        }
    }


    /**
     * 来源
     */
    private Handler sourceOK = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                //修改来源UI
                for (int i = 0; i < sourceCount; i++) {
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    final ImageButton imageButton = new ImageButton(videoInfo.this);
                    imageButton.setPadding(3, 3, 3, 3);
                    imageButton.setId(i);
                    imageButton.getBackground().setAlpha(0);
                    Bitmap tempBit = bitmapResize.setBitmapSize(BitmapFactory.decodeResource(getResources(), bitmapResource[i]), 75, 75);
                    lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                    if (i == 0) {
                        lp.addRule(RelativeLayout.RIGHT_OF, R.id.video_sourceLabel);
                    } else {
                        lp.addRule(RelativeLayout.RIGHT_OF, i - 1);
                    }
                    imageButton.setImageBitmap(tempBit);
                    videoSourceLayout.addView(imageButton, lp);
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //监听点击事件
                            changeSource(imageButton.getId());
                        }
                    });
                    //添加图片
                }
            }
        }
    };

    /**
     * 切换来源
     *
     * @param sourceId
     */
    private void changeSource(int sourceId) {
        //添加选中标志
        currentVideo = String.valueOf(sourceData.get(sourceId).getVideoId());
        Bitmap choicedBit = BitmapFactory.decodeResource(getResources(), R.drawable.xuanzhong);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView imageView = new ImageView(videoInfo.this);
        imageView.setPadding(1, 1, 1, 1);
//        imageView.getBackground().setAlpha(0);
        lp.addRule(RelativeLayout.ALIGN_BOTTOM, sourceId);
        lp.addRule(RelativeLayout.ALIGN_RIGHT, sourceId);
        imageView.setImageBitmap(choicedBit);
        videoSourceLayout.addView(imageView, lp);
        //显示缓冲标志
        videoProgressBar.setVisibility(View.VISIBLE);
        //获取播放地址
        new Thread(new getPlayUrl()).start();
    }

    /**
     * 获取播放地址
     */
    private class getPlayUrl implements Runnable {
        @Override
        public void run() {
            GetServer getServer = new GetServer();
            getServer.getUrl = "http://115.29.190.54:99/Video.aspx?videoid=" + currentVideo + "&appid=" + appId + "&version=" + version;
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
                //再请求当前播放地址
                Gson gson = new Gson();
                VideoInfoModel videoInfoModel = gson.fromJson(json, VideoInfoModel.class);
                //请求播放地址，记录信息，准备修改UI
                contentEntity.addAll(videoInfoModel.getContent());
                videoCateId = String.valueOf(videoInfoModel.getCateId());
                videoIntro = videoInfoModel.getIntro();
                videoSitdId = String.valueOf(contentEntity.get(0).getId());
                GetServer getServer1 = new GetServer();
                getServer1.getUrl = "http://115.29.190.54:99/Episode.aspx?videoid=" + currentVideo + "&siteid=" + contentEntity.get(0).getId() + "&appid=" + appId + "&version=" + version;
                getServer1.aesSecret = "dd358748fcabdda1";
                String json1 = getServer1.getInfoFromServer();
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
                    videoSitdId = String.valueOf(episodeModel.getSiteId());
                    //getplayUrl.episode就是1 2 3 4 5 集
                    getPlayUrl.setValue(Integer.parseInt(videoSitdId), episodeNum, screenWidth, screenHeight);
                    getPlayUrl.ua = "iPhone";
                    getPlayUrl.originPlayUrl = episodeContent.get(episodeNum - 1).getPlayUrl();
                    getPlayUrl.quality = videoQuality;

//                    uri = Uri.parse(getPlayUrl.getUrl());
                    path = getPlayUrl.getUrl();
                    //获取信息完毕，通知修改UI
                    Message msg = Message.obtain();
                    msg.what = 1;
                    playUrlOK.sendMessage(msg);
                }

            }
        }
    }

    /**
     * 播放地址获取完毕
     */
    private Handler playUrlOK = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //获取播放地址完毕，准备播放视频
                //隐藏按钮
                //设置播放地址
                if (episodeContent.size() > 0) {
                    videoControlLayout.setVisibility(View.VISIBLE);
                    try {
                        playVideo();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    // TODO: 16/3/9 没获取到播放地址
                }
            }
        }
    };


    private void playVideo() throws IOException {
        //播放视频
        mediaPlayer = new MediaPlayer();
        //重置mediaPlayer
        mediaPlayer.reset();
        //设置声音效果
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //设置播放完成监听
        mediaPlayer.setOnCompletionListener(this);
        //设置媒体加载完成后的回调函数
        mediaPlayer.setOnPreparedListener(this);
        //设置错误监听函数
        mediaPlayer.setOnErrorListener(this);
        //设置缓存变化监听
        mediaPlayer.setOnBufferingUpdateListener(this);
        //设置播放路径
        mediaPlayer.setDataSource(path);
//        mediaPlayer.setDataSource(this, uri);
        //设置异步加载视频
        mediaPlayer.prepareAsync();
    }


    /**
     * 视频缓存大小监听
     *
     * @param mp
     * @param percent
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.e("text", "onBufferingUpdate-->" + percent);

    }

    @Override
    public void onClick(View v) {
        //重写点击事件
        if (v == videoPlayOrPauseButton) {
            if (null != mediaPlayer) {
                //正在播放
                if (mediaPlayer.isPlaying()) {
                    Constants.playPosition = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();
                    videoPlayOrPauseButton.setImageResource(R.drawable.video_pause);
                } else {
                    if (Constants.playPosition >= 0) {
                        mediaPlayer.seekTo(Constants.playPosition);
                        mediaPlayer.start();
                        videoPlayOrPauseButton.setImageResource(R.drawable.video_play);
                        Constants.playPosition = -1;
                    }
                }
            }
        }
        if (v == videoFullScreen) {
            changeVideoSize();
            //全屏按钮处理
        }
        if (v == videoFavorateButton) {
            addToFavorate();
            //收藏电影
        }
        if (v == videoCommnetButton) {
            commentShow();
            //简介
        }
        if (v == videoDownloadButton) {
            addToDownload();
            //下载
        }
        if (v == videoEpisodeButton) {
            episodeShow();
            //剧集
        }
    }

    /**
     * 播放完成
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        //设置seekBar跳转到最后位置
        videoSeekBar.setProgress(Integer.parseInt(String.valueOf(videoTimeLong)));
        //设置播放标记为false
        seekBarAutoFlag = false;

    }

    // TODO: 16/3/9 所有的视频播放失败，都加载出错了应该~
    /**
     * 错误监听 
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //需要处理一些错误信息
        String TAG2 = "mediaError";
        switch (what){
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.e(TAG2, "unknown media playback error");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.e(TAG2, "server connection died");
            default:
                Log.e(TAG2, "generic audio playback error");
                break;
        }

        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                Log.e(TAG2, "IO media error");
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Log.e(TAG2, "media error, malformed");
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Log.e(TAG2, "unsupported media content");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Log.e(TAG2, "media timeout error");
                break;
            default:
                Log.e(TAG2, "unknown playback error");
                break;
        }
        return false;
    }

    /**
     * 视频加载完毕监听
     *
     * @param mp
     */

    @Override
    public void onPrepared(MediaPlayer mp) {
        //当视频加载完成后，隐藏加载提示
        videoProgressBar.setVisibility(View.INVISIBLE);
        //判断是否有保存的播放位置，防止屏幕旋转导致的播放位置丢失
        if (Constants.playPosition >= 0) {
            mediaPlayer.seekTo(Constants.playPosition);
            Constants.playPosition = -1;
        }
        seekBarAutoFlag = true;
        videoSeekBar.setMax(mediaPlayer.getDuration());
        videoTimeLong = mediaPlayer.getDuration();
        videoTimeString = getShowTime(videoTimeLong);
        videoTotalTimeLabel.setText(videoTimeString);
        //设置拖动监听事件
        videoSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
        //设置按钮监听事件
        videoPlayOrPauseButton.setOnClickListener(this);
        videoFullScreen.setOnClickListener(this);
        videoDownloadButton.setOnClickListener(this);
        videoFavorateButton.setOnClickListener(this);
        //播放视频
        mediaPlayer.start();
        //开启线程，刷新进度条
        videoSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏控制栏或者显示控制栏
                if (controlShow) {
                    videoControlLayout.setVisibility(View.INVISIBLE);
                    controlShow = false;
                } else {
                    videoControlLayout.setVisibility(View.VISIBLE);
                    controlShow = true;
                }
            }
        });
        new Thread(changeSeekBar).start();
        //设置surfaceView保持在屏幕上
        mediaPlayer.setScreenOnWhilePlaying(true);
        surfaceHolder.setKeepScreenOn(true);
    }

    /**
     * 从暂停中恢复
     */
    protected void onResume() {
        super.onResume();
        if (Constants.playPosition >= 0) {
            if (null != mediaPlayer) {
                seekBarAutoFlag = true;
                mediaPlayer.seekTo(Constants.playPosition);
                mediaPlayer.start();
            }
        }
    }

    /**
     * 页面处于暂停状态
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (null != mediaPlayer && mediaPlayer.isPlaying()) {
                Constants.playPosition = mediaPlayer.getCurrentPosition();
                seekBarAutoFlag = false;
            }
        } catch (Exception e) {
            // TODO: 16/3/7 handle exception
            e.printStackTrace();
        }
    }

    /**
     * 屏幕旋转完成时调用
     *
     * @param outState
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mediaPlayer) {
            //保存播放位置
            Constants.playPosition = mediaPlayer.getCurrentPosition();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 当屏幕销毁的时候调用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //MediaPlayer非常消耗资源，当activity销毁时，需要直接销毁
        try {
            if (null != this.mediaPlayer) {
                //提前标志为false,防止视频停止时，线程仍旧在运行
                seekBarAutoFlag = false;
                //如果正在播放 则停止
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                Constants.playPosition = -1;
                videoInfo.this.mediaPlayer.release();
                ;
                videoInfo.this.mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 滑动条变化线程
     */
    private Runnable changeSeekBar = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            // 增加对异常的捕获，防止在判断mediaPlayer.isPlaying的时候，报IllegalStateException异常
            try {
                while (seekBarAutoFlag) {
                    /*
                     * mediaPlayer不为空且处于正在播放状态时，使进度条滚动。
                     * 通过指定类名的方式判断mediaPlayer防止状态发生不一致
                     */

                    if (null != videoInfo.this.mediaPlayer
                            && videoInfo.this.mediaPlayer.isPlaying()) {
//                        videoSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


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
                    mediaPlayer.seekTo(progress);
                }
                // 设置当前播放时间
                vedioTiemTextView.setText(getShowTime(progress) + "/" + videoTimeString);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

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
     * 全屏按钮处理
     */
    private void changeVideoSize() {
        int width = mediaPlayer.getVideoWidth();
        int height = mediaPlayer.getVideoHeight();
        if (isFullScreen) {
            /*
             * 如果为全屏模式则改为适应内容的，前提是视频宽高小于屏幕宽高，如果大于宽高 我们要做缩放
             * 如果视频的宽高度有一方不满足我们就要进行缩放. 如果视频的大小都满足就直接设置并居中显示。
             */
            if (width > screenWidth || height > screenHeight) {
                //计算出宽高的倍数
                float vWidth = (float) width / (float) screenWidth;
                float vHeight = (float) height / (float) screenHeight;
                //获取最大的倍数值 按大数进行缩放
                float max = Math.max(vWidth, vHeight);
                width = (int) Math.ceil((float) width / max);
                height = (int) Math.ceil((float) height / max);
            }
            //设置SurfaceView的大小并且居中显示
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            videoSurface.setLayoutParams(layoutParams);
            isFullScreen = false;
        } else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screenWidth, screenHeight);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            videoSurface.setLayoutParams(layoutParams);
            isFullScreen = true;
        }
    }

    /**
     * 添加到下载
     */
    private void addToDownload() {

    }

    /**
     * 添加到最爱
     */
    private void addToFavorate() {

    }

    /**
     * 剧集点击事件
     */
    private void episodeShow() {

    }

    /**
     * 评论点击事件
     */
    private void commentShow() {

    }

    /**
     * 返回按钮
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isFullScreen) {
                //退出全屏
                changeVideoSize();
            } else {
                //非全屏状态退出当前当前页面
                appData.setSourcePage("Play");
                startActivity(new Intent(videoInfo.this, videoList.class));
            }
        }
        return false;
    }


}
