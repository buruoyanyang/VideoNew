package com.example.biezhi.videonew;

import android.app.Application;
import android.graphics.Bitmap;


import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhf.
 * BiezhiDebug
 * 2015/11/10 13:54
 */
public class Data extends Application {
    //当前设备的长宽
    int width = 0;
    int height = 0;
    //用户信息
    String userName = "";
    String userPwd = "";
    //html页代码
    String htmlString = "";
    //跳转前页面
    String sourcePage = "";
    //用户vip状态
    boolean userVip = false;
    boolean videoVip = true;
    //分类图片
    List<Bitmap> bitmapList = new ArrayList<Bitmap>();
    //分类名
    List<String> nameList = new ArrayList<String>();
    //cateId列表
    List<String> cateIdList = new ArrayList<String>();
    //被点击的cateId
    String clickedCateId = "";
    //被搜索的Video
    String searchVideo = "";
    //被点击的cateName
    String cateName = "";
    //已加载的VideoID
    List<String> videoIDList = new ArrayList<String>();
    //已加载的VideoBm
    List<Bitmap> videoBmList = new ArrayList<Bitmap>();
    //已加载的VideoName
    List<String> videoNameList = new ArrayList<>();

    //初始化界面获取到的imageUrl
    List<String> imageUrlFromInitView = new ArrayList<>();
    //点击的VideoID
    String clickedVideoID = "";
    //点击的ImageViewID 用来返回的时候回归位置
    int clickedImageID = 0;

    //当前视频的图片
    String videoCover = "";

    //appid
    String appId = "1";

    //app内部版本
    String version = "1.0";

    //当前视频的播放地址
    String playUrl = "";

    //当前视频的播放进度
    int currentPosition = 0;

    String videoName = "";


    public void setWidth(int _width) {
        width = _width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int _height) {
        height = _height;
    }

    public int getHeight() {
        return height;
    }

    public void setUserName(String _userName) {
        userName = _userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserPwd(String _userPwd) {
        userPwd = _userPwd;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setHtmlString(String _htmlString) {
        htmlString = _htmlString;
    }

    public String getHtmlString() {
        return htmlString;
    }

    public void setSourcePage(String _sourcePage) {
        sourcePage = _sourcePage;
    }

    public String getSourcePage() {
        return sourcePage;
    }

    public void setUserVip(boolean _userVip) {
        userVip = _userVip;
    }

    public boolean getUserVip() {
        return userVip;
    }

    public void setVideoVip(boolean _videoVip) {
        videoVip = _videoVip;
    }

    public boolean getVideoVip() {
        return videoVip;
    }

    public void setBitmapList(List<Bitmap> _bitmapList) {
        bitmapList = _bitmapList;
    }

    public List<Bitmap> getBitmapList() {
        return bitmapList;
    }

    public void setNameList(List<String> _nameList) {
        nameList = _nameList;
    }

    public List<String> getNameList() {
        return nameList;
    }

    public void setCateIdList(List<String> _cateIdList) {
        cateIdList = _cateIdList;
    }

    public List<String> getCateIdList() {
        return cateIdList;
    }

    public void setClickedCateId(String _clickedCateId) {
        clickedCateId = _clickedCateId;
    }

    public String getClickedCateId() {
        return clickedCateId;
    }

    public void setSearchVideo(String _searchVideo) {
        searchVideo = _searchVideo;
    }

    public String getSearchVideo() {
        return searchVideo;
    }

    public void setCateName(String _cateName) {
        cateName = _cateName;
    }

    public String getCateName() {
        return cateName;
    }

    public void setVideoIDList(List<String> _videoIDList) {
        videoIDList = _videoIDList;
    }

    public List<String> getVideoIDList() {
        return videoIDList;
    }

    public void setVideoBmList(List<Bitmap> _videoBmList) {
        videoBmList = _videoBmList;
    }

    public List<Bitmap> getVideoBmList() {
        return videoBmList;
    }

    public void setVideoNameList(List<String> _videoNameList) {
        videoNameList = _videoNameList;
    }

    public List<String> getVideoNameList() {
        return videoNameList;
    }

    public void setClickedVideoID(String _clickedVideoID) {
        clickedVideoID = _clickedVideoID;
    }

    public String getClickedVideoID() {
        return clickedVideoID;
    }

    public void setClickedImageID(int _clickedImageID) {
        clickedImageID = _clickedImageID;
    }

    public int getClickedImageID() {
        return clickedImageID;
    }

    public void setImageUrlFromInitView(List<String> _imageUrlList) {
        imageUrlFromInitView = _imageUrlList;
    }

    public List<String> getImageUrlFromInitView() {
        return imageUrlFromInitView;
    }

    public void setVideoCover(String _videoCover) {
        videoCover = _videoCover;
    }

    public String getVideoCover() {
        return videoCover;
    }

    public void setAppId(String _appid) {
        appId = _appid;
    }

    public String getAppid() {
        return appId;
    }

    public void setVersion(String _version) {
        version = _version;
    }

    public String getVersion() {
        return version;
    }

    public void setPlayUrl(String _playUrl) {
        playUrl = _playUrl;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setCurrentPosition(int _currentPosition) {
        currentPosition = _currentPosition;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setVideoName(String _videoName) {
        videoName = _videoName;
    }

    public String getVideoName() {
        return videoName;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }

}
