package com.example.biezhi.videonew.CustomerClass;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.biezhi.videonew.Data;
import com.example.biezhi.videonew.DataModel.RecomModel;
import com.example.biezhi.videonew.MessageBox.HotMessage;
import com.example.biezhi.videonew.MessageBox.PlayUrlMessage;
import com.example.biezhi.videonew.MessageBox.RecomAdapterMessage;
import com.example.biezhi.videonew.NetWorkServer.GetServer;
import com.example.biezhi.videonew.R;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import io.vov.vitamio.utils.Log;

/**
 * Created by xiaofeng on 16/4/22.
 */
public class RecomListFragment extends Fragment {

    PtrClassicFrameLayout ptrFrame;
    Context superContext;
    ListView recomList;
    LayoutInflater inflater;
    int screenWidth;
    int screenHeight;
    Data appData;
    BitmapDrawable holdBD;
    Bitmap holdBM;
    ArrayList<String> hotIdList = new ArrayList<>();
    ArrayList<String> jsonList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contextView = inflater.inflate(R.layout.activity_recom_list, container, false);
        superContext = container.getContext();
        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        hotIdList = bundle.getStringArrayList("hotIds");
        EventBus.getDefault().post(new HotMessage());
        initClass(contextView, superContext);
//        initCate();
        return contextView;
    }

    private void initClass(View contextView, Context superContext) {
        appData = (Data) superContext.getApplicationContext();
        screenHeight = appData.getHeight();
        screenWidth = appData.getWidth();
        holdBM = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.item_bg), screenHeight / 6, screenWidth * 2 / 5, false);
        holdBD = new BitmapDrawable(holdBM);
        recomList = (ListView) contextView.findViewById(R.id.recom_list);
        inflater = (LayoutInflater) superContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ptrFrame = (PtrClassicFrameLayout) contextView.findViewById(R.id.ptr_refresh_recom);
        ptrFrame.setLastUpdateTimeRelateObject(superContext);
        ptrFrame.disableWhenHorizontalMove(false);
        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //处理刷新
                //异步请求网络
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //异步请求网络
//                        new Thread(new getVideoList()).start();
                        EventBus.getDefault().post(new HotMessage());
                    }
                }, 1000);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, recomList, header);
            }
        });
//        RecomListAdapter adapter = new RecomListAdapter();
//        recomList.setAdapter(adapter);

    }

    //设置adapter
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setListAdapter(RecomAdapterMessage begin) {
        RecomListAdapter adapter = new RecomListAdapter();
        recomList.setAdapter(adapter);
    }

    //请求hots
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getHots(HotMessage hotMessage) {
        if (jsonList.size() != 0) {
            jsonList.clear();
        }
        GetServer getServer = new GetServer();
        getServer.aesSecret = "C169F435FEA3530E";
        for (Object hotid : hotIdList) {
            getServer.getUrl = "http://115.29.190.54:99/Recommends.aspx?hotid=" + hotid + "&appid=" + appData.getAppid() + "&version=" + appData.getVersion();
            String json = getServer.getInfoFromServer();
            //将json放入list。然后准备初始化
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
                jsonList.add(json);
            }
        }
        //处理json完毕，准备修改adapter
        EventBus.getDefault().post(new RecomAdapterMessage());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private class RecomListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return hotIdList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.recom_item, parent, false);
            }
            int[] ivIds = new int[]{R.id.recom_image1, R.id.recom_image2, R.id.recom_image3};
            int[] tvIds = new int[]{R.id.recom_videoName_tv1, R.id.recom_videoName_tv2, R.id.recom_videoName_tv3};

            TextView recom_name_tv = ViewHolder.get(convertView, R.id.recom_name_tv);
            recom_name_tv.setText(String.valueOf(position));
            Gson gson = new Gson();
            ImageView imageView;
            TextView textView;
            RecomModel recomModel;
            String json = jsonList.get(position);
            recomModel = gson.fromJson(json, RecomModel.class);
            if (recomModel != null) {
                List<RecomModel.RecommendsEntity> list = recomModel.getRecommends();
                for (int i = 0; i < list.size(); i++) {
                    imageView = ViewHolder.get(convertView, ivIds[i % 3]);
                    textView = ViewHolder.get(convertView, tvIds[i % 3]);
                    //请求图片
                    Glide.with(superContext)
                            .load(list.get(i).getCover())
                            .override(screenHeight / 6, screenWidth * 2 / 5)
                            .centerCrop()
                            .placeholder(holdBD)
                            .crossFade(100)
                            .error(holdBD)
                            .into(imageView);
                    textView.setText(list.get(i).getName());
                    final String finalId = String.valueOf(list.get(i).getId());
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //收集数据，准备跳转

                        }
                    });
                }

            }
            return convertView;
        }
    }

    static class ViewHolder {
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }
}
