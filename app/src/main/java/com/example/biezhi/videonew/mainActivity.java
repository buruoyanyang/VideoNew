package com.example.biezhi.videonew;

import android.app.TabActivity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.biezhi.videonew.CustomerClass.SysApplication;


public class mainActivity extends TabActivity implements View.OnClickListener {

    public static String TAB_TAG_RECOM = "recom";
    public static String TAB_TAG_CATE = "cate";
    public static String TAB_TAG_MY = "my";

    private Animation left_in, left_out, right_in, right_out;  //动画
    private Intent intent_recom, intent_cate, intent_my;       //tabhost选项卡
    private ImageView iv_recom, iv_cate, iv_my;                //按钮图标
    private static TabHost tabHost;
    private int currentTab = R.id.ll_recom;
    boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAnim();
        initIntent();
        initClass();
        initTabHost();
    }

    /**
     * 初始化动画效果
     */
    private void initAnim() {
        left_in = AnimationUtils.loadAnimation(this, R.anim.left_in);
        left_out = AnimationUtils.loadAnimation(this, R.anim.left_out);
        right_in = AnimationUtils.loadAnimation(this, R.anim.right_in);
        right_out = AnimationUtils.loadAnimation(this, R.anim.right_out);
    }

    /**
     * 初始化Intent
     */
    private void initIntent() {
        intent_recom = new Intent(this, recomList.class);
        intent_cate = new Intent(this, cateList.class);
        intent_my = new Intent(this, myList.class);
    }

    private void initClass() {
        SysApplication.getInstance().addActivity(this);
        findViewById(R.id.ll_recom).setOnClickListener(this);
        findViewById(R.id.ll_cate).setOnClickListener(this);
        findViewById(R.id.ll_my).setOnClickListener(this);
        iv_recom = (ImageView) findViewById(R.id.imageViewRecom);
        iv_recom.setImageResource(R.drawable.home_press);
        iv_cate = (ImageView) findViewById(R.id.imageViewChanel);
        iv_my = (ImageView) findViewById(R.id.imageViewMy);
    }

    private TabHost.TabSpec buildTabSpec(String tag, int resLable, int resIcon, final Intent content) {
        return tabHost.newTabSpec(tag).setIndicator(getString(resLable), getResources().getDrawable(resIcon)).setContent(content);
    }

    private void initTabHost() {
        tabHost = getTabHost();
        tabHost.addTab(buildTabSpec(TAB_TAG_RECOM, R.string.home_recom, R.drawable.home, intent_recom));
        tabHost.addTab(buildTabSpec(TAB_TAG_CATE, R.string.home_cate, R.drawable.chanel, intent_cate));
        tabHost.addTab(buildTabSpec(TAB_TAG_MY, R.string.home_my, R.drawable.user, intent_my));
    }

    /**
     * 重写tabhost点击事件
     *
     * @param v 视图
     */
    @Override
    public void onClick(View v) {
        if (v.getId() != currentTab) {
            //当前不同tab,处理
            iv_recom.setImageResource(R.drawable.home);
            iv_cate.setImageResource(R.drawable.chanel);
            iv_my.setImageResource(R.drawable.user);
            int checkedId = v.getId();
            //往左翻还是往右翻
            final boolean or;
            or = checkedId < currentTab;
            if (or) {
                tabHost.getCurrentTabView().startAnimation(right_out);
            } else {
                tabHost.getCurrentTabView().startAnimation(left_out);
            }
            switch (checkedId) {
                case R.id.ll_recom:
                    tabHost.setCurrentTabByTag(TAB_TAG_RECOM);
                    iv_recom.setImageResource(R.drawable.home_press);
                    break;
                case R.id.ll_cate:
                    tabHost.setCurrentTabByTag(TAB_TAG_CATE);
                    iv_cate.setImageResource(R.drawable.chanel_press);
                    break;
                case R.id.ll_my:
                    tabHost.setCurrentTabByTag(TAB_TAG_MY);
                    iv_my.setImageResource(R.drawable.user_press);
                    break;
                default:
                    break;
            }
            if (or) {
                tabHost.getCurrentTabView().startAnimation(left_in);
            } else {
                tabHost.getCurrentTabView().startAnimation(right_in);
            }
            currentTab = checkedId;
        }

    }

    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        if (KeyCode == KeyEvent.KEYCODE_BACK) {
            if (isFirst) {
                isFirst = false;
                Toast.makeText(this, "再按一次就退出了哟~", Toast.LENGTH_SHORT).show();
            } else {
                SysApplication.getInstance().exit();
            }
        } else {
            isFirst = true;
        }
        return false;
    }
}
