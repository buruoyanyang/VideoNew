package com.example.biezhi.videonew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.example.biezhi.videonew.CustomerClass.Constants;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnPreparedListener, MediaController.OnHiddenListener ,View.OnClickListener{

    VideoView videoView;
    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private AudioManager mAudioManager;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_play);
        initClass();
        initPlayer();
    }

    /**
     * 初始化类
     */
    private void initClass() {
        appData = (Data) this.getApplicationContext();
        avloadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);
        fullscreen_back = (ImageButton) findViewById(R.id.fullscreen_back);
        video_title = (TextView) findViewById(R.id.video_title);
        video_control = (RelativeLayout) findViewById(R.id.video_control);
        video_seekBar = (SeekBar) findViewById(R.id.video_seekBar);
        video_totalTime = (TextView) findViewById(R.id.video_totalTime);
        video_currentTime = (TextView) findViewById(R.id.video_currentTime);
        playOrPause = (ImageButton)findViewById(R.id.videoPlayOrPause);
        videoTimeString = "";
        videoTotalString = "";
        avloadingIndicatorView.setVisibility(View.VISIBLE);
        path = appData.getPlayUrl();
        videoTitle = appData.getVideoName();
        isVipVideo = appData.getVideoVip();
        isVipUser = appData.getUserVip();
        currentTime = appData.getCurrentPosition();
        video_title.setText(videoTitle);
//        fullscreen_back.setOnClickListener(this);
//        playOrPause.setOnClickListener(this);
    }



    /**
     * 播放器初始化
     */
    private void initPlayer() {
        Vitamio.initialize(fullScreenPlay.this);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;
        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
            case MotionEvent.ACTION_DOWN:
                //显示或者不显示标题
                Log.e("123", "123");
                break;
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
        //MediaPlayer非常消耗资源，当activity销毁时，需要直接销毁
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

                    if (null != fullScreenPlay.this.videoView
                            && fullScreenPlay.this.videoView.isPlaying()) {
//                        videoSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 控件点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {

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
                video_currentTime.setText(getShowTime(progress) + "/" + videoTimeString);
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
        videoTotalString = getShowTime(videoView.getDuration());
        video_totalTime.setText(videoTotalString);
        video_currentTime.setText(getShowTime(currentTime));
//        playOrPause.setImageResource(R.drawable.video_play);
        //隐藏控制栏
//        video_control.setVisibility(View.INVISIBLE);
        //隐藏按钮和标题
        fullscreen_back.setVisibility(View.INVISIBLE);
        video_title.setVisibility(View.INVISIBLE);
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
            if (mLayout == VideoView.VIDEO_LAYOUT_ZOOM)
                mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
            else
                mLayout++;
            if (videoView != null)
                videoView.setVideoLayout(mLayout, 0);
            return true;
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {

            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();

            if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
                onVolumeSlide((mOldY - y) / windowHeight);
            else if (mOldX < windowWidth / 5.0)// 左边滑动
                onBrightnessSlide((mOldY - y) / windowHeight);
            //左右滑动

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        private int verticalMinDistance = 100;
        private int minVelocity = 0;

        //重写滑动事件
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {

                // 切换Activity
                // Intent intent = new Intent(ViewSnsActivity.this, UpdateStatusActivity.class);
                // startActivity(intent);
//                Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();
                Log.e("123", "左滑");
                //改变进度条
            } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {

                // 切换Activity
                // Intent intent = new Intent(ViewSnsActivity.this, UpdateStatusActivity.class);
                // startActivity(intent);
//                Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();

                Log.e("123", "右滑");
            }
            return false;
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


}
