package com.sudhirkhanger.app.popularmoviesstageone;

import com.sudhirkhanger.app.popularmoviesstageone.Model.MovieModel;

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
    private static final String posterURL = "https://image.tmdb.org/t/p/w185/";

    public ArrayList<MovieModel> JsonToArrayList(String jsonStr, int index) {
        String title;
        String release_date;
        String poster_path;
        String vote_average;
        String overview;
        ArrayList<MovieModel> arrayList = new ArrayList<MovieModel>();

        try {
            JSONObject movieObj = new JSONObject(jsonStr);
            JSONArray movieArr = movieObj.getJSONArray(TAG_RESULT);

            for (int i = 0; i < index; i++) {
                JSONObject titleObj = movieArr.getJSONObject(i);

                title = titleObj.getString(TAG_TITLE);
                release_date = titleObj.getString(TAG_RELEASE_DATE);
                poster_path = titleObj.getString(TAG_POSTER_PATH);
                vote_average = titleObj.getString(TAG_VOTE_AVERAGE);
                overview = titleObj.getString(TAG_OVERVIEW);

                MovieModel movieModel = new MovieModel(posterURL + poster_path);
                arrayList.add(movieModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayList;
    }
}
