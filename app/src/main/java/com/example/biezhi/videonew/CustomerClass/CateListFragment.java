package com.example.biezhi.videonew.CustomerClass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.biezhi.videonew.Data;
import com.example.biezhi.videonew.R;
import com.example.biezhi.videonew.videoList;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xiaofeng on 16/3/30.
 * use:cateList界面
 */
public class CateListFragment extends Fragment {
    ImageButton searchButton;
    ImageButton downloadButton;
    GridView gridView;
    Data appData;
    //跳转页面
    private String sourcePage;
    //屏幕大小
    int width;
    int height;
    //分类图片
    List<Bitmap> bitmapList = new ArrayList<>();
    //分类名
    List<String> nameList = new ArrayList<>();
    //分类id
    List<String> cateIdList = new ArrayList<>();

    List<String> urlList = new ArrayList<>();

    Context superContext;
    private LayoutInflater inflater;
    int screenHeight;
    int screenWidth;
    BitmapDrawable holdBD;
    Bitmap holdBM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contextView = inflater.inflate(R.layout.activity_cate_list, container, false);
        superContext = container.getContext();
        initClass(contextView, superContext);
        initCate();
        return contextView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 其他初始化
     */
    private void initClass(View contextView, Context superContext) {
        appData = (Data) superContext.getApplicationContext();
//        Log.e("bitmap", appData.getBitmapList().size());
//        Log.d("nameList", appData.getCateIdList().size());
//        while (appData.getBitmapList().size() < appData.getCateIdList().size()) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        bitmapPath = appData.getImageUrlFromInitView();
        bitmapList = appData.getBitmapList();
        screenHeight = appData.getHeight();
        screenWidth = appData.getWidth();
        cateIdList = appData.getCateIdList();
        nameList = appData.getNameList();
        urlList = appData.getImageUrlFromInitView();
        width = appData.getWidth();
        height = appData.getHeight();
        sourcePage = appData.getSourcePage();
        holdBM = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.item_bg), screenHeight / 6, screenWidth * 2 / 5, false);
        holdBD = new BitmapDrawable(holdBM);
        inflater = (LayoutInflater) superContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gridView = (GridView) contextView.findViewById(R.id.cate_list);

    }

    /**
     * 初始化cate
     */
    private void initCate() {
        //先计算有多少个图片被加载成功
        //计算有多少需要被初始化的频道
        GridViewAdapter gridViewAdapter = new GridViewAdapter();
        gridView.setAdapter(gridViewAdapter);

    }

    private class GridViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return urlList.size();
        }

        @Override
        public Object getItem(int position) {
            return urlList.get(position);
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
                convertView = inflater.inflate(R.layout.cate_adpter, null);
                holder.imageView = (RoundedImageView) convertView.findViewById(R.id.cate_image);
//                holder.imageView = (ImageView) convertView.findViewById(R.id.cate_image);
                holder.textView = (TextView) convertView.findViewById(R.id.cate_name);
//                holder.progressBar = (ProgressBar)convertView.findViewById(R.id.lodingProgressBar);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imageView.setImageBitmap(bitmapList.get(position));
            holder.textView.setText(nameList.get(position));
//            holder.progressBar.setVisibility(View.INVISIBLE);
            final int postNum = position;
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加点击事件获取cateId
                    //准备跳转页面的内容
                    appData.setClickedCateId(String.valueOf(cateIdList.get(postNum)));
                    appData.setCateName(nameList.get(postNum));
                    appData.setSourcePage("CateList");
                    startActivity(new Intent(superContext, videoList.class));
                }
            });
            return convertView;
        }

    }

    static class ViewHolder {
        RoundedImageView imageView;
//        ImageView imageView;
        TextView textView;
        ProgressBar progressBar;
    }

}
