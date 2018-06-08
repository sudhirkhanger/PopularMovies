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

package com.sudhirkhanger.app.popularmovies.FetchTasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sudhirkhanger.app.popularmovies.BuildConfig;
import com.sudhirkhanger.app.popularmovies.database.Movie;

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
        final String MDB_ID = "id";
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
            String id = movieObject.getString(MDB_ID);

            Movie movie = new Movie(title,
                    release_date,
                    IMAGE_BASE_URL + poster_path,
                    vote_average,
                    overview,
                    IMAGE_BACKDROPS_URL + backdrops,
                    id);

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

            // http://api.themoviedb.org/3/movie/popular?page=1&api_key={API Key}
            // Build an URL like above

            final String MOVIEDB_BASE_URL =
                    "http://api.themoviedb.org/3/movie";
            final String QUERY_PAGE = "page";
            final String QUERY_API_KEY = "api_key";

            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(QUERY_PAGE, params[1])
                    .appendQueryParameter(QUERY_API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG, url.toString());

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