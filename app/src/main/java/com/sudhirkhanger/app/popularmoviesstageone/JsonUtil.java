package com.sudhirkhanger.app.popularmoviesstageone;

import com.sudhirkhanger.app.popularmoviesstageone.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtil {

    private static final String TAG_RESULT = "results";
    private static final String TAG_TITLE = "title";
    private static final String TAG_RELEASE_DATE = "release_date";
    private static final String TAG_POSTER_PATH = "poster_path";
    private static final String TAG_VOTE_AVERAGE = "vote_average";
    private static final String TAG_OVERVIEW = "overview";
    private static final String IMAGE_URL = "https://image.tmdb.org/t/p/w185/";

    protected ArrayList<Movie> JsonToArrayList(String jsonStr, int index) {

        ArrayList<Movie> movieArr = new ArrayList<>();

        try {
            JSONObject movieJsonObj = new JSONObject(jsonStr);
            JSONArray movieJsonArr = movieJsonObj.getJSONArray(TAG_RESULT);

            for (int i = 0; i < index; i++) {
                JSONObject titleObj = movieJsonArr.getJSONObject(i);

                String title = titleObj.getString(TAG_TITLE);
                String release_date = titleObj.getString(TAG_RELEASE_DATE);
                String poster_path = titleObj.getString(TAG_POSTER_PATH);
                String vote_average = titleObj.getString(TAG_VOTE_AVERAGE);
                String overview = titleObj.getString(TAG_OVERVIEW);

                Movie movie = new Movie(title,
                        release_date,
                        IMAGE_URL + poster_path,
                        vote_average,
                        overview);

                movieArr.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieArr;
    }
}
