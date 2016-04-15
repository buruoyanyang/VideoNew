package com.example.biezhi.videonew;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.biezhi.videonew.CustomerClass.GetPlayUrl;
import com.example.biezhi.videonew.DownloadManager.DownloadManager;
import com.example.biezhi.videonew.MessageBox.AfterUrlMessage;
import com.example.biezhi.videonew.MessageBox.TestMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class downloadEpisodeActivity extends AppCompatActivity {

    private ListView episodeList;
    private Button button;
    private int episodeCount;
    private ArrayList<String> pathList;
    Data appData;
    private int siteId = 26;
    private ArrayList<String> downloadUrlList;
    private ArrayList<String> nameList;
    private String videoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_episode);
        appData = (Data) this.getApplicationContext();
        EventBus.getDefault().register(this);
        episodeList = (ListView) findViewById(R.id.download_episodeList);
        button = (Button) findViewById(R.id.download_okButton);
        Intent intent = getIntent();
        episodeCount = intent.getIntExtra("episodeCount", 0);
        pathList = intent.getStringArrayListExtra("downloadUrls");
        downloadUrlList = new ArrayList<>();
        siteId = intent.getIntExtra("siteId", 0);
        videoName = intent.getStringExtra("videoName");
        nameList = intent.getStringArrayListExtra("episodeNameList");
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                getData(),
                R.layout.download_episode_item,
                new String[]{"episode"},
                new int[]{R.id.download_episode_item});
        episodeList.setAdapter(adapter);
        episodeList.setDividerHeight(5);
        EventBus.getDefault().post(new TestMessage("url"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                EventBus.getDefault().unregister(downloadEpisodeActivity.this);
            }
        });
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < episodeCount; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("episode", nameList.get(i));
            list.add(map);
        }
        return list;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getDownloadUrl(TestMessage testMessage) {
        episodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(downloadEpisodeActivity.this.getApplication(),"正在解析下载地址，请稍等",Toast.LENGTH_LONG).show();
            }
        });
        GetPlayUrl getPlayUrl = new GetPlayUrl();
        for (int i = 0; i < pathList.size(); i++) {
            getPlayUrl.setValue(siteId, i + 1, appData.getWidth(), appData.getHeight());
            getPlayUrl.ua = "iPhone";
            getPlayUrl.originPlayUrl = pathList.get(i);
            getPlayUrl.quality = "normal";
            downloadUrlList.add(getPlayUrl.getUrl());
        }
        EventBus.getDefault().post(new AfterUrlMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void urlOK(AfterUrlMessage afterUrlMessage) {
        final File appDir = new File(Environment.getExternalStorageDirectory(), "BieZhi");
        episodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    DownloadManager.getInstance().startDownload(downloadUrlList.get(position), nameList.get(position) + ".mp4", appDir.getAbsolutePath(), true, false, null);
                    episodeList.setSelection(position);
                    Toast.makeText(downloadEpisodeActivity.this.getApplication(), "添加下载成功！", Toast.LENGTH_SHORT).show();
                } catch (DbException ex) {
                    //添加下载失败
                    Toast.makeText(downloadEpisodeActivity.this.getApplication(), "添加下载失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
