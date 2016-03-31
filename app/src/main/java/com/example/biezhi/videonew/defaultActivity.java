package com.example.biezhi.videonew;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biezhi.videonew.CustomerClass.CateListFragment;
import com.rey.material.widget.TabPageIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class defaultActivity extends FragmentActivity {


    private static final String[] TITLE = new String[]{"推荐", "频道"};
    private DrawerLayout drawerLayout;
    private ImageButton title_iconButton;
    private TextView title_text;
    private ListView left_menuList;
    private int[] imageId = new int[]{
            R.drawable.login_icon,
            R.drawable.pen_icon,
            R.drawable.settings_icon,
            R.drawable.feedback,
            R.drawable.update_icon,
            R.drawable.about_icon
    };
    private String[] choiseName = new String[]{
            "登陆/注册",
            "我的收藏",
            "设置",
            "意见反馈",
            "检查新版本",
            "关于我们"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        initClass();

        //ViewPager的adapter
        FragmentPagerAdapter adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        //实例化TabPageIndicator然后设置ViewPager与之关联
        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        //如果我们要对ViewPager设置监听，用indicator设置就行了
        indicator.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                Toast.makeText(getApplicationContext(), TITLE[arg0], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    private void initClass() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        title_iconButton = (ImageButton) findViewById(R.id.title_icon);
        title_text = (TextView) findViewById(R.id.title_appName);
        left_menuList = (ListView) findViewById(R.id.left_menu_list);
        title_iconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        title_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        SimpleAdapter adapter = new SimpleAdapter(
                defaultActivity.this,
                getData(),
                R.layout.drawlayout_item, new String[]{"img", "name"},
                new int[]{R.id.drawer_layout_image, R.id.drawer_layout_text}
        );
        //设置行间距
        left_menuList.setDividerHeight(10);
        left_menuList.setAdapter(adapter);
        left_menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //设置点击功能
                Toast.makeText(getApplicationContext(), "敬请期待", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < imageId.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("img", imageId[i]);
            map.put("name", choiseName[i]);
            list.add(map);
        }
        return list;
    }

    /**
     * ViewPager适配器
     */
    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new CateListFragment();
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLE[position % TITLE.length];
        }

        @Override
        public int getCount() {
            return TITLE.length;
        }
    }

}
































