package com.example.biezhi.videonew;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.example.biezhi.videonew.CustomerClass.Constants;
import com.example.biezhi.videonew.CustomerClass.GetPlayUrl;
import com.example.biezhi.videonew.CustomerClass.SysApplication;
import com.example.biezhi.videonew.DataModel.EpisodeModel;
import com.example.biezhi.videonew.DataModel.VideoInfoModel;
import com.example.biezhi.videonew.MessageBox.SeekBarChangeMessage;
import com.example.biezhi.videonew.MessageBox.SeekBarChangedMessage;
import com.example.biezhi.videonew.MessageBox.TestMessage;
import com.example.biezhi.videonew.MessageBox.VideoEpisodeMessage;
import com.example.biezhi.videonew.MessageBox.VideoSourceMessage;
import com.example.biezhi.videonew.NetWorkServer.GetServer;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


/*
* 1，全屏有标题栏
* 2，添加mediacontroller
* 3，上一个界面传参
*/

public class fullScreenPlay extends Activity implements MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnPreparedListener, MediaController.OnHiddenListener, View.OnClickListener {

    VideoView videoView;
    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private AudioManager mAudioManager;
    private TextView videoSelectTv;
    private ListView episodeList;
    /**
     * 最大声音
     */
    private int mMaxVolume;
    /**
     * 当前声音
     */
    private int mVolume = -1;
    /**
     * 当前亮度
     */
    private float mBrightness = -1f;
    /**
     * 当前缩放模式
     */
    private int mLayout = VideoView.VIDEO_LAYOUT_ZOOM;
    private GestureDetector mGestureDetector;

    private boolean seekBarAutoFlag = true;

    /**
     * 菊花
     */
    AVLoadingIndicatorView avloadingIndicatorView;

    /**
     * 返回按钮
     */
    ImageButton fullscreen_back;

    /**
     * 播放按钮
     */
    ImageButton playOrPause;

    /**
     * 电影标题
     */
    TextView video_title;

    /**
     * 控制栏
     */
    RelativeLayout video_control;

    /**
     * 当前播放时间
     */
    TextView video_currentTime;

    /**
     * 进度条
     */
    SeekBar video_seekBar;

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

    /**
     * 缓存暂停标志
     */
    private boolean needResume = false;

    /**
     * 播放地址
     */
    private String path;

    /**
     * 视频名
     */
    private String videoTitle;

    /**
     * 会员视频
     */
    private boolean isVipVideo;

    /**
     * 是否是vip用户
     */
    private boolean isVipUser;

    /**
     * 当前播放时间
     */
    private int currentTime;

    Data appData;

    /**
     * 屏幕的宽
     */
    private int screenWidth;

    /**
     * 屏幕的高
     */
    private int screenHeight;

    /**
     * 是否显示control
     */
    private boolean isShow = true;

    /**
     * 锁定按钮
     */
    private ImageButton video_lockButton;

    /**
     * 是否锁定
     */
    private boolean isLocked = false;

    private boolean isShowEpisode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(this);
        setContentView(R.layout.activity_full_screen_play);
//        SysApplication.getInstance().addActivity(this);
        initClass();
        initPlayer();
    }

    /**
     * 初始化类
     */
    private void initClass() {
        EventBus.getDefault().register(this);
        appData = (Data) this.getApplicationContext();
        avloadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);
        episodeList = (ListView) findViewById(R.id.episode_list);
        fullscreen_back = (ImageButton) findViewById(R.id.fullscreen_back);
        video_title = (TextView) findViewById(R.id.video_title);
        video_control = (RelativeLayout) findViewById(R.id.video_control);
        video_seekBar = (SeekBar) findViewById(R.id.video_seekBar);
        video_totalTime = (TextView) findViewById(R.id.video_totalTime);
        video_currentTime = (TextView) findViewById(R.id.video_currentTime);
        playOrPause = (ImageButton) findViewById(R.id.video_playOrPause);
        video_lockButton = (ImageButton) findViewById(R.id.video_lock);
        videoSelectTv = (TextView) findViewById(R.id.video_select);
        videoSelectTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowEpisode) {
                    episodeList.setVisibility(View.VISIBLE);
                    isShowEpisode = !isShowEpisode;
                } else {
                    episodeList.setVisibility(View.INVISIBLE);
                    isShowEpisode = !isShowEpisode;
                }
            }
        });
//        drawerLayout = (DrawerLayout) findViewById(R.id.episode_layout);
        videoTimeString = "";
        videoTotalString = "";
        avloadingIndicatorView.setVisibility(View.VISIBLE);
        path = appData.getPlayUrl();
        videoTitle = appData.getVideoName();
        isVipVideo = appData.getVideoVip();
        isVipUser = appData.getUserVip();
        currentTime = appData.getCurrentPosition();
        video_title.setText(videoTitle);
        fullscreen_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocked) {
                    //提示界面处于锁定状态
                    Toast.makeText(fullScreenPlay.this, "界面已被锁定，请解锁！", Toast.LENGTH_SHORT).show();
                } else {
                    //非全屏状态退出当前当前页面
                    //准备数据，进行跳转
                    if (appData.getSourcePage() == "Download") {
                        appData.setSourcePage("FullScreen");
                        appData.setCurrentPosition((int) videoView.getCurrentPosition());
                        startActivity(new Intent(fullScreenPlay.this, downloadActivity.class));
                    } else {
                        appData.setSourcePage("FullScreen");
                        appData.setCurrentPosition((int) videoView.getCurrentPosition());
                        startActivity(new Intent(fullScreenPlay.this, videoPlay.class));

                    }
                }
            }
        });
        video_lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLocked) {
                    video_lockButton.setImageResource(R.drawable.lock);
                    //隐藏所有control
                    video_control.setVisibility(View.INVISIBLE);
                    fullscreen_back.setVisibility(View.INVISIBLE);
                    video_title.setVisibility(View.INVISIBLE);
                    videoSelectTv.setVisibility(View.INVISIBLE);
                } else {
                    video_lockButton.setImageResource(R.drawable.unlock);
                    video_title.setVisibility(View.VISIBLE);
                    video_control.setVisibility(View.VISIBLE);
                    fullscreen_back.setVisibility(View.VISIBLE);
                    videoSelectTv.setVisibility(View.VISIBLE);
                }
                isLocked = !isLocked;

            }
        });
        playOrPause.setOnClickListener(this);
        video_seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
        screenWidth = appData.getWidth();
        screenHeight = appData.getHeight();
        isShow = true;
        isLocked = false;
        initList();
    }

    private void initList() {
        SimpleAdapter adapter = new SimpleAdapter(
                fullScreenPlay.this,
                getData(),
                R.layout.episode_item, new String[]{"episodeName"},
                new int[]{R.id.episode_text}
        );
        episodeList.setAdapter(adapter);
        episodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //展示菊花
                avloadingIndicatorView.setVisibility(View.VISIBLE);
                String videoId = appData.getVideoEpisode().get(position).get("videoId");
                String episodeId = appData.getVideoEpisode().get(position).get("episodeId");
                //请求网络
                EventBus.getDefault().post(new VideoEpisodeMessage(videoId, episodeId, position + 1));
            }
        });

    }

    /**
     * 获取播放地址
     *
     * @param videoEpisodeMessage
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getPlayUrl(VideoEpisodeMessage videoEpisodeMessage) {
        if (videoEpisodeMessage.getVideoId() != null) {
            GetServer getServer = new GetServer();
            getServer.getUrl = "http://115.29.190.54:99/Video.aspx?videoid=" + videoEpisodeMessage.getVideoId() + "&appid=" + appData.getAppid() + "&version=" + appData.getVersion();
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
                getServer.getUrl = "http://115.29.190.54:99/Episode.aspx?videoid=" + videoEpisodeMessage.getVideoId() + "&siteid=" + videoInfoModel.getContent().get(0).getId() + "&appid=" + appData.getAppid() + "&version=" + appData.getVersion();
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
                    GetPlayUrl getPlayUrl = new GetPlayUrl();
                    getPlayUrl.setValue(episodeModel.getSiteId(), videoEpisodeMessage.getEpisode(), screenWidth, screenHeight);
                    getPlayUrl.ua = "iPhone";
                    getPlayUrl.originPlayUrl = episodeModel.getContent().get(videoEpisodeMessage.getEpisode() - 1).getPlayUrl();
                    getPlayUrl.quality = "normal";
                    path = getPlayUrl.getUrl();
                    //通知主线程修改播放器
                    appData.setVideoName(episodeModel.getContent().get(videoEpisodeMessage.getEpisode() - 1).getName());
                    EventBus.getDefault().post(new TestMessage("URL_OK"));
                }
            }
        }
    }

    /**
     * 重新设置播放器，播放
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void playUrlOK(TestMessage testMessage) {
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
        videoView.setVideoURI(Uri.parse(path));
        //修改标题
        video_title.setText(appData.getVideoName());
    }

    /**
     * 播放器初始化
     */
    private void initPlayer() {
        videoView = (VideoView) findViewById(R.id.video_fullScreen_view);
        videoView.setVideoLayout(2, 0);
        videoView.setVideoURI(Uri.parse(path));
        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView) findViewById(R.id.operation_percent);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        videoView.setOnPreparedListener(this);
        videoView.setOnSeekCompleteListener(this);
        videoView.setOnInfoListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnBufferingUpdateListener(this);
        videoView.setOnSeekCompleteListener(this);

    }


    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i < appData.getVideoEpisode().size() + 1; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("episodeName", "第" + i + "集");
            list.add(map);
        }
        return list;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isLocked) {
            //隐藏所有按钮
            video_control.setVisibility(View.INVISIBLE);
            video_title.setVisibility(View.INVISIBLE);
            fullscreen_back.setVisibility(View.INVISIBLE);
            videoSelectTv.setVisibility(View.INVISIBLE);
            episodeList.setVisibility(View.INVISIBLE);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (isShow) {
                        video_lockButton.setVisibility(View.VISIBLE);
                        isShow = !isShow;
                    } else {
                        video_lockButton.setVisibility(View.INVISIBLE);
                        isShow = !isShow;
                    }
                    break;
            }
        } else {
            if (mGestureDetector.onTouchEvent(event))
                return true;
            // 处理手势结束
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    endGesture();
                    break;
                case MotionEvent.ACTION_DOWN:
                    //显示或者不显示标题
//                    Log.e("123", "123");
                    if (isShow) {
                        video_title.setVisibility(View.VISIBLE);
                        video_control.setVisibility(View.VISIBLE);
                        fullscreen_back.setVisibility(View.VISIBLE);
                        video_lockButton.setVisibility(View.VISIBLE);
                        videoSelectTv.setVisibility(View.VISIBLE);
                        isShow = !isShow;
                    } else {
                        video_control.setVisibility(View.INVISIBLE);
                        video_title.setVisibility(View.INVISIBLE);
                        fullscreen_back.setVisibility(View.INVISIBLE);
                        video_lockButton.setVisibility(View.INVISIBLE);
                        videoSelectTv.setVisibility(View.INVISIBLE);
                        episodeList.setVisibility(View.INVISIBLE);
                        isShow = !isShow;
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;

        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
    }


    /**
     * 从暂停中恢复
     */
    protected void onResume() {
        super.onResume();
        if (Constants.playPosition >= 0) {
            if (null != videoView) {
                seekBarAutoFlag = true;
                videoView.seekTo(Constants.playPosition);
                videoView.start();
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
            if (null != videoView && videoView.isPlaying()) {
                Constants.playPosition = (int) videoView.getCurrentPosition();
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
        if (null != videoView) {
            //保存播放位置
            Constants.playPosition = (int) videoView.getCurrentPosition();
        }
    }

    /**
     * 当屏幕销毁的时候调用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        try {
            if (null != this.videoView) {
                //提前标志为false,防止视频停止时，线程仍旧在运行
                seekBarAutoFlag = false;
                //如果正在播放 则停止
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
                Constants.playPosition = -1;
                fullScreenPlay.this.videoView.stopPlayback();
                fullScreenPlay.this.videoView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 滑动条变化线程
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void changeSeekBarMessage(SeekBarChangeMessage seek) {
        while (seekBarAutoFlag) {
            if (this.videoView != null && this.videoView.isPlaying()) {
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


//    /**
//     * 滑动条变化线程
//     */
//    private Runnable changeSeekBar = new Runnable() {
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            // 增加对异常的捕获，防止在判断mediaPlayer.isPlaying的时候，报IllegalStateException异常
//            try {
//                while (seekBarAutoFlag) {
//                    /*
//                     * mediaPlayer不为空且处于正在播放状态时，使进度条滚动。
//                     * 通过指定类名的方式判断mediaPlayer防止状态发生不一致
//                     */
//
//                    if (null != fullScreenPlay.this.videoView
//                            && fullScreenPlay.this.videoView.isPlaying()) {
////                        videoSeekBar.setProgress(mediaPlayer.getCurrentPosition());
//                        video_seekBar.setProgress((int) videoView.getCurrentPosition());
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };

    /**
     * 控件点击事件
     *
     * @param v 被点击的view
     */
    @Override
    public void onClick(View v) {
        if (v == playOrPause) {
            if (videoView != null) {
                if (videoView.isPlaying()) {
                    Constants.playPosition = (int) videoView.getCurrentPosition();
                    videoView.pause();
                    playOrPause.setImageResource(R.drawable.video_pause);
                } else {
                    videoView.seekTo(Constants.playPosition);
                    videoView.start();
                    playOrPause.setImageResource(R.drawable.video_play);
                    Constants.playPosition = -1;
                }
            }
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
    public void onHidden() {

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
                avloadingIndicatorView.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                //缓存完成，继续播放
                if (needResume)
                    mp.start();
                avloadingIndicatorView.setVisibility(View.GONE);
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
        //缓冲完毕，准备播放
        avloadingIndicatorView.setVisibility(View.INVISIBLE);
        //设置视频的时间
        videoTotalString = getShowTime((int) videoView.getDuration());
        video_totalTime.setText(videoTotalString);
        video_currentTime.setText(getShowTime(currentTime));
//        playOrPause.setImageResource(R.drawable.video_play);
        //隐藏控制栏
//        video_control.setVisibility(View.INVISIBLE);
        //隐藏按钮和标题
        fullscreen_back.setVisibility(View.INVISIBLE);
        video_title.setVisibility(View.INVISIBLE);
        videoSelectTv.setVisibility(View.INVISIBLE);
        video_seekBar.setMax((int) videoView.getDuration());
        video_seekBar.setProgress(currentTime);
        videoView.seekTo(currentTime);
        EventBus.getDefault().post(new SeekBarChangeMessage());
        videoView.start();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            if (mLayout == VideoView.VIDEO_LAYOUT_ZOOM)
//                mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
//            else
//                mLayout++;
//            if (videoView != null)
//                videoView.setVideoLayout(mLayout, 0);
            return true;
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {

            //lock屏蔽手势
            if (!isLocked) {
                float mOldX = e1.getX(), mOldY = e1.getY();
                int y = (int) e2.getRawY();
                Display disp = getWindowManager().getDefaultDisplay();
                int windowWidth = disp.getWidth();
                int windowHeight = disp.getHeight();

                if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
                    onVolumeSlide((mOldY - y) / windowHeight);
                else if (mOldX < windowWidth / 5.0)// 左边滑动
                    onBrightnessSlide((mOldY - y) / windowHeight);
            }
            //左右滑动
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        private int verticalMinDistance = 100;
        private int minVelocity = 20;

        //重写滑动事件
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!isLocked) {
                if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {

                    // 切换Activity
                    // Intent intent = new Intent(ViewSnsActivity.this, UpdateStatusActivity.class);
                    // startActivity(intent);
//                Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();
                    Log.e("123", "左滑");
                    if (videoView != null) {
                        int total = (int) videoView.getDuration();
                        int moved = (int) Math.abs(e2.getX() - e1.getX());
                        if (total > 0) {
                            float seekNums = (float) moved / (float) screenWidth * (float) total;
                            seekByGesture((int) seekNums, false);
                        }

                    }
                    Log.e("距离", "" + (e2.getX() - e1.getX()));
                    //改变进度条
                } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {

                    // 切换Activity
                    // Intent intent = new Intent(ViewSnsActivity.this, UpdateStatusActivity.class);
                    // startActivity(intent);
//                Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();
                    if (videoView != null) {
                        int total = (int) videoView.getDuration();
                        int moved = (int) Math.abs(e2.getX() - e1.getX());
                        if (total > 0) {
                            float seekNums = (float) moved / (float) screenWidth * (float) total;
                            seekByGesture((int) seekNums, true);
                        }
                        Log.e("123", "右滑");
                    }

                }
            }
            return false;
        }
    }

    private void seekByGesture(int seekNums, boolean isToRight) {
        if (seekNums > 0) {
            if (isToRight) {
                int current = (int) videoView.getCurrentPosition();
                if (seekNums + current < (int) videoView.getDuration()) {
                    videoView.seekTo(current + seekNums);
                    video_seekBar.setProgress(current + seekNums);
                } else {
                    videoView.seekTo(videoView.getDuration() - 1000);
                    video_seekBar.setProgress((int) videoView.getDuration() - 1000);
                }
            } else {
                int current = (int) videoView.getCurrentPosition();
                if (current - seekNums > 0) {
                    videoView.seekTo(current - seekNums);
                    video_seekBar.setProgress(current - seekNums);
                } else {
                    videoView.seekTo(1000);
                    video_seekBar.setProgress(1000);
                }
            }
        }
    }

    /**
     * 定时隐藏
     */
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mVolumeBrightnessLayout.setVisibility(View.GONE);
        }
    };

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width
                * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (videoView != null)
            videoView.setVideoLayout(mLayout, 0);
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isLocked) {
                //提示界面处于锁定状态
                Toast.makeText(this, "界面已被锁定，请解锁！", Toast.LENGTH_SHORT).show();
            } else {
                //非全屏状态退出当前当前页面
                //准备数据，进行跳转
                if (appData.getSourcePage() == "Download") {
                    appData.setSourcePage("FullScreen");
                    appData.setCurrentPosition((int) videoView.getCurrentPosition());
                    startActivity(new Intent(fullScreenPlay.this, downloadActivity.class));
                    finish();
//                    SysApplication.getInstance().exit();
                } else {
                    appData.setSourcePage("FullScreen");
                    appData.setCurrentPosition((int) videoView.getCurrentPosition());
                    startActivity(new Intent(fullScreenPlay.this, videoPlay.class));
                    finish();
//                    SysApplication.getInstance().exit();
                }
            }
        }
        return false;
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


}
