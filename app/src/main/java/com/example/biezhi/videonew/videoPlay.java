package com.example.biezhi.videonew;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.biezhi.videonew.CustomerClass.BitmapResize;
import com.example.biezhi.videonew.CustomerClass.GetPlayUrl;
import com.example.biezhi.videonew.DataModel.EpisodeModel;
import com.example.biezhi.videonew.DataModel.SourceModel;
import com.example.biezhi.videonew.DataModel.VideoInfoModel;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class videoPlay extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener
        , MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,View.OnClickListener,View.OnTouchListener {

    private VideoView videoView;
    ImageButton videoPlayOrPauseButton;
    TextView videoCurrentTimeLabel;
    SeekBar videoSeekBar;
    TextView videoTotalTimeLabel;
    ImageButton videoFullScreen;
    TextView videoSourceLabel;
    ImageButton videoDownloadButton;
    ImageButton videoFavorateButton;
    Button videoEpisodeButton;
    Button videoCommnetButton;
    ListView videoEpisodeList;
    TextView videoCommentText;
    ProgressBar videoProgressBar;
    Data appData;
    android.media.MediaPlayer mediaPlayer;
    RelativeLayout videoControlLayout;
    List<SourceModel.DataEntity> sourceData;
    int sourceCount;
    RelativeLayout videoSourceLayout;
    int[] bitmapResource;
    BitmapResize bitmapResize = new BitmapResize();

    /**
     * 是否处于全屏状态
     */
    private boolean isFullScreen = false;

    //设置seekBar是否可以拖动
    private boolean seekBarAutoFlag = false;

    /**
     * 视频时间显示
     */
    private TextView videoTiemTextView;

    /**
     * 播放总时间
     */
    private String videoTimeString;

    private long videoTimeLong;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        initPlayer();
        initClass();
    }

    private void initClass()
    {
        appData = (Data) this.getApplicationContext();
        bitmapResource = new int[]{R.drawable.other_on1, R.drawable.other_on2, R.drawable.other_on3, R.drawable.other_on4, R.drawable.other_on5};
        bitmapResize = new BitmapResize();
//        videoPlayOrPauseButton = (ImageButton) findViewById(R.id.videoPlayOrPause);
        videoCurrentTimeLabel = (TextView) findViewById(R.id.video_currentTime);
//        videoSeekBar = (SeekBar) findViewById(R.id.video_seekBar);
//        videoTotalTimeLabel = (TextView) findViewById(R.id.video_totalTime);
//        videoFullScreen = (ImageButton) findViewById(R.id.video_fullScreen);
        videoSourceLabel = (TextView) findViewById(R.id.video_sourceLabel);
        videoDownloadButton = (ImageButton) findViewById(R.id.video_download);
        videoFavorateButton = (ImageButton) findViewById(R.id.video_favorate);
        videoEpisodeButton = (Button) findViewById(R.id.video_button_episode);
        videoCommentText = (TextView) findViewById(R.id.video_comment);
        videoCommnetButton = (Button) findViewById(R.id.video_button_comment);
        videoEpisodeList = (ListView) findViewById(R.id.video_episode_list);
        videoProgressBar = (ProgressBar) findViewById(R.id.video_loadingBar);
//        videoControlLayout = (RelativeLayout) findViewById(R.id.video_control);
        videoSourceLayout = (RelativeLayout) findViewById(R.id.video_source);
        appId = appData.getAppid();
        appVersion = appData.getVersion();
        sourceData = new ArrayList<>();
        currentVideo = appData.getClickedVideoID();
        contentEntity = new ArrayList<>();
        videoIntro = "";
        videoCateId = "";
        episodeContent = new ArrayList<>();
        isVipVideo = false;
        episodeId = "";
        currentVideoPlayUrl = "";
        videoSiteId = "";
        getPlayUrl = new GetPlayUrl();
        episodeNum = 1;
        videoQuality = "normal";
        //获取播放地址
        //获取来源信息
        controlShow = true;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }
    private void initPlayer() {
        Vitamio.initialize(videoPlay.this);
        videoView = (VideoView) findViewById(R.id.video_surface);
        videoView.setVideoURI(Uri.parse("http://vplay.aixifan.com/des/20160304/3251926_mp4/3251926_lvbr.mp4?k=f0d230e08c8af6c131390bec52390a6a&t=1458114898"));
        videoView.setVideoLayout(0,1080/650);
        MediaController mediaPlayerControl = new MediaController(this);
        mediaPlayerControl.setAnchorView(videoView);
        mediaPlayerControl.setAnimationStyle(-1);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.video_surface);
//        mediaPlayerControl.setLayoutParams(layoutParams);
        videoView.setMediaController(mediaPlayerControl);
        videoView.setOnPreparedListener(this);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToFullScreen();
            }
        });

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        videoView.start();
        videoView.getDuration();

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }


    public void changeToFullScreen()
    {

    }

    @Override
    public void onClick(View v) {
        if (v == videoFullScreen)
        {
            changeToFullScreen();
        }
        if (v == videoView)
        {
            videoView.pause();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == videoFullScreen)
        {
            changeToFullScreen();
        }
        return false;
    }
}
