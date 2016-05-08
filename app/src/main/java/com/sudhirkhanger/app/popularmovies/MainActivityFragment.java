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

import com.sudhirkhanger.app.popularmovies.Model.Movie;
import com.sudhirkhanger.app.popularmovies.Model.MovieContract;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivityFragment extends Fragment {

    private ArrayList<Movie> mMovieArrayList = new ArrayList<Movie>();
    private static final String PAGE = "1";
    private RecyclerView mRecyclerView;

    private SharedPreferences mSettings;
    private SharedPreferences.Editor mEditor;

    private static final String LOG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        // Set column size to 2 for default and portrait
        // and 3 for landscape orientations
        int column = Integer.parseInt(getString(R.string.grid_portrait));
        if (getResources().getConfiguration().orientation == 1) {
            column = Integer.parseInt(getString(R.string.grid_portrait));
        } else if (getResources().getConfiguration().orientation == 2) {
            column = Integer.parseInt(getString(R.string.grid_landscape));
        }

        if (getActivity().findViewById(R.id.movie_detail_container) != null) {
            column = Integer.parseInt("2");
        }

        mSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mSettings.edit();
        mEditor.apply();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), column));

        mRecyclerView.setAdapter(new MovieAdapter(getActivity(), mMovieArrayList));
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popularity:
                mEditor.putString(getResources().getString(R.string.perf_sort),
                        getResources().getString(R.string.url_popularity));
                mEditor.apply();
                updateMovieList();
                item.setChecked(true);
                Log.d(LOG, "onOptionsItemSelected: popularity");
                return true;
            case R.id.rating:
                mEditor.putString(getResources().getString(R.string.perf_sort),
                        getResources().getString(R.string.url_top_rated));
                mEditor.apply();
                updateMovieList();
                item.setChecked(true);
                Log.d(LOG, "onOptionsItemSelected: rating");
                return true;
            case R.id.favorite:
                mEditor.putString(getResources().getString(R.string.perf_sort),
                        getResources().getString(R.string.url_favorite));
                mEditor.apply();
                updateMovieList();
                item.setChecked(true);
                Log.d(LOG, "onOptionsItemSelected: favorite");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        String sortBy = mSettings.getString(getResources().getString(R.string.perf_sort),
                getResources().getString(R.string.url_popularity));

        if (sortBy.equals(getResources().getString(R.string.url_popularity))) {
            menu.findItem(R.id.popularity).setChecked(true);
        } else if (sortBy.equals(getResources().getString(R.string.url_top_rated))) {
            menu.findItem(R.id.rating).setChecked(true);
        } else if (sortBy.equals(getResources().getString(R.string.url_favorite))) {
            menu.findItem(R.id.favorite).setChecked(true);
        }
    }

    private void updateMovieList() {
        mMovieArrayList = new ArrayList<>();
        String sortBy = mSettings.getString(getResources().getString(R.string.perf_sort),
                getResources().getString(R.string.url_popularity));

        if (sortBy.equals(getResources().getString(R.string.url_popularity)) ||
                sortBy.equals(getResources().getString(R.string.url_top_rated))) {

            try {
                mMovieArrayList =
                        new FetchMoviesTask().execute(sortBy, PAGE).get();
            } catch (ExecutionException | InterruptedException ei) {
                ei.printStackTrace();
            }
        } else if (sortBy.equals(getResources().getString(R.string.url_favorite))) {
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

        mRecyclerView.setAdapter(new MovieAdapter(getActivity(), mMovieArrayList));
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }
}
