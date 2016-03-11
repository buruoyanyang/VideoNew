package com.example.biezhi.videonew;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.SurfaceView;
import android.widget.ImageButton;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaList;

import java.lang.ref.WeakReference;


public class videoPlay extends AppCompatActivity implements SurfaceHolder.Callback,
        IVideoPlayer {

    private String currentPlayUrl;

    private SurfaceView mSurface;
    private SurfaceHolder holder;

    //media player
    private LibVLC libVLC;
    private int mVideoWidth;
    private int mVideoHeight;
    private final static int VideoSizeChanged = -1;
    ImageButton videoPlayOrPauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        initVideoPlayer();
        videoPlayOrPauseButton = (ImageButton)findViewById(R.id.videoPlayOrPause);
        videoPlayOrPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                libVLC.setPosition(90);
            }
        });
    }

    private void initVideoPlayer() {
        currentPlayUrl = "http://113.207.43.146/vplay.aixifan.com/des/20160309/3269154_mp4/3269154_lvbr.mp4?k=02af1b4f500ef72debdd2da0b0f3eea8&t=1457689369";
        mSurface = (SurfaceView) findViewById(R.id.video_surface);
        holder = mSurface.getHolder();
        holder.addCallback(videoPlay.this);
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createPlayer(currentPlayUrl);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if ( libVLC == null)
            return;
        EventHandler.getInstance().removeHandler(mHandler);
        libVLC.stop();
        libVLC.detachSurface();
        holder = null;
        libVLC.closeAout();
        libVLC.destroy();
        libVLC = null;

        mVideoWidth = 0;
        mVideoHeight = 0;

    }

    private void createPlayer(String media) {
        releasePlayer();
        if (media.length() > 10)
        {
            try {
                libVLC = LibVLC.getInstance();
                libVLC.setHardwareAcceleration(LibVLC.HW_ACCELERATION_DISABLED);
                libVLC.setSubtitlesEncoding("");
                libVLC.setAout(LibVLC.AOUT_OPENSLES);
                libVLC.setTimeStretching(true);
                libVLC.setChroma("RV32");
                libVLC.setVerboseMode(true);
                LibVLC.restart(this);
                EventHandler.getInstance().addHandler(mHandler);
                holder.setFormat(PixelFormat.RGBX_8888);
                holder.setKeepScreenOn(true);
                MediaList list = libVLC.getMediaList();
                list.clear();
                list.add(new Media(libVLC, LibVLC.PathToURI(media)), false);
                libVLC.playIndex(0);
                libVLC.getLength();
            } catch (LibVlcException e) {
                e.printStackTrace();
            }
        }


    }

    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoHeight * mVideoWidth <= 1) {
        }

        //获取屏幕大小
        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();

        boolean isPortait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortait || w < h && !isPortait) {
            int i = w;
            w = h;
            h = i;
        }
        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;
        if (screenAR < videoAR) {
            h = (int) (w / videoAR);
        } else {
            w = (int) (h * videoAR);
        }
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();



    }

    /**
     * Surface
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (libVLC != null) {
            libVLC.attachSurface(holder.getSurface(), this);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
        Message msg = Message.obtain(mHandler,VideoSizeChanged,width,height);
        msg.sendToTarget();

    }
    /*************
     * Events
     *************/

    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<videoPlay> mOwner;

        public MyHandler(videoPlay owner) {
            mOwner = new WeakReference<>(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            videoPlay player = mOwner.get();

            // SamplePlayer events
            if (msg.what == VideoSizeChanged) {
                player.setSize(msg.arg1, msg.arg2);
                return;
            }

            // Libvlc events
            Bundle b = msg.getData();
            switch (b.getInt("event")) {
                case EventHandler.MediaPlayerEndReached:
                    player.releasePlayer();
                    break;
                case EventHandler.MediaPlayerPlaying:
                    break;
                case EventHandler.MediaPlayerPaused:
                    break;
                case EventHandler.MediaPlayerStopped:
                    break;
                default:
                    break;
            }
        }
    }
}























































