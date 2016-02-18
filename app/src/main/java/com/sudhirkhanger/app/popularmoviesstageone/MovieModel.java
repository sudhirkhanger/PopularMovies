package com.sudhirkhanger.app.popularmoviesstageone;

public class MovieModel {
    private String mposterUrl;

    public MovieModel(String posterUrl) {
        mposterUrl = posterUrl;
    }

    public String getMposterUrl() {
        return mposterUrl;
    }

    public void setMposterUrl(String mposterUrl) {
        this.mposterUrl = mposterUrl;
    }
}
