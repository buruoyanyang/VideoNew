package com.example.biezhi.videonew;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biezhi.videonew.CustomerClass.CateListFragment;
import com.example.biezhi.videonew.CustomerClass.RecomListFragment;
import com.example.biezhi.videonew.MessageBox.AfterUrlMessage;
import com.example.biezhi.videonew.MessageBox.TestMessage;
import com.example.biezhi.videonew.NetWorkServer.GetServer;
import com.rey.material.widget.TabPageIndicator;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private ImageView left_menuBack;
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
    private ImageButton downloadButton;
    private ImageButton cacheButton;
    private ImageButton searchButton;
    Data appData;
    private static String deviceId = "";

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
        setContentView(R.layout.activity_default);
        EventBus.getDefault().register(this);
        appData = (Data) this.getApplicationContext();
        initClass();
        if (appData.sourcePage == "Login") {
            initFromLogin();
        }
        else if (appData.sourcePage == "INIT") {
            initFromLogin();
        }
        else
        {
            initFromOthers();
        }
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

    private void initFromOthers()
    {
        if (appData.getUserName() != "")
        {
            SimpleAdapter adapter = new SimpleAdapter(
                    defaultActivity.this,
                    getDataWithUser(),
                    R.layout.drawlayout_item, new String[]{"img", "name"},
                    new int[]{R.id.drawer_layout_image, R.id.drawer_layout_text}
            );
            left_menuList.setDividerHeight(10);
            left_menuList.setAdapter(adapter);
            EventBus.getDefault().post(new TestMessage("test"));
        }
    }
    private void initFromLogin() {
        if (appData.getUserName() != "") {
            //通知服务器登陆，进行二次验证
            EventBus.getDefault().post(new TestMessage("test"));
        }
    }



    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void trackToServer(TestMessage testMessage) {
        GetServer getServer = new GetServer();
        getServer.getUrl = "http://115.29.190.54:12345/mLogin.aspx?tel=" + appData.getUserName() + "&password=" + appData.getUserPwd() + "&idfa=" + appData.getDeviceId();
        getServer.aesSecret = "dd358748fcabdda1";
        String json = getServer.getTrachFromServer();
        if (json.length() < 10) {
            //账号异常，登陆失效
            appData.setUserName("");
            appData.setUserPwd("");
            appData.setUserVip(false);
        }
        //通知修改登陆信息ui
        EventBus.getDefault().post(new AfterUrlMessage());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initFromLogin(AfterUrlMessage afterUrlMessage)
    {
        SimpleAdapter adapter = new SimpleAdapter(
                defaultActivity.this,
                getDataWithUser(),
                R.layout.drawlayout_item, new String[]{"img", "name"},
                new int[]{R.id.drawer_layout_image, R.id.drawer_layout_text}
        );
        left_menuList.setDividerHeight(10);
        left_menuList.setAdapter(adapter);
        //同时将信息写入本地文件
        saveInfoToGallery("UI", "BieZhi", appData.getHtmlString());
//        appData.setHtmlString("");
    }

    private void deleteInfoFromGallery(String fileName, String dirName) {
        File appDir = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!appDir.exists())
            return;
        File file = new File(appDir, fileName);
        if (file.exists())
            file.delete();

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

    private void initClass() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        title_iconButton = (ImageButton) findViewById(R.id.title_icon);
        title_text = (TextView) findViewById(R.id.title_appName);
        left_menuList = (ListView) findViewById(R.id.left_menu_list);
        left_menuBack = (ImageView) findViewById(R.id.left_menu_back);
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
//                Toast.makeText(getApplicationContext(), "敬请期待", Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    if (appData.getUserName() != "") {
                        //已经处于登陆状态，点击登出
                        appData.setUserPwd("");
                        appData.setUserName("");
                        appData.setUserVip(false);
                        appData.setHtmlString("");
                        //删除本地文件
                        deleteInfoFromGallery("UI", "BieZhi");
                    }
                    startActivity(new Intent(defaultActivity.this, loginActivity.class));
                } else if (position == 1) {
                    startActivity(new Intent(defaultActivity.this, favoriteActivity.class));
                }

            }
        });
        downloadButton = (ImageButton) findViewById(R.id.title_download);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.setSourcePage("Default");
                startActivity(new Intent(defaultActivity.this, downloadActivity.class));
            }
        });
        cacheButton = (ImageButton) findViewById(R.id.title_cache);
        cacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.setSourcePage("Default");
                startActivity(new Intent(defaultActivity.this, cacheActivity.class));
            }
        });
        searchButton = (ImageButton) findViewById(R.id.title_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.setSourcePage("Default");
                startActivity(new Intent(defaultActivity.this, searchActivity.class));
            }
        });
        deviceId = appData.getDeviceId();

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

    private List<Map<String, Object>> getDataWithUser() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < imageId.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("img", imageId[i]);
            if (i == 0 && appData.getUserName() != "") {
                map.put("name", appData.getUserName());
            } else {
                map.put("name", choiseName[i]);
            }
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
            if(position ==1) {
                Fragment fragment = new CateListFragment();
                return fragment;
            }
            else
            {
                Fragment fragment = new RecomListFragment();
                ArrayList<String> hotIdList = new ArrayList<>();
                hotIdList.add("382");
                hotIdList.add("354");
                hotIdList.add("36");
                hotIdList.add("381");
                hotIdList.add("320");
                hotIdList.add("42");
                hotIdList.add("379");
                hotIdList.add("268");
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("hotIds", hotIdList);
                fragment.setArguments(bundle);
                return fragment;
            }
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

    boolean isFirst = true;

    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        if (KeyCode == KeyEvent.KEYCODE_BACK) {
            if (isFirst) {
                isFirst = false;
                Toast.makeText(this, "再按一次返回就退出了哟", Toast.LENGTH_SHORT).show();
            } else {
                appData.setExsit(true);
                finish();
            }
        } else {
            appData.setExsit(false);
            isFirst = true;
        }
        return false;
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}