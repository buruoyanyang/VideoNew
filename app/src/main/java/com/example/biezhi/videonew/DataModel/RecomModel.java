package com.example.biezhi.videonew.DataModel;

import java.util.List;

/**
 * Created by xiaofeng on 16/4/25.
 */
public class RecomModel {

    /**
     * id : 312357
     * cateId : 15
     * brief :
     * cover : http://img.rrmj.tv/video/20151124/o_1448358638123.jpg
     * name : 大卫·艾登堡的微型猛兽世界之旅
     * rating : 0
     * updateInfo :
     * hotId : 354
     */

    private List<RecommendsEntity> recommends;

    public List<RecommendsEntity> getRecommends() {
        return recommends;
    }

    public void setRecommends(List<RecommendsEntity> recommends) {
        this.recommends = recommends;
    }

    public static class RecommendsEntity {
        private int id;
        private int cateId;
        private String brief;
        private String cover;
        private String name;
        private int rating;
        private String updateInfo;
        private int hotId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCateId() {
            return cateId;
        }

        public void setCateId(int cateId) {
            this.cateId = cateId;
        }

        public String getBrief() {
            return brief;
        }

        public void setBrief(String brief) {
            this.brief = brief;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getUpdateInfo() {
            return updateInfo;
        }

        public void setUpdateInfo(String updateInfo) {
            this.updateInfo = updateInfo;
        }

        public int getHotId() {
            return hotId;
        }

        public void setHotId(int hotId) {
            this.hotId = hotId;
        }
    }
}
