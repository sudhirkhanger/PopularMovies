package com.sudhirkhanger.app.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class PopularMoviesDatabase extends RoomDatabase {

    private static final String TAG = "PopularMoviesDatabase";
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "moviesdatabase";
    private static PopularMoviesDatabase popularMoviesDatabase;

    public static PopularMoviesDatabase getInstance(Context context) {
        if (popularMoviesDatabase == null) {
            synchronized (LOCK) {
                Log.e(TAG, "getInstance: creating database");
                popularMoviesDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        PopularMoviesDatabase.class, PopularMoviesDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.e(TAG, "getInstance: getting database");
        return popularMoviesDatabase;
    }

    public abstract MovieDao movieDao();
}
