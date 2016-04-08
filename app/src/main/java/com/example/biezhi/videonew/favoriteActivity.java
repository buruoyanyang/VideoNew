package com.example.biezhi.videonew;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.example.biezhi.videonew.DataModel.FavoriteModel;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class favoriteActivity extends AppCompatActivity {

    private static ListView listView;
    private static List<FavoriteModel.VideosEntity> videos;
    private static LayoutInflater inflater;
    private static ListAdapter adapter;
    private static ImageButton imageButton;
    private static TextView textView;
    private static FavoriteModel favoriteModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        getSdCard();
        initClass();

    }

    private void initClass() {
        listView = (ListView) findViewById(R.id.favorite_list);
        imageButton = (ImageButton) findViewById(R.id.title_icon);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        textView = (TextView) findViewById(R.id.download_no);
        adapter = new ListAdapter();
        listView.setAdapter(adapter);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (videos != null) {
            textView.setVisibility(View.INVISIBLE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    private class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (videos == null) {
                return 0;
            } else {
                return videos.size();
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.favorite_item, null);
                holder.buttonVideo = (Button) convertView.findViewById(R.id.favorite_video);
                holder.buttonDelete = (Button) convertView.findViewById(R.id.favorite_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.buttonVideo.setText(videos.get(position).getVideoName());
            holder.buttonVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("123", videos.get(position).getCover());
                    //todo 准备数据，进行跳转
                }
            });
            holder.buttonDelete.setText("移除");
            holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("123", "delete");
                    videos.remove(position);
                    favoriteModel.setVideos(videos);
                    adapter.notifyDataSetChanged();
                    listView.invalidateViews();
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        Button buttonVideo;
        Button buttonDelete;
    }

    protected void getSdCard() {
        File appDir = new File(Environment.getExternalStorageDirectory(), "BieZhi");
        String jsonStr = "";
        if (appDir.exists()) {
            File file = new File(appDir, "FA");
            if (file.exists()) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String mimeTypeLine;
                    while ((mimeTypeLine = bufferedReader.readLine()) != null) {
                        jsonStr = jsonStr + mimeTypeLine;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
//        jsonStr = "{\"videos\":[{\"videoId\":237,\"videoName\":\"名字\",\"cover\":\"全部\"},{\"videoId\":237,\"videoName\":\"名字\",\"cover\":\"全部\"}]}";
        if (jsonStr != "") {
            try {
                Gson gson = new Gson();
                favoriteModel = gson.fromJson(jsonStr, FavoriteModel.class);
                if (favoriteModel.getVideos().size() != 0) {
                    videos = favoriteModel.getVideos();
                }
            } catch (Exception ex) {
                favoriteModel = null;
                videos = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Gson gson = new Gson();
        //将最新的信息重新写入本地
        if (favoriteModel == null) {
            saveInfoToGallery("FA", "BieZhi", "");
        } else {
            saveInfoToGallery("FA", "BieZhi", gson.toJson(favoriteModel));
        }
    }

    private String saveInfoToGallery(String fileName, String dirName, String writeFile) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(writeFile.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appDir.getAbsolutePath() + "/" + fileName;
    }
}
