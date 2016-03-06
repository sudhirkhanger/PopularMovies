package com.sudhirkhanger.app.popularmoviesstageone.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    String mTitle;
    String mReleaseDate;
    String mPosterPath;
    String mVoteAverage;
    String mOverView;
    String mBackdrops;

    public Movie(String title,
                 String releaseDate,
                 String posterPath,
                 String voteAverage,
                 String overView,
                 String backdrops) {
        mTitle = title;
        mReleaseDate = releaseDate;
        mPosterPath = posterPath;
        mVoteAverage = voteAverage;
        mOverView = overView;
        mBackdrops = backdrops;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        mVoteAverage = voteAverage;
    }

    public String getOverView() {
        return mOverView;
    }

    public void setOverView(String overView) {
        mOverView = overView;
    }

    public String getBackdrops() {
        return mBackdrops;
    }

    public void setBackdrops(String backdrops) {
        mBackdrops = backdrops;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mTitle='" + mTitle + '\'' +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                ", mPosterPath='" + mPosterPath + '\'' +
                ", mVoteAverage='" + mVoteAverage + '\'' +
                ", mOverView='" + mOverView + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTitle);
        dest.writeString(this.mReleaseDate);
        dest.writeString(this.mPosterPath);
        dest.writeString(this.mVoteAverage);
        dest.writeString(this.mOverView);
        dest.writeString(this.mBackdrops);
    }

    protected Movie(Parcel in) {
        this.mTitle = in.readString();
        this.mReleaseDate = in.readString();
        this.mPosterPath = in.readString();
        this.mVoteAverage = in.readString();
        this.mOverView = in.readString();
        this.mBackdrops = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
