package com.example.biezhi.videonew.CustomerClass;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.biezhi.videonew.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xiaofeng on 16/4/1.
 */


public class EpisodeListFragment extends Fragment implements AdapterView.OnItemClickListener {
    Context superContext;
    ArrayList<String> vipArray = new ArrayList<>();

    ArrayList<String> episodeNameArray = new ArrayList<>();

    ArrayList<String> episodeNumArray = new ArrayList<>();

    ListView episodeList;

    int currentEpisodePosition;
    OnEpisodeClickListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contextView = inflater.inflate(R.layout.episode_list, container, false);
        superContext = container.getContext();
        Bundle bundle=getArguments();
        vipArray = bundle.getStringArrayList("vipArray");
        episodeNameArray = bundle.getStringArrayList("nameArray");
        episodeNumArray = bundle.getStringArrayList("numArray");
        currentEpisodePosition = bundle.getInt("currentEpisode");
        initList(contextView);
        return contextView;
    }
    public void initList(View contextView)
    {
        if (episodeNumArray.size() >0)
        {
            episodeList = (ListView)contextView.findViewById(R.id.video_episode_list);
            String[] adapterKeys = new String[]{"vipImage", "notShow", "episodeName", "episodeNum"};
            int[] adapterIds = new int[]{R.id.vip_imageView, R.id.not_show, R.id.episode_name, R.id.episode_num};
            SimpleAdapter simpleAdapter = new SimpleAdapter(superContext, getData(), R.layout.episode_adapter, adapterKeys, adapterIds);
            episodeList.setAdapter(simpleAdapter);
            episodeList.setOnItemClickListener(this);
        }
    }
    private List<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map;
        for (int i = 0; i < episodeNameArray.size(); i++) {
            //"vipImage", "notShow", "episodeName", "episodeNum"
            map = new HashMap<>();
            if (vipArray.get(i) == "true") {
                map.put("vipImage", R.drawable.vip);
            } else {
                map.put("vipImage", R.drawable.baiban);
            }
            map.put("notShow", " ");
            map.put("episodeName", episodeNameArray.get(i));
            map.put("episodeNum", episodeNumArray.get(i));
            list.add(map);
        }

        return list;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.OnEpisodeClickListener(position);
    }

    // 定义接口
    public interface OnEpisodeClickListener {
        void OnEpisodeClickListener(int episodeSelect);
    }
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try {
            mListener = (OnEpisodeClickListener) activity;
        } catch (Exception e) {
            // TODO: handle exception
            throw new ClassCastException(activity.toString() + "must implement OnButton2ClickListener");
        }
    }
}
