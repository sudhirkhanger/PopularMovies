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
import com.sudhirkhanger.app.popularmovies.Model.Review;

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

public class FetchReviews extends AsyncTask<String, Void, ArrayList<Review>> {

    private static final String LOG_TAG = FetchReviews.class.getSimpleName();

    private ArrayList<Review> getReviewsFromJson(String trailerJsonStr)
            throws JSONException {

        final String MDB_RESULTS = "results";
        final String MDB_AUTHOR = "author";
        final String MDB_CONTENT = "content";

        JSONObject reviewsJson = new JSONObject(trailerJsonStr);
        JSONArray reviewsArray = reviewsJson.getJSONArray(MDB_RESULTS);

        ArrayList<Review> reviewsArrayList = new ArrayList<>();

        for (int i = 0; i < reviewsArray.length(); i++) {
            JSONObject reviewsObject = reviewsArray.getJSONObject(i);

            String author = reviewsObject.getString(MDB_AUTHOR);
            String content = reviewsObject.getString(MDB_CONTENT);
            String contentEscapeChar = JSONObject.quote(content);

            reviewsArrayList.add(new Review(author, contentEscapeChar));
        }
        return reviewsArrayList;
    }

    @Override
    protected ArrayList<Review> doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String reviewJsonStr = null;

        try {
            // Construct the URL for the MovieDB query

            final String MDB_BASE_URL =
                    "http://api.themoviedb.org/3/movie/";
            final String PATH_REVIEWS = "reviews";
            final String QUERY_API_KEY = "api_key";

            // http://api.themoviedb.org/3/movie/{movie_id}/reviews?api_key={API_KEY}

            Uri builtUri = Uri.parse(MDB_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendPath(PATH_REVIEWS)
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
                reviewJsonStr = null;
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
                reviewJsonStr = null;
            }
            reviewJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            reviewJsonStr = null;
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
            return getReviewsFromJson(reviewJsonStr);
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
