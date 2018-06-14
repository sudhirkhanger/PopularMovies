package com.sudhirkhanger.app.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.sudhirkhanger.app.popularmovies.database.Movie;
import com.sudhirkhanger.app.popularmovies.database.PopularMoviesDatabase;


public class DetailViewModel extends ViewModel {

    private LiveData<Movie> movieLiveData;

    public DetailViewModel(PopularMoviesDatabase popularMoviesDatabase, String movieId) {
        movieLiveData = popularMoviesDatabase.movieDao().getMovieByMovieId(movieId);
    }

    public LiveData<Movie> getMovieLiveData() {
        return movieLiveData;
    }
}
