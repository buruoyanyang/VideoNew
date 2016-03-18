package com.example.biezhi.videonew;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import io.vov.vitamio.widget.VideoView;


/*1，全屏有标题栏
* 2，添加mediacontroller
* 3，上一个界面传参
* */

public class fullScreenPlay extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_play);
        videoView = (VideoView) findViewById(R.id.video_fullScreen_view);
        videoView.setVideoURI(Uri.parse("http://api1.rrmj.tv/api/letvyun/letvmmsid.php?vid=47896295" ));
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

    }
}
