package com.example.biezhi.videonew.DataModel;

import java.util.List;

/**
 * Created by xiaofeng on 16/3/8.
 */
public class EpisodeModel {

    /**
     * content : [{"id":4316572,"episode":1,"name":"成都市场麦麦故事王","playUrl":"134970","VIP":false,"sourceId":0},{"id":4316573,"episode":2,"name":"东北市场麦麦故事王","playUrl":"134970","VIP":false,"sourceId":0},{"id":4316574,"episode":3,"name":"东部市场麦麦故事王","playUrl":"134970","VIP":false,"sourceId":0},{"id":4316575,"episode":4,"name":"广州市场麦麦故事王","playUrl":"134970","VIP":false,"sourceId":0},{"id":4316576,"episode":5,"name":"海南市场麦麦故事王","playUrl":"134970","VIP":false,"sourceId":0},{"id":4316577,"episode":6,"name":"上海市场麦麦故事王","playUrl":"134970","VIP":false,"sourceId":0},{"id":4316578,"episode":7,"name":"黑龙江市场麦麦故事王","playUrl":"134970","VIP":false,"sourceId":0},{"id":4316579,"episode":8,"name":"湖南市场麦麦故事王","playUrl":"134970","VIP":false,"sourceId":0},{"id":4316580,"episode":9,"name":"江西市场麦麦故事王","playUrl":"134970","VIP":false,"sourceId":0}]
     * videoId : 302940
     * siteId : 28
     * siteName : 其他
     * urlJs :
     * canDownload : false
     */

    private int videoId;
    private int siteId;
    private String siteName;
    private String urlJs;
    private boolean canDownload;
    /**
     * id : 4316572
     * episode : 1
     * name : 成都市场麦麦故事王
     * playUrl : 134970
     * VIP : false
     * sourceId : 0
     */

    private List<ContentEntity> content;

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setUrlJs(String urlJs) {
        this.urlJs = urlJs;
    }

    public void setCanDownload(boolean canDownload) {
        this.canDownload = canDownload;
    }

    public void setContent(List<ContentEntity> content) {
        this.content = content;
    }

    public int getVideoId() {
        return videoId;
    }

    public int getSiteId() {
        return siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getUrlJs() {
        return urlJs;
    }

    public boolean isCanDownload() {
        return canDownload;
    }

    public List<ContentEntity> getContent() {
        return content;
    }

    public static class ContentEntity {
        private int id;
        private int episode;
        private String name;
        private String playUrl;
        private boolean VIP;
        private int sourceId;

        public void setId(int id) {
            this.id = id;
        }

        public void setEpisode(int episode) {
            this.episode = episode;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPlayUrl(String playUrl) {
            this.playUrl = playUrl;
        }

        public void setVIP(boolean VIP) {
            this.VIP = VIP;
        }

        public void setSourceId(int sourceId) {
            this.sourceId = sourceId;
        }

        public int getId() {
            return id;
        }

        public int getEpisode() {
            return episode;
        }

        public String getName() {
            return name;
        }

        public String getPlayUrl() {
            return playUrl;
        }

        public boolean isVIP() {
            return VIP;
        }

        public int getSourceId() {
            return sourceId;
        }
    }
}
