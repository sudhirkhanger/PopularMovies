package com.sudhirkhanger.app.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sudhirkhanger.app.popularmovies.database.Movie;
import com.sudhirkhanger.app.popularmovies.database.PopularMoviesDatabase;

import java.util.List;


public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";
    private LiveData<List<Movie>> movieList;

    public MainViewModel(@NonNull Application application) {
        super(application);
        PopularMoviesDatabase popularMoviesDatabase =
                PopularMoviesDatabase.getInstance(this.getApplication());
        Log.e(TAG, "MainViewModel: actively retrieving movies");
        movieList = popularMoviesDatabase.movieDao().loadAllMovies();
    }

    public LiveData<List<Movie>> getMovieList() {
        return movieList;
    }
}
