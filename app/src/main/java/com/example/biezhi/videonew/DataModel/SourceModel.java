package com.example.biezhi.videonew.DataModel;

import java.util.List;

/**
 * Created by xiaofeng on 16/3/7.
 */
public class SourceModel {

    /**
     * count : 1
     * data : [{"videoId":302940,"logo":"图标1"}]
     */

    private int count;
    /**
     * videoId : 302940
     * logo : 图标1
     */

    private List<DataEntity> data;

    public void setCount(int count) {
        this.count = count;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public List<DataEntity> getData() {
        return data;
    }

    public static class DataEntity {
        private int videoId;
        private String logo;

        public void setVideoId(int videoId) {
            this.videoId = videoId;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public int getVideoId() {
            return videoId;
        }

        public String getLogo() {
            return logo;
        }
    }
}
