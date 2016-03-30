package com.example.biezhi.videonew.CustomerClass;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.biezhi.videonew.R;

/**
 * Created by xiaofeng on 16/3/30.
 */
public class ItemFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contextView = inflater.inflate(R.layout.activity_recom_list, container, false);
        TextView mTextView = (TextView) contextView.findViewById(R.id.textview);
        Button mButton = (Button)contextView.findViewById(R.id.test_button);
        //获取Activity传递过来的参数
        Bundle mBundle = getArguments();
        String title = mBundle.getString("arg");
        mTextView.setText(title);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("123","123");
            }
        });
        return contextView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
