package com.example.biezhi.videonew;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.StatFs;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biezhi.videonew.DownloadManager.DownloadInfo;
import com.example.biezhi.videonew.DownloadManager.DownloadManager;
import com.example.biezhi.videonew.DownloadManager.DownloadState;
import com.example.biezhi.videonew.DownloadManager.DownloadViewHolder;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

public class downloadActivity extends AppCompatActivity {

    private ListView downloadList;

    private DownloadManager downloadManager;
    private DownloadListAdapter downloadListAdapter;

    private Button deleteButton;

    private TextView downloadNO;

    private ProgressBar progressBar;

    private TextView titleName;

    private ImageButton backButton;

    Data appData;

    @Override
    protected void onStart() {
        super.onStart();
        if (appData.getExsit()) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        downloadList = (ListView) findViewById(R.id.download_list);
        downloadNO = (TextView)findViewById(R.id.download_no);
        progressBar = (ProgressBar)findViewById(R.id.disk_free_num);
        deleteButton = (Button)findViewById(R.id.title_delete);
        deleteButton.setVisibility(View.INVISIBLE);
        titleName = (TextView)findViewById(R.id.title_appName);
        backButton = (ImageButton) findViewById(R.id.title_icon);
        appData = (Data) this.getApplicationContext();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleName.setText("缓存");
        downloadManager = DownloadManager.getInstance();
        if (downloadManager.getDownloadListCount() ==0)
        {
            downloadList.setVisibility(View.INVISIBLE);
            downloadNO.setVisibility(View.VISIBLE);
        }
        else
        {
            downloadList.setVisibility(View.VISIBLE);
            downloadNO.setVisibility(View.INVISIBLE);
        }
        downloadListAdapter = new DownloadListAdapter();
        downloadList.setAdapter(downloadListAdapter);
        float size1 = getSDCardTotalSize();
        float size2 = getSDCardFreeSize();
        progressBar.setMax((int) size1);
        progressBar.setProgress((int)size1 - (int)size2);

    }

    private float getSDCardFreeSize()
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File path = Environment.getExternalStorageDirectory();
            StatFs statfs = new StatFs(path.getPath());
            long blockSize = statfs.getBlockSize();
            long availableBlocks = statfs.getAvailableBlocks();
            long size = (availableBlocks * blockSize)/1024/1024;
            return (float) size;
        }
        return 1;
    }

    private float getSDCardTotalSize()
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getBlockCount();
            long size = (availableBlocks * blockSize)/1024/1024;
            return (float) size;
        }
        return 1;
    }
    private class DownloadListAdapter extends BaseAdapter {

        private Context mContext;
        private final LayoutInflater mInflater;

        private DownloadListAdapter() {
            mContext = getBaseContext();
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            if (downloadManager == null)
                return 0;
            return downloadManager.getDownloadListCount();
        }

        @Override
        public Object getItem(int position) {
            return downloadManager.getDownloadInfo(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DownloadItemViewHolder holder;
            DownloadInfo downloadInfo = downloadManager.getDownloadInfo(position);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.download_item_default, null);
                holder = new DownloadItemViewHolder(convertView, downloadInfo);
                convertView.setTag(holder);
                holder.refresh();
            } else {
                holder = (DownloadItemViewHolder) convertView.getTag();
                holder.update(downloadInfo);
            }
            if (downloadInfo.getState().value() < DownloadState.FINISHED.value()) {
                try {
                    downloadManager.startDownload(
                            downloadInfo.getUrl(),
                            downloadInfo.getLabel(),
                            downloadInfo.getFileSavePath(),
                            downloadInfo.isAutoResume(),
                            downloadInfo.isAutoRename(),
                            holder);
                } catch (DbException ex) {
                    Toast.makeText(x.app(), "添加下载失败", Toast.LENGTH_LONG).show();
                }
            }
            return convertView;
        }
    }

    public class DownloadItemViewHolder extends DownloadViewHolder {

        @ViewInject(R.id.download_label)
        TextView label;
        @ViewInject(R.id.download_state)
        TextView stateLabel;
        @ViewInject(R.id.download_pb)
        ProgressBar progressBar;
        @ViewInject(R.id.download_stopOrStart)
        Button stopBtn;

        public DownloadItemViewHolder(View view, DownloadInfo downloadInfo) {
            super(view, downloadInfo);
            refresh();
        }

        @Event(R.id.download_stopOrStart)
        private void toggleEvent(View view) {
            DownloadState state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                case STARTED:
                    downloadManager.stopDownload(downloadInfo);
                    break;
                case ERROR:
                case STOPPED:
                    try {
                        downloadManager.startDownload(
                                downloadInfo.getUrl(),
                                downloadInfo.getLabel(),
                                downloadInfo.getFileSavePath(),
                                downloadInfo.isAutoResume(),
                                downloadInfo.isAutoRename(),
                                this);
                    } catch (DbException ex) {
                        Toast.makeText(x.app(), "添加下载失败", Toast.LENGTH_LONG).show();
                    }
                    break;
                case FINISHED:
                    Toast.makeText(x.app(), "下载已完成", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

        @Event(R.id.download_remove_btn)
        private void removeEvent(View view) {
            try {
                downloadManager.removeDownload(downloadInfo);
                downloadListAdapter.notifyDataSetChanged();
            } catch (DbException e) {
                Toast.makeText(x.app(), "移除任务失败", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void update(DownloadInfo downloadInfo) {
            super.update(downloadInfo);
            refresh();
        }

        @Override
        public void onWaiting() {
            refresh();

        }

        @Override
        public void onStarted() {
            refresh();

        }

        @Override
        public void onLoading(long total, long current) {
            refresh();

        }

        @Override
        public void onSuccess(File result) {
            refresh();

        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            refresh();

        }

        @Override
        public void onCancelled(Callback.CancelledException cex) {
            refresh();
        }

        public void refresh() {
            label.setText(downloadInfo.getLabel());
            stateLabel.setText(downloadInfo.getState().toString());
            progressBar.setProgress(downloadInfo.getProgress());

            stopBtn.setVisibility(View.VISIBLE);
//            stopBtn.setText(x.app().getString(R.string.stop));
            DownloadState state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                case STARTED:
//                    stopBtn.setText(x.app().getString(R.string.stop));
                    stateLabel.setText("下载中");
                    break;
                case ERROR:
                case STOPPED:
//                    stopBtn.setText(x.app().getString(R.string.start));
                    stateLabel.setText("暂停中");
                    break;
                case FINISHED:
//                    stopBtn.setVisibility(View.INVISIBLE);
                    stopBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("已完成","跳转播放链接");
                            Log.e("播放地址",downloadInfo.getFileSavePath());
                            appData.setVideoVip(false);
                            appData.setPlayUrl(downloadInfo.getFileSavePath()+"/"+downloadInfo.getLabel());
                            appData.setCurrentPosition(0);
                            appData.setVideoName(label.getText().toString());
                            appData.setSourcePage("Download");
                            startActivity(new Intent(downloadActivity.this, fullScreenPlay.class));
                        }
                    });
                    break;
                default:
//                    stopBtn.setText(x.app().getString(R.string.start));
                    stateLabel.setText("暂停中");
                    break;

            }
        }
    }
}





































