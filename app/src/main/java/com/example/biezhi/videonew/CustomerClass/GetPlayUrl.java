package com.example.biezhi.videonew.CustomerClass;

import org.json.JSONObject;

/**
 * Created by xhf.
 * BiezhiVideo
 * 2015/11/5 12:38
 */
public class GetPlayUrl {
    int siteId = 0;
    int episode = 1;
    int width = 768;
    int height = 1024;
    public String ua = "";
    public String originPlayUrl = "";
    public String quality = "normal";
    String video_quality = "video_1";
    public void setValue(int _siteId,int _episode,int _width,int _height)
    {
        siteId = _siteId;
        episode = _episode;
        width = _width;
        height = _height;
    }
    public String getUrl()
    {
        String playUrl;
        switch (siteId)
        {
            case 12:
                // TODO: 2015/11/5 好像要发一个http头
                playUrl = "http://flv.wandoujia.com/hack/flv/download?url=" + originPlayUrl + "&format=" + quality + "&f=iphone&v=2.2.0";
                /*DefaultNetServer _defaultNetServer = new DefaultNetServer();
                _defaultNetServer.Url = playUrl;
                _defaultNetServer.getPlayUrl();
                playUrl = _defaultNetServer.result;*/
                break;
            case 26:
                playUrl = originPlayUrl;
                break;
            case 28:
                playUrl = "http://account.lekan.com/queryVideoFiles?site=6&version=2.001&entranceID=9601&ck_platform=10&ck_width=" + width
                        + "&ck_height=" + height
                        +  "&ck_sysName=iPhone&ck_sysVer=7.1.1&ck_ua=" + ua
                        + "&ck_did=020000000000&ck_is_dev=0&ck_odid=610191de5f11d5ae15aed8d04ba4fdffe9d4e207&ck_idfvdid=7E753044-94A2-49D4-BFA5-DED83C857977&ck_idfadid=&userId=-1&videoId=" + originPlayUrl
                        + "&videoType=6&idx=" + episode + "&did=2&s=2";
                break;
            case 29:
                playUrl = "http://vv.video.qq.com/geturl?otype=json&vid=" + originPlayUrl + "&defaultfmt=sd";
                // TODO: 2015/11/6 需要再处理一次JSON
                DefaultNetServer defaultNetServer29 = new DefaultNetServer();
                defaultNetServer29.Url = playUrl;
                defaultNetServer29.getPlayUrl();
                playUrl = defaultNetServer29.result;
                playUrl = playUrl.trim();
                playUrl = playUrl.replaceAll("QZOutputJson=", "");
                playUrl = playUrl.replaceAll(";", "");
                playUrl = playUrl.trim();
                try {
                    JSONObject jsonObject29 = new JSONObject(playUrl);
                    jsonObject29 = jsonObject29.getJSONObject("vd");
                    jsonObject29 = (jsonObject29.getJSONArray("vi")).getJSONObject(0);
                            //.getJSONObject("vi");
                            //.getJSONObject("0");
                    playUrl = jsonObject29.optString("url");
                    //DefaultNetServer defaultNetServer29 = new DefaultNetServer();
                    //defaultNetServer29.Url = playUrl;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            case 30:
                playUrl = "http://account.lekan.com/queryVideoFiles?site=5&version=3.070&entranceID=9601&ck_platform=10&ck_width=" + width + "&ck_height="
                        + "&ck_sysName=iPhone&ck_sysVer=7.1.1&ck_ua="
                        + ua + "&ck_did=020000000000&ck_is_dev=0&groupId=0&ck_odid=610191de5f11d5ae15aed8d04ba4fdffe9d4e207&ck_idfvdid=7E753044-94A2-49D4-BFA5-DED83C857977&userId=680241b479682cdd168e7481677c9e4a39ef893e15043a65&videoId="
                        + originPlayUrl + "&videoType=1&idx="
                        + episode + "&did=5&s=2";
                break;
            case 31:
                //// TODO: 2015/11/5 处理一个json 已处理
                DefaultNetServer defaultNetServer = new DefaultNetServer();
                defaultNetServer.Url = originPlayUrl;
                defaultNetServer.getPlayUrl();
                playUrl = originPlayUrl  + "?key=" + defaultNetServer.getValue();
                break;
            case 32:
                //// TODO: 2015/11/5 需要在播放前发送一个http头
                //playUrl = originPlayUrl;
                GetaiqiyiUrl getaiqiyiUrl = new GetaiqiyiUrl();
                GetaiqiyiUrl _getaiqiyiUrl = new GetaiqiyiUrl();
                _getaiqiyiUrl.isFirst = false;
                playUrl =  getaiqiyiUrl.playUrl(originPlayUrl);
                playUrl = playUrl.trim();
                try {
                    JSONObject jsonObject = new JSONObject(playUrl);
                    jsonObject = jsonObject.getJSONObject("tv").getJSONObject("0");
                    playUrl = jsonObject.getString("_durl");
                    playUrl = _getaiqiyiUrl.playUrl(playUrl);
                    playUrl = playUrl.trim();
                    playUrl = playUrl.substring(13, playUrl.length());
                    playUrl = playUrl.substring(0, playUrl.length() - 1);
                    playUrl = playUrl.replaceAll("code", "\"code\"");
                    playUrl = playUrl.replaceAll("data", "\"data\"");
                    jsonObject = new JSONObject(playUrl);
                    jsonObject = jsonObject.getJSONObject("data");
                    playUrl = jsonObject.optString("l");

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


                break;
            case 34:
                if (quality == "normal")
                {
                    video_quality = "video_1";
                }
                else if (quality == "high")
                {
                    video_quality = "video_2";
                }
                else if (quality == "super")
                {
                    video_quality = "video_3";
                }
                Base64Get base64Get = new Base64Get();
                DefaultNetServer defaultNetServer34 = new DefaultNetServer();
                defaultNetServer34.Url = originPlayUrl;
                // TODO: 2015/11/5 需要重新处理下json
                defaultNetServer34.getPlayUrl();
                playUrl =  base64Get.getUrl(defaultNetServer34.get34Value(video_quality));
                break;
            case 37:
                VideoPlayNetServer videoPlayNetServer = new VideoPlayNetServer();
                videoPlayNetServer.Url = "http://115.29.190.54:12345/pptv.aspx?playStr=" + originPlayUrl + "&quality=" + quality + "&type=0";
                videoPlayNetServer.key = "dd358748fcabdda1";
                videoPlayNetServer.getVideoPlayUrl();
                playUrl = videoPlayNetServer.unlockJson;
                break;
            default:
                playUrl = "http://flv.wandoujia.com/hack/flv/download?url=" + originPlayUrl + "&format=" + quality + "&f=iphone&v=2.2.0";
                /*DefaultNetServer _defaultNetServer_ = new DefaultNetServer();
                _defaultNetServer_.Url = playUrl;
                _defaultNetServer_.getPlayUrl();
                playUrl = _defaultNetServer_.result;*/
                break;
        }
        return playUrl;
    }


}
