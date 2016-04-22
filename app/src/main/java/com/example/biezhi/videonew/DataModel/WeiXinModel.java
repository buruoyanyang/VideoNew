package com.example.biezhi.videonew.DataModel;

/**
 * Created by xiaofeng on 16/4/22.
 */
public class WeiXinModel {

    /**
     * code : 1
     * wx : xiaoyingjielove
     * isMarked : True
     * isWeixin : True
     * banner : http://115.29.190.54:1987/image/flash/xiaoyingjielove.png
     */

    private int code;
    private String wx;
    private String isMarked;
    private String isWeixin;
    private String banner;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getWx() {
        return wx;
    }

    public void setWx(String wx) {
        this.wx = wx;
    }

    public String getIsMarked() {
        return isMarked;
    }

    public void setIsMarked(String isMarked) {
        this.isMarked = isMarked;
    }

    public String getIsWeixin() {
        return isWeixin;
    }

    public void setIsWeixin(String isWeixin) {
        this.isWeixin = isWeixin;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }
}
