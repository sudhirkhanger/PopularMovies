package com.sudhirkhanger.app.popularmoviesstageone.Model;

public class MovieModel {
    private String mposterUrl;
    private String mTitle;

    public MovieModel(String posterUrl) {
        mposterUrl = posterUrl;
    }

    public String getMposterUrl() {
        return mposterUrl;
    }

    public void setMposterUrl(String mposterUrl) {
        this.mposterUrl = mposterUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
