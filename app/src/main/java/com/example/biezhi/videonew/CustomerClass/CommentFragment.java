package com.example.biezhi.videonew.CustomerClass;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.biezhi.videonew.R;

/**
 * Created by xiaofeng on 16/4/1.
 */


public class CommentFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contextView = inflater.inflate(R.layout.comment_layout, container, false);
        Bundle bundle = getArguments();
        String comment = bundle.getString("comment");
        TextView textView = (TextView)contextView.findViewById(R.id.video_comment);
        textView.setText(comment);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        return contextView;
    }
}
