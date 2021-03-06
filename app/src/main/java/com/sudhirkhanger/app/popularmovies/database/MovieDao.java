package com.sudhirkhanger.app.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> loadAllMovies();

    @Insert
    void insertMovie(Movie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movie movie);

    @Delete()
    void deleteMovie(Movie movie);

    @Query("delete from movie where movie_id = :movieId")
    void deleteByMovieId(String movieId);

    @Query("SELECT * FROM movie WHERE movie_id = :movieId")
    LiveData<Movie> getMovieByMovieId(String movieId);
}
