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
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.biezhi.videonew.CustomerClass.AES;
import com.example.biezhi.videonew.CustomerClass.BitmapCut;
import com.example.biezhi.videonew.CustomerClass.SysApplication;
import com.example.biezhi.videonew.DataModel.CateModel;
import com.example.biezhi.videonew.NetWorkServer.GetServer;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;


import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class initActivity extends AppCompatActivity {


    static int screenWidth;
    static int screenHeight;
    Data appData;
    List<String> nameList = new ArrayList<>();        //分类名list
    List<String> urlList = new ArrayList<>();         //地址list
    List<String> cateIdList = new ArrayList<>();      //cateIdList
    List<Bitmap> bitmapList = new ArrayList<>();      //图片list
    boolean isFirst = true; //第一次点击
    private final static String loginMessage = Environment.getExternalStorageDirectory() + "/BiezhiVideo/Message";   //登录信息保存路径
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/BiezhiVideo/Images";      //图片保存路径
    BitmapCut bitmapCut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        SysApplication.getInstance().addActivity(this);
        appData = (Data) this.getApplicationContext();
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);
        initApp();
    }

    private void initApp()
    {
        getResourcesFromDefault();
        bitmapCut = new BitmapCut();
    }

    private void getResourcesFromDefault()
    {

        //获取上一次登录信息
        getSdCard();

        //获取屏幕大小
        if (Integer.valueOf(android.os.Build.VERSION.SDK) > 13 )
        {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y ;
            screenWidth = size.x ;
        }
        else
        {
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
        }
        else
        {
            //todo 请求各个分类
            new Thread(new getTabulation()).start();
        }




    }

    /**
     * 获取当前网络状态
     * @return 网络情况变量
     */
    private boolean checkNetWork() {
        boolean netInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (mobile == NetworkInfo.State.CONNECTED && wifi == NetworkInfo.State.DISCONNECTED) {
            netInfo = true;
            Toast.makeText(this, "当前是3G/4G网络，请注意流量哦o(>﹏<)o", Toast.LENGTH_SHORT).show();
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
            SysApplication.getInstance().exit();
        }
    };

    /**
     * 获取本地信息
     */
    protected void getSdCard() {
        try {
            File dirFile = new File(loginMessage);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
            File file = new File( loginMessage + "Message");
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = "";
            String mimeTypeLine = null ;
            while ((mimeTypeLine = bufferedReader.readLine()) != null) {
                str = str+mimeTypeLine;
            }
            appData.setHtmlString(str);
            //处理Json
            AES aes = new AES();
            String tempJson = appData.getHtmlString().substring(1, appData.getHtmlString().length() - 1);
            tempJson = tempJson.substring(0, tempJson.length() - 1);
            tempJson = tempJson.replaceAll("\"responseData\"", "");
            tempJson = tempJson.substring(2, tempJson.length());
            byte[] tempResult = AES.Decrypt(tempJson, "dd358748fcabdda1");
            tempJson = new String(tempResult,"UTF-8");
            JSONObject jsonObject = new JSONObject(tempJson);
            appData.setUserName(jsonObject.optString("tel"));
            appData.setUserPwd(jsonObject.optString("password"));
            if (jsonObject.optString("vip") == "false")
            {
                appData.setUserVip(false);
            }
            else
            {
                appData.setUserVip(true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        appData.setSourcePage("INIT");
    }

    //异步请求分类，同时请求图片
    protected class getTabulation implements Runnable
    {
        @Override
        public void run()
        {
            GetServer getServer = new GetServer();
            getServer.getUrl = "http://www.biezhi360.cn:99/category.aspx?appid="+appData.getAppid()+"&version="+appData.getVersion();
            getServer.aesSecret = "C169F435FEA3530E";
            String json = getServer.getInfoFromServer();
            if (json.length() < 100)
            {
                //网络请求异常或者其他错误
                switch (json)
                {
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
            }
            else
            {
                //用Gson处理json
                final Gson gson = new Gson();
                CateModel cateModel = gson.fromJson(json,CateModel.class);
                List<CateModel.ContentEntity> contentEntities = cateModel.getContent();
                for (int i = 0;i < contentEntities.size();i++)
                {
                    final CateModel.ContentEntity contentEntity = contentEntities.get(i);
//                    ImageSize imageSize = new ImageSize(screenWidth / 3,screenHeight / 7);
                    //使用imageloader加载图片
                    ImageLoader.getInstance().loadImage(contentEntity.getCover(), new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            if (loadedImage == null) {
                                Resources resources = getResources();
                                loadedImage = BitmapFactory.decodeResource(resources, R.drawable.item_bg);
                            }
                            loadedImage = bitmapCut.setBitmapSize(loadedImage, screenHeight / 7, screenWidth / 3);
                            bitmapList.add(loadedImage);
                            urlList.add(contentEntity.getCover());
                            cateIdList.add(String.valueOf(contentEntity.getCateId()));
                            nameList.add(contentEntity.getName());
                        }
                    });
//                    //将加载完成的图片写入本地
//                    Resources resources = getResources();
//                    bitmapList.add(BitmapFactory.decodeResource(resources, R.drawable.item_bg));
//                    urlList.add(contentEntity.getCover());
//                    cateIdList.add(String.valueOf(contentEntity.getCateId()));
//                    nameList.add(contentEntity.getName());
                }
                //处理完成通知主线程可以跳转了
                Message message = Message.obtain();
                message.what = 1;
                initNetOK.sendMessage(message);
            }
        }
    }



    private Handler initNetOK = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == 1)
            {
                //保存图片到本地
                try
                {
                    for (int i = 0; i< bitmapList.size();i++)
                    {
                        Bitmap tempBit = bitmapList.get(i);
                        saveFile(tempBit, nameList.get(i));
                    }
                }
                catch (Exception e)
                {
                    // TODO: 2016/1/26 保存图片出现异常
                    e.printStackTrace();
                }
                appData.setNameList(nameList);
                appData.setImageUrlFromInitView(urlList);
                appData.setCateIdList(cateIdList);
                appData.setBitmapList(bitmapList);

                startActivity(new Intent(initActivity.this, defaultActivity.class));
                //销毁页面
//                onDestroy();
            }
        }
    };

    /**
     * 保存图片到本地
     * @param bm 图片
     * @param fileName 图片名
     * @return 图片路径
     * @throws IOException
     */
    public String saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + fileName + ".jpg");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return ALBUM_PATH+fileName + ".jpg";
    }
    @Override
    public boolean onKeyDown(int KeyCode,KeyEvent event)
    {
        if (KeyCode == KeyEvent.KEYCODE_BACK)
        {
            if (isFirst)
            {
                isFirst = false;
                Toast.makeText(this, "再按一次返回就退出了哟o(>﹏<)o", Toast.LENGTH_SHORT).show();
            }
            else
            {
                SysApplication.getInstance().exit();
            }
        }
        else
        {
            isFirst = true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_init, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
}
