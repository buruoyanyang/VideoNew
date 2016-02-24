package com.example.biezhi.videonew.DataModel;

import java.util.List;

/**
 * Created by xiaofeng on 16/2/23.
 */
public class CateModel {
    /**
     * id : 35
     * cateId : 13
     * cover : http://video.chaoxing.com/images/ztnew/xltm0987-1.jpg
     * detailUrl :
     * districtId : 0
     * key : lesson
     * name : 趣味课堂
     * kindId : 0
     */

    private List<ContentEntity> content;

    public void setContent(List<ContentEntity> content) {
        this.content = content;
    }

    public List<ContentEntity> getContent() {
        return content;
    }

    public static class ContentEntity {
        private int id;
        private int cateId;
        private String cover;
        private String detailUrl;
        private int districtId;
        private String key;
        private String name;
        private int kindId;

        public void setId(int id) {
            this.id = id;
        }

        public void setCateId(int cateId) {
            this.cateId = cateId;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public void setDetailUrl(String detailUrl) {
            this.detailUrl = detailUrl;
        }

        public void setDistrictId(int districtId) {
            this.districtId = districtId;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setKindId(int kindId) {
            this.kindId = kindId;
        }

        public int getId() {
            return id;
        }

        public int getCateId() {
            return cateId;
        }

        public String getCover() {
            return cover;
        }

        public String getDetailUrl() {
            return detailUrl;
        }

        public int getDistrictId() {
            return districtId;
        }

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public int getKindId() {
            return kindId;
        }
    }
}
