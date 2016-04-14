package com.example.biezhi.videonew.MessageBox;

/**
 * Created by xiaofeng on 16/4/14.
 */
public class VideoEpisodeMessage {
    private String videoId;
    private String episodeId;
    private int episode;

    public VideoEpisodeMessage(String videoId, String episodeId,int episode) {
        this.videoId = videoId;
        this.episodeId = episodeId;
        this.episode = episode;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    public void setEpisode(int episode)
    {
        this.episode = episode;
    }

    public String getVideoId() {
        return this.videoId;
    }

    public String getEpisodeId() {
        return this.episodeId;
    }

    public int getEpisode()
    {
        return this.episode;
    }
}
