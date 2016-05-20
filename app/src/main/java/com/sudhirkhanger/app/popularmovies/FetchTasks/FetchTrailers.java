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
import com.sudhirkhanger.app.popularmovies.Model.Trailer;

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

public class FetchTrailers extends AsyncTask<String, Void, ArrayList<Trailer>> {

    private static final String LOG_TAG = FetchTrailers.class.getSimpleName();

    private ArrayList<Trailer> getTrailerFromJson(String trailerJsonStr)
            throws JSONException {

        final String MDB_RESULTS = "results";
        final String MDB_KEY = "key";
        final String YT_IMAGE_URL_PREFIX = "http://img.youtube.com/vi/";
        final String YT_IMAGE_URL_SUFFIX = "/0.jpg";
        final String YT_LINK_URL = "https://www.youtube.com/watch?v=";

        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray(MDB_RESULTS);

        ArrayList<Trailer> trailerArrayList = new ArrayList<>();

        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject trailerObject = trailerArray.getJSONObject(i);

            String key = trailerObject.getString(MDB_KEY);

            String image = YT_IMAGE_URL_PREFIX + key + YT_IMAGE_URL_SUFFIX;
            String link = YT_LINK_URL + key;

            trailerArrayList.add(new Trailer(image, link));
        }

        return trailerArrayList;
    }

    @Override
    protected ArrayList<Trailer> doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String trailerJsonStr = null;

        try {
            // Construct the URL for the MovieDB query

            final String MDB_BASE_URL =
                    "http://api.themoviedb.org/3/movie/";
            final String PATH_VIDEO = "videos";
            final String QUERY_API_KEY = "api_key";

            // http://api.themoviedb.org/3/movie/209112/videos?api_key={API_KEY}

            Uri builtUri = Uri.parse(MDB_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendPath(PATH_VIDEO)
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
                trailerJsonStr = null;
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
                trailerJsonStr = null;
            }
            trailerJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            trailerJsonStr = null;
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
            return getTrailerFromJson(trailerJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
