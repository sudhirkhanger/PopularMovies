package com.sudhirkhanger.app.popularmovies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.sudhirkhanger.app.popularmovies.database.PopularMoviesDatabase;


public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final PopularMoviesDatabase popularMoviesDatabase;
    private final String movieId;

    public DetailViewModelFactory(PopularMoviesDatabase popularMoviesDatabase, String movieId) {
        this.popularMoviesDatabase = popularMoviesDatabase;
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailViewModel(popularMoviesDatabase, movieId);
    }
}
