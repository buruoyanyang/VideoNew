package com.example.biezhi.videonew.CustomerClass;

import android.app.Fragment;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.biezhi.videonew.Data;
import com.example.biezhi.videonew.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contextView = inflater.inflate(R.layout.activity_cate_list, container, false);
        superContext = container.getContext();
        Bundle bundle=getArguments();
        hotIdList = bundle.getStringArrayList("hotIds");
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
        ptrFrame = (PtrClassicFrameLayout) contextView.findViewById(R.id.ptr_refresh);
        ptrFrame.setLastUpdateTimeRelateObject(superContext);
        ptrFrame.disableWhenHorizontalMove(false);
        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //处理刷新
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, recomList, header);
            }
        });
        RecomListAdapter adapter = new RecomListAdapter();
        recomList.setAdapter(adapter);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private class RecomListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.recom_item, parent, false);
            }

            ImageView imageView = ViewHolder.get(convertView, R.id.recom_image1);
            TextView textView = ViewHolder.get(convertView, R.id.recom_videoName_tv1);
            // TODO: 16/4/22 添加推荐页面
//            Glide.with(superContext)
//                    .load(url)
//                    .override(screenHeight / 6, screenWidth * 2 / 5)
//                    .centerCrop()
//                    .placeholder(holdBD)
//                    .crossFade(100)
//                    .error(holdBD)
//                    .into(imageView);
//            textView.setText(videoName);
            //加载图片
            return null;
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
