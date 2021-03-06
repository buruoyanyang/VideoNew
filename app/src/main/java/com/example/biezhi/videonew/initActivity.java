package com.example.biezhi.videonew;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.biezhi.videonew.CustomerClass.AES;
import com.example.biezhi.videonew.CustomerClass.BitmapCut;
import com.example.biezhi.videonew.CustomerClass.ImageService;
import com.example.biezhi.videonew.DataModel.CateModel;
import com.example.biezhi.videonew.DataModel.LoginModel;
import com.example.biezhi.videonew.DataModel.WeiXinModel;
import com.example.biezhi.videonew.MessageBox.AfterUrlMessage;
import com.example.biezhi.videonew.MessageBox.EpisodeMessage;
import com.example.biezhi.videonew.MessageBox.TestMessage;
import com.example.biezhi.videonew.NetWorkServer.GetServer;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class initActivity extends AppCompatActivity {


    int screenWidth;
    int screenHeight;
    Data appData;
    List<String> nameList = new ArrayList<>();        //分类名list
    List<String> urlList = new ArrayList<>();         //地址list
    List<String> cateIdList = new ArrayList<>();      //cateIdList
    List<Bitmap> bitmapList = new ArrayList<>();      //图片list
    boolean isFirst = true; //第一次点击退出
//    private final static String loginMessage = Environment.getExternalStorageDirectory() + "/BiezhiVideo/Message";   //登录信息保存路径
//    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/BiezhiVideo/Images";      //图片保存路径
    BitmapCut bitmapCut;
    private String deviceId;
    private String userName;
    private String userPwd;
    private boolean userIsVip;
    private boolean isExUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        appData = (Data) this.getApplicationContext();
        EventBus.getDefault().register(this);
        initApp();
    }

    private void initApp() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        appData.setDeviceId(tm.getDeviceId());
        deviceId = appData.getDeviceId();
        userName = "";
        userPwd = "";
        userIsVip = false;
        isExUser = false;
        bitmapCut = new BitmapCut();
        EventBus.getDefault().post(deviceId);
        getResourcesFromDefault();
    }


    private void getResourcesFromDefault() {

        //获取上一次登录信息
        getSdCard();
        //请求验证接口
        //获取屏幕大小
        if (Integer.valueOf(android.os.Build.VERSION.SDK) > 13) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
            screenWidth = size.x;
        } else {
            screenWidth = getWindowManager().getDefaultDisplay().getWidth();
            screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        }
        appData.setHeight(screenHeight);
        appData.setWidth(screenWidth);

        //获取当前网络状态
        if (!checkNetWork()) {
            AlertDialog netWorkInfo = new AlertDialog.Builder(this).create();
            netWorkInfo.setTitle("网络开小差了...");
            netWorkInfo.setMessage("无网络连接，快去打开WIFI吧~");
            netWorkInfo.setButton("好", listener);
            netWorkInfo.show();
        } else {
            //请求各个分类
            EventBus.getDefault().post(new AfterUrlMessage());
            //验证账号使用情况
//            EventBus.getDefault().post(new TestMessage("test"));

        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getWeiXin(String deviceId)
    {
        GetServer getServer = new GetServer();
        getServer.getUrl = "http://115.29.190.54:99/idfa.aspx?idfa="+deviceId;
        getServer.aesSecret = "";
        String json = getServer.getInfoFromServerWithNoSecret();
        if (json.length() > 10)
        {
            //解析json
            Gson gson = new Gson();
            WeiXinModel weiXinModel = gson.fromJson(json,WeiXinModel.class);
            appData.setWeixinId(weiXinModel.getWx());
        }
    }

    //验证账号使用
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void trackToServer(TestMessage testMessage) {
        GetServer getServer = new GetServer();
        getServer.getUrl = "http://115.29.190.54:12345/mLogin.aspx?tel=" + appData.getUserName() + "&password=" + appData.getUserPwd() + "&idfa=" + appData.getDeviceId();
//        getServer.aesSecret = "C169F435FEA3530E";
        getServer.aesSecret = "dd358748fcabdda1";
//        getServer.getInfoFromServer();
        String json = getServer.getTrachFromServer();
        if (json.length() < 10) {
            //账号异常，登陆失效
            userName = "";
            userPwd = "";
            userIsVip = false;
            appData.setUserName(userName);
            appData.setUserPwd(userPwd);
            appData.setUserVip(userIsVip);
        }
    }


    /**
     * 获取当前网络状态
     *
     * @return 网络情况变量
     */
    private boolean checkNetWork() {
        boolean netInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (mobile == NetworkInfo.State.CONNECTED && wifi == NetworkInfo.State.DISCONNECTED) {
            netInfo = true;
            Toast.makeText(this, "当前是3G/4G网络，请注意流量哦", Toast.LENGTH_SHORT).show();
        } else netInfo = wifi == NetworkInfo.State.CONNECTED;
        return netInfo;
    }

    /**
     * 网络状态弹窗点击listener
     */
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        }
    };

    /**
     * 获取本地信息
     */
    protected void getSdCard() {
        File appDir = new File(Environment.getExternalStorageDirectory(), "BieZhi");
        if (appDir.exists()) {
            File file = new File(appDir, "UI");
            if (file.exists()) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String str = "";
                    String mimeTypeLine;
                    while ((mimeTypeLine = bufferedReader.readLine()) != null) {
                        str = str + mimeTypeLine;
                    }
                    appData.setHtmlString(str);
                    JSONObject jsonObject = new JSONObject(str);
                    String responseData = jsonObject.optString("responseData");
                    //解密
                    //getServer.aesSecret = "C169F435FEA3530E";
                    byte[] tempResult = AES.Decrypt(responseData, "dd358748fcabdda1");
//                    byte[] tempResult = AES.Decrypt(responseData, "C169F435FEA3530E");
                    responseData = new String(tempResult, "UTF-8");
                    if (responseData.length() > 200) {
                        responseData = responseData.split("\\},\\{")[0];
                        responseData = responseData + "}";
                    }
                    Gson gson = new Gson();
                    LoginModel loginModel = gson.fromJson(responseData, LoginModel.class);
                    String tel = loginModel.getTel();
                    String password = loginModel.getPassword();
                    boolean vip = loginModel.isVip();
                    appData.setUserName(tel);
                    appData.setUserPwd(password);
                    appData.setUserVip(vip);
                    userName = tel;
                    userPwd = password;
                    userIsVip = vip;
                    EventBus.getDefault().post(new TestMessage("track"));
                    inputStreamReader.close();
                } catch (Exception ex) {
                    appData.setHtmlString("");
                    appData.setUserName("");
                    appData.setUserPwd("");
                    appData.setUserVip(false);
                    userName = "";
                    userPwd = "";
                    userIsVip = false;
                }
            }
        }
        appData.setSourcePage("INIT");
    }

    //请求分类信息
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getTabulation(AfterUrlMessage afterUrlMessage) {
        GetServer getServer = new GetServer();
        getServer.getUrl = "http://www.biezhi360.cn:99/category.aspx?appid=" + appData.getAppid() + "&version=" + appData.getVersion();
        getServer.aesSecret = "C169F435FEA3530E";
        String json = getServer.getInfoFromServer();
        if (json.length() < 100) {
            switch (json) {
                case "0":
                    //服务器连接失败
                    break;
                case "1":
                    //io读写错误
                    break;
                case "2":
                    //解密错误
                    break;
            }
        } else {
            Gson gson = new Gson();
            CateModel cateModel = gson.fromJson(json, CateModel.class);
            List<CateModel.ContentEntity> contentEntities = cateModel.getContent();
            for (int i = 0; i < contentEntities.size(); i++) {
                CateModel.ContentEntity contentEntity = contentEntities.get(i);
                urlList.add(contentEntity.getCover());
                cateIdList.add(String.valueOf(contentEntity.getCateId()));
                Bitmap loadedImage;
                nameList.add(contentEntity.getName());
                try {
                    byte data[] = ImageService.getImage(urlList.get(i));
                    loadedImage = bitmapCut.setBitmapSize(BitmapFactory.decodeByteArray(data, 0, data.length), screenHeight / 6, screenWidth * 2 / 5);
                    bitmapList.add(loadedImage);
                } catch (Exception e) {
                    Resources resources = getResources();
                    loadedImage = BitmapFactory.decodeResource(resources, R.drawable.item_bg);
                    loadedImage = bitmapCut.setBitmapSize(loadedImage, screenHeight / 6, screenWidth * 2 / 5);
                    bitmapList.add(loadedImage);
                }
            }
            //通知主线程 修改ui
            EventBus.getDefault().post(new EpisodeMessage(null));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initNetOK(EpisodeMessage episodeMessage) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < bitmapList.size(); i++) {
            list.add(saveImageToGallery(bitmapList.get(i), nameList.get(i) + ".jpg", "BieZhi") + "/"+nameList.get(i) + ".jpg");
        }
        appData.setNameList(nameList);
        appData.setImageUrlFromInitView(list);
//        list.clear();
        appData.setCateIdList(cateIdList);
        appData.setBitmapList(bitmapList);
//        bitmapList.clear();
        appData.setExUser(isExUser);
        appData.setUserName(userName);
        appData.setUserPwd(userPwd);
        appData.setUserVip(userIsVip);
        startActivity(new Intent(initActivity.this, defaultActivity.class));
        finish();
    }

    /**
     * 保存图片到本地
     *
     * @param bmp      被保存图片
     * @param fileName 文件名
     */
    public String saveImageToGallery(Bitmap bmp, String fileName, String dirName) {
        //只有在图片不存在的情况下才将图片写入本地
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), dirName);
        String path = appDir.getAbsolutePath();
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        if (!file.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 其次把文件插入到系统图库
//            try {
//                MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                        file.getAbsolutePath(), fileName, null);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
        }
        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        return path;
    }

    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        if (KeyCode == KeyEvent.KEYCODE_BACK) {
            if (isFirst) {
                isFirst = false;
                Toast.makeText(this, "再按一次返回就退出了哟", Toast.LENGTH_SHORT).show();
            }
        } else {
            isFirst = true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_init, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        bitmapList.clear();
    }
}
