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

package com.sudhirkhanger.app.popularmoviesstageone;

import android.content.SharedPreferences;
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

import com.sudhirkhanger.app.popularmoviesstageone.Model.Movie;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * This is the primary fragment where
 * json is download and processed in
 * a background task
 */

public class TitlesFragment extends Fragment {

    private RecyclerView mRecyclerView;

    SharedPreferences mSettings;
    private SharedPreferences.Editor mEditor;

    private static final String SHARED_KEY_SORT = "sort";
    private static final String PAGE = "1";

    public TitlesFragment() {
    }

    /**
     * This is the main class of the of the fragment
     * we set RecyclerView here.
     * Initialize settings to store URL for sorting.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_titles, container, false);

        // Set column size to 2 for default and portrait
        // and 3 for landscape orientations
        int column = Integer.parseInt(getString(R.string.grid_portrait));
        if (getResources().getConfiguration().orientation == 1) {
            column = Integer.parseInt(getString(R.string.grid_portrait));
        } else if (getResources().getConfiguration().orientation == 2) {
            column = Integer.parseInt(getString(R.string.grid_landscape));
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), column));

        mSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mSettings.edit();
        mEditor.apply();

        mRecyclerView.setAdapter(new MovieAdapter(getActivity(), new ArrayList<Movie>()));

        return rootView;
    }

    /**
     * Adapter will first populated here
     */
    @Override
    public void onStart() {
        super.onStart();
        updateScreen();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_titles_fragment, menu);
    }

    /**
     * User makes choice about sorting
     * There are two possibilities
     * First on basis of popularity
     * Second on basis of average rating
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popularity:
                mEditor.putString(SHARED_KEY_SORT, getString(R.string.url_popularity));
                mEditor.apply();
                updateScreen();
                item.setChecked(true);
                return true;
            case R.id.rating:
                mEditor.putString(SHARED_KEY_SORT, getString(R.string.url_top_rated));
                mEditor.apply();
                updateScreen();
                item.setChecked(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * convenience class to update the data
     */
    private void updateScreen() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        String sortBy = mSettings.getString(SHARED_KEY_SORT, getString(R.string.url_popularity));
        try {
            mRecyclerView.setAdapter(new MovieAdapter(getActivity(),
                    fetchMoviesTask.execute(sortBy, PAGE).get()));
            Log.d("updateScreen()", "fetchMovieTask performed");
        } catch (ExecutionException | InterruptedException ei) {
            ei.printStackTrace();
        }
    }

    /**
     * When a user makes a choice between
     * sorting choices we also want to update
     * the selection in menu ui.
     *
     * @param menu
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        String sortBy = mSettings.getString(SHARED_KEY_SORT, getString(R.string.url_popularity));
        if (sortBy.equals(getString(R.string.url_popularity))) {
            menu.findItem(R.id.popularity).setChecked(true);
        } else {
            menu.findItem(R.id.rating).setChecked(true);
        }
    }
}