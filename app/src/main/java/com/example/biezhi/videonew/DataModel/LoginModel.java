package com.example.biezhi.videonew.DataModel;

/**
 * Created by xiaofeng on 16/4/6.
 */
public class LoginModel {

    /**
     * id : 175969
     * weixin :
     * tel : 18580434395
     * password : 123456
     * vip : true
     * beginTime : 2015-11-09T13:35:27.68
     * endTime : 2016-11-09T13:18:21.47
     * score : 0
     * operator : null
     */

    private int id;
    private String weixin;
    private String tel;
    private String password;
    private boolean vip;
    private String beginTime;
    private String endTime;
    private int score;
    private Object operator;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Object getOperator() {
        return operator;
    }

    public void setOperator(Object operator) {
        this.operator = operator;
    }
}
