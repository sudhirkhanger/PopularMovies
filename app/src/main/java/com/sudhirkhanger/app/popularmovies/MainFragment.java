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

package com.sudhirkhanger.app.popularmovies;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sudhirkhanger.app.popularmovies.Adapters.MovieAdapter;
import com.sudhirkhanger.app.popularmovies.FetchTasks.FetchMoviesTask;
import com.sudhirkhanger.app.popularmovies.Model.Movie;
import com.sudhirkhanger.app.popularmovies.Model.MovieContract;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainFragment extends Fragment {

    private ArrayList<Movie> mMovieArrayList = new ArrayList<Movie>();
    private static final String PAGE = "1";
    private static final int GRID = 2;
    private RecyclerView mRecyclerView;

    private SharedPreferences mSettings;
    private SharedPreferences.Editor mEditor;

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private static final String URL_POPULARITY = "popular";
    private static final String URL_RATING = "top_rated";
    private static final String URL_FAVORITE = "favorite";
    private static final String PREF = "sort";

    public interface Callback {
        void onItemSeleted(Movie movie);
    }


    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mSettings.edit();
        mEditor.apply();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), GRID));

        updateMovieList();

        mRecyclerView.setAdapter(new MovieAdapter(getActivity(),
                mMovieArrayList,
                new MovieAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Movie movie) {
                        Log.d(LOG_TAG, "onItemClick " + movie.toString());
                        initiateCallback(movie);
                    }
                }));

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume() reached");
        String str = mSettings.getString(PREF, URL_POPULARITY);
        if (str.equals(URL_FAVORITE)) {
            Log.d(LOG_TAG, "onResume() getDataFromDB");
            getDataFromDB();
            mRecyclerView.setAdapter(new MovieAdapter(getActivity(),
                    mMovieArrayList,
                    new MovieAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Movie movie) {
                            Log.d(LOG_TAG, "onItemClick " + movie.toString());
                            initiateCallback(movie);
                        }
                    }));
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popularity:
                mEditor.putString(PREF, URL_POPULARITY);
                mEditor.apply();
                updateMovieList();
                item.setChecked(true);
                Log.d(LOG_TAG, "onOptionsItemSelected: popularity");
                return true;
            case R.id.rating:
                mEditor.putString(PREF, URL_RATING);
                mEditor.apply();
                updateMovieList();
                item.setChecked(true);
                Log.d(LOG_TAG, "onOptionsItemSelected: rating");
                return true;
            case R.id.favorite:
                mEditor.putString(PREF, URL_FAVORITE);
                mEditor.apply();
                updateMovieList();
                item.setChecked(true);
                Log.d(LOG_TAG, "onOptionsItemSelected: favorite");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        String sortBy = mSettings.getString(PREF, URL_POPULARITY);

        switch (sortBy) {
            case URL_POPULARITY:
                menu.findItem(R.id.popularity).setChecked(true);
                break;
            case URL_RATING:
                menu.findItem(R.id.rating).setChecked(true);
                break;
            case URL_FAVORITE:
                menu.findItem(R.id.favorite).setChecked(true);
                break;
        }
    }

    private void updateMovieList() {
        mMovieArrayList = new ArrayList<>();
        String sortBy = mSettings.getString(PREF, URL_POPULARITY);

        if (sortBy.equals(URL_POPULARITY) ||
                sortBy.equals(URL_RATING)) {

            try {
                mMovieArrayList =
                        new FetchMoviesTask().execute(sortBy, PAGE).get();
            } catch (ExecutionException | InterruptedException ei) {
                ei.printStackTrace();
            }
        } else if (sortBy.equals(URL_FAVORITE)) {
            getDataFromDB();
        }
        mRecyclerView.setAdapter(new MovieAdapter(getActivity(),
                mMovieArrayList,
                new MovieAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Movie movie) {
                        Log.d(LOG_TAG, "onItemClick " + movie.toString());
                        initiateCallback(movie);
                    }
                }));
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    //TODO: replace null with column constants
    private void getDataFromDB() {
        mMovieArrayList = new ArrayList<>();
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor =
                resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.TITLE));
                    String movie_id = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_ID));
                    String poster = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.POSTER));
                    String backdrop = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.BACKDROP));
                    String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.OVERVIEW));
                    String vote_average = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.VOTE_AVERAGE));
                    String release_date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.DATE));

                    Movie movie = new Movie(title, release_date, poster,
                            vote_average, overview, backdrop, movie_id);
                    mMovieArrayList.add(movie);
                } while (cursor.moveToNext());
            }
        }

        if (cursor != null)
            cursor.close();
    }

    public void initiateCallback(Movie movie) {
        ((Callback) getActivity()).onItemSeleted(movie);
    }
}
