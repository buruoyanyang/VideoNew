package com.example.biezhi.videonew.MessageBox;

import com.example.biezhi.videonew.DataModel.EpisodeModel;

/**
 * Created by xiaofeng on 16/4/13.
 */
public class EpisodeMessage {
    private EpisodeModel episodeModel;
    public EpisodeMessage(EpisodeModel episodeModel)
    {
        this.episodeModel = episodeModel;
    }
    public void setEpisodeModel(EpisodeModel episodeModel)
    {
        this.episodeModel = episodeModel;
    }
    public EpisodeModel getEpisodeModel()
    {
        return this.episodeModel;
    }
}
