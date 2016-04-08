package com.example.biezhi.videonew.DataModel;

import java.util.List;

/**
 * Created by xiaofeng on 16/4/8.
 */
public class FavoriteModel {


    /**
     * videoId : 237
     * videoName : 名字
     * cover : 全部
     */

    private List<VideosEntity> videos;

    public List<VideosEntity> getVideos() {
        return videos;
    }

    public void setVideos(List<VideosEntity> videos) {
        this.videos = videos;
    }

    public static class VideosEntity {
        private String videoId;
        private String videoName;
        private String cover;

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getVideoName() {
            return videoName;
        }

        public void setVideoName(String videoName) {
            this.videoName = videoName;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }
    }
}
