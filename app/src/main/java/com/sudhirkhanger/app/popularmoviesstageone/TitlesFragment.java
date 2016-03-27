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
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * This is the primary fragment where
 * json is download and processed in
 * a background task
 */

public class TitlesFragment extends Fragment {

    private static final int COLUMN = 2;

    private RecyclerView mRecyclerView;

    SharedPreferences mSettings;
    private SharedPreferences.Editor mEditor;

    private static final String SHARED_KEY_SORT = "sort";
    private static final String POPULARITY = "popularity.desc";
    private static final String RATING = "vote_average.desc";

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

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), COLUMN));

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
                mEditor.putString(SHARED_KEY_SORT, POPULARITY);
                mEditor.apply();
                updateScreen();
                item.setChecked(true);
                return true;
            case R.id.rating:
                mEditor.putString(SHARED_KEY_SORT, RATING);
                mEditor.apply();
                updateScreen();
                item.setChecked(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Background task class where JSON is downloaded
     * and returned as an arraylist of movie objects
     */
    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * JSON parsing class
         *
         * @param movieJsonStr Raw json data
         * @return Arraylist of movie objects
         * @throws JSONException
         */
        private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            final String MDB_RESULT = "results";
            final String MDB_TITLE = "title";
            final String MDB_RELEASE_DATE = "release_date";
            final String MDB_POSTER_PATH = "poster_path";
            final String MDB_VOTE_AVERAGE = "vote_average";
            final String MDB_OVERVIEW = "overview";
            final String MDB_BACKDROPS = "backdrop_path";
            final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185/";
            final String IMAGE_BACKDROPS_URL = "https://image.tmdb.org/t/p/w300/";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_RESULT);

            ArrayList<Movie> movieArrayList = new ArrayList<>();

            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject movieObject = movieArray.getJSONObject(i);

                String title = movieObject.getString(MDB_TITLE);
                String release_date = movieObject.getString(MDB_RELEASE_DATE);
                String poster_path = movieObject.getString(MDB_POSTER_PATH);
                String vote_average = movieObject.getString(MDB_VOTE_AVERAGE);
                String overview = movieObject.getString(MDB_OVERVIEW);
                String backdrops = movieObject.getString(MDB_BACKDROPS);

                Movie movie = new Movie(title,
                        release_date,
                        IMAGE_BASE_URL + poster_path,
                        vote_average,
                        overview,
                        IMAGE_BACKDROPS_URL + backdrops);

                movieArrayList.add(movie);
            }

            return movieArrayList;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {

                final String MOVIEDB_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_SORT_BY = "sort_by";
                final String QUERY_APPKEY = "api_key";
                final String QUERY_VOTE_COUNT = "vote_count.gte";
                final String PARAM_MIN_VOTES = "50";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_SORT_BY, params[0])
                        .appendQueryParameter(QUERY_VOTE_COUNT, PARAM_MIN_VOTES)
                        .appendQueryParameter(QUERY_APPKEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();


                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
        }
    }

    /**
     * convenience class to update the data
     */
    private void updateScreen() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        String sortBy = mSettings.getString(SHARED_KEY_SORT, POPULARITY);
        try {
            mRecyclerView.setAdapter(new MovieAdapter(getActivity(),
                    fetchMoviesTask.execute(sortBy).get()));
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
        String sortBy = mSettings.getString(SHARED_KEY_SORT, POPULARITY);
        if (sortBy.equals(POPULARITY)) {
            menu.findItem(R.id.popularity).setChecked(true);
        } else {
            menu.findItem(R.id.rating).setChecked(true);
        }
    }
}