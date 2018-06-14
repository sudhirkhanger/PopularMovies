/*
 * Copyright 2016 Sudhir Khanger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sudhirkhanger.app.popularmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * This class holds movie object which
 * contains various info about a movie.
 */
@Entity(tableName = "movie")
public class Movie implements Parcelable {

    @ColumnInfo(name = "title")
    private String mTitle;

    @ColumnInfo(name = "release_date")
    private String mReleaseDate;

    @ColumnInfo(name = "poster_path")
    private String mPosterPath;

    @ColumnInfo(name = "vote_average")
    private String mVoteAverage;

    @ColumnInfo(name = "overview")
    private String mOverView;

    @ColumnInfo(name = "backdrops")
    private String mBackdrops;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    private String movieId;

    public Movie(String title,
                 String releaseDate,
                 String posterPath,
                 String voteAverage,
                 String overView,
                 String backdrops,
                 @NonNull String movieId) {
        mTitle = title;
        mReleaseDate = releaseDate;
        mPosterPath = posterPath;
        mVoteAverage = voteAverage;
        mOverView = overView;
        mBackdrops = backdrops;
        this.movieId = movieId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
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

    public static Creator<Movie> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mTitle='" + mTitle + '\'' +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                ", mPosterPath='" + mPosterPath + '\'' +
                ", mVoteAverage='" + mVoteAverage + '\'' +
                ", mOverView='" + mOverView + '\'' +
                ", mBackdrops='" + mBackdrops + '\'' +
                ", movieId='" + movieId + '\'' +
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
        dest.writeString(this.movieId);
    }

    protected Movie(Parcel in) {
        this.mTitle = in.readString();
        this.mReleaseDate = in.readString();
        this.mPosterPath = in.readString();
        this.mVoteAverage = in.readString();
        this.mOverView = in.readString();
        this.mBackdrops = in.readString();
        this.movieId = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
