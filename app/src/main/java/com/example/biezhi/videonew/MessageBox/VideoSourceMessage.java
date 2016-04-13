package com.example.biezhi.videonew.MessageBox;

import com.example.biezhi.videonew.DataModel.SourceModel;

/**
 * Created by xiaofeng on 16/4/13.
 */
public class VideoSourceMessage {
    private SourceModel sourceModel;

    public VideoSourceMessage(SourceModel sourceModel) {
        this.sourceModel = sourceModel;
    }

    public void setSourceModel(SourceModel sourceModel) {
        this.sourceModel = sourceModel;
    }

    public SourceModel getSourceModel() {
        return this.sourceModel;
    }
}
