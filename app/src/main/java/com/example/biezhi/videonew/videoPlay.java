package com.example.biezhi.videonew;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;


public class videoPlay extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener
        , MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, View.OnClickListener {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        initPlayer();
    }

    private void initPlayer() {
        Vitamio.initialize(videoPlay.this);
        videoView = (VideoView) findViewById(R.id.video_surface);
        videoView.setVideoURI(Uri.parse("http://play.g3proxy.lecloud.com/vod/v2/MTgzLzUyLzExMS9sZXR2LXV0cy8yMC92ZXJfMDBfMjItMTAzMTU5MTQwNy1hdmMtNDE0NDU5LWFhYy0zMjAwMC01ODA5MzIwLTMzMDYxMDkzNi0wOWIwM2EwMWEzMTk3MDE4MDFkMzNmMjRhMmQxMDVkOS0xNDU3ODUyMzM0Njc3Lm1wNA==?b=455&mmsid=48400688&tm=1457929491&key=06124a5108b4f698b9f5edf9af2b9d51&platid=14&splatid=1403&playid=0&tss=ios&vtype=13&cvid=65418574921&payff=0&pip=d63fd5ef507e6b0cf5b10d9d660984a3&tag=macos&sign=webdisk_165961447&termid=2&pay=0&ostype=macos&hwtype=un&uid=165961447&token=6a63be768ae75c158ffee2ec2349be20"));
//        videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
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

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onClick(View v) {

    }
}
