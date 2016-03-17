package com.example.biezhi.videonew;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.biezhi.videonew.CustomerClass.BitmapResize;
import com.example.biezhi.videonew.CustomerClass.GetPlayUrl;
import com.example.biezhi.videonew.DataModel.EpisodeModel;
import com.example.biezhi.videonew.DataModel.SourceModel;
import com.example.biezhi.videonew.DataModel.VideoInfoModel;
import com.example.biezhi.videonew.NetWorkServer.GetServer;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class videoPlay extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener
        , MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, View.OnClickListener {

    private VideoView videoView;
    TextView videoCurrentTimeLabel;
    TextView videoSourceLabel;
    ImageButton videoDownloadButton;
    ImageButton videoFavoriteButton;
    Button videoEpisodeButton;
    Button videoCommentButton;
    ListView videoEpisodeList;
    TextView videoCommentText;
    Data appData;
    List<SourceModel.DataEntity> sourceData;
    int sourceCount;
    RelativeLayout videoSourceLayout;
    int[] bitmapResource;
    BitmapResize bitmapResize = new BitmapResize();
    ProgressBar mProgressBar;
    int[] sourceButtonsId;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        initPlayer();
        initClass();
    }

    private void initClass() {
        appData = (Data) this.getApplicationContext();
        bitmapResource = new int[]{R.drawable.other_on1, R.drawable.other_on2, R.drawable.other_on3, R.drawable.other_on4, R.drawable.other_on5};
        bitmapResize = new BitmapResize();
        videoCurrentTimeLabel = (TextView) findViewById(R.id.video_currentTime);
        videoSourceLabel = (TextView) findViewById(R.id.video_sourceLabel);
        videoDownloadButton = (ImageButton) findViewById(R.id.video_download);
        videoFavoriteButton = (ImageButton) findViewById(R.id.video_favorate);
        videoEpisodeButton = (Button) findViewById(R.id.video_button_episode);
        videoCommentText = (TextView) findViewById(R.id.video_comment);
        videoCommentButton = (Button) findViewById(R.id.video_button_comment);
        videoEpisodeList = (ListView) findViewById(R.id.video_episode_list);
        videoSourceLayout = (RelativeLayout) findViewById(R.id.video_source);
        sourceButtonsId = new int[]{R.id.source_1, R.id.source_2, R.id.source_3, R.id.source_4, R.id.source_5};
        mProgressBar = (ProgressBar) findViewById(R.id.video_loadingBar);
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
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        videoDownloadButton.setOnClickListener(this);
        videoCommentButton.setOnClickListener(this);
        videoFavoriteButton.setOnClickListener(this);
        videoEpisodeButton.setOnClickListener(this);
        new Thread(new getVideoSource()).start();
    }

    private void initPlayer() {
        Vitamio.initialize(videoPlay.this);
        videoView = (VideoView) findViewById(R.id.video_surface);
        videoView.setVideoURI(Uri.parse("http://api1.rrmj.tv/api/letvyun/letvmmsid.php?vid=47896295"));
        MediaController mediaPlayerControl = new MediaController(this);
        mediaPlayerControl.setAnchorView(videoView);
        mediaPlayerControl.setAnimationStyle(-1);
        videoView.setMediaController(mediaPlayerControl);
        videoView.setOnPreparedListener(this);
        videoView.setOnFullScreenboolChangeListener(new MediaPlayer.OnFullScreenChangeListener() {
            @Override
            public void onFullScreenChanged() {
                if (videoView.gotoFullScreen) {
                    //如果等于false。则表明是非全屏状态，全屏
                } else {
                    //进行全屏变换
                }
            }
        });
        videoView.setOnInfoListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnBufferingUpdateListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnSeekCompleteListener(this);
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
                    ImageButton sourceButton = (ImageButton) findViewById(sourceButtonsId[i]);
                    sourceButton.setVisibility(View.VISIBLE);
                    final int finalI = i;
                    sourceButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changeSource(finalI + 1);
                        }
                    });
                }
            }
        }
    };

    /**
     * 修改来源
     * @param sourceId 来源ID
     */
    private void changeSource(int sourceId) {
        Log.e("123", sourceId + "");
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
        videoView.start();
        videoView.getDuration();
        mProgressBar.setVisibility(View.INVISIBLE);

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
        if (v == videoEpisodeButton) {
            episodeButtonClicked();
        }
        if (v == videoCommentButton) {
            videoCommentButtonClicked();
        }
    }

    private void addToDownload() {

    }

    private void addToFavorite() {

    }

    private void episodeButtonClicked() {

    }

    private void videoCommentButtonClicked() {

    }

    private void changeEpisode(int episodeNum) {

    }
}
