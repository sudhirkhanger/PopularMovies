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
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sudhirkhanger.app.popularmovies.Model.Movie;
import com.sudhirkhanger.app.popularmovies.Model.MovieContract;

import java.text.DateFormatSymbols;

/**
 * Fragment shows detailed information about the movies.
 */
public class DetailsFragment extends Fragment {

    private static final String PARCEL_KEY = "movie_parcel";
    private static final String LOG = DetailsFragment.class.getSimpleName();
    private ContentResolver resolver;

    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        Intent intent = getActivity().getIntent();
        Movie parcelableExtra = null;

        if (intent != null && intent.getData() == null) {
            parcelableExtra = intent.getParcelableExtra(PARCEL_KEY);
        }

        final String title = parcelableExtra.getTitle();
        final String movie_id = parcelableExtra.getId();
        final String poster = parcelableExtra.getPosterPath();
        final String backdrop = parcelableExtra.getBackdrops();
        final String overview = parcelableExtra.getOverView();
        final String vote_average = parcelableExtra.getVoteAverage();
        final String release_date = parcelableExtra.getReleaseDate();

        TextView detailTitle = (TextView) rootView.findViewById(R.id.details_title);
        TextView detailReleaseYear = (TextView) rootView.findViewById(R.id.details_release_year);
        TextView detailReleaseMonth = (TextView) rootView.findViewById(R.id.details_release_month);
        TextView detailVoteAverage = (TextView) rootView.findViewById(R.id.details_vote_average);
        TextView detailOverview = (TextView) rootView.findViewById(R.id.details_overview);
        ImageView detailBackdrops = (ImageView) rootView.findViewById(R.id.details_backdrop);
        ImageView detailThumbnail = (ImageView) rootView.findViewById(R.id.details_thumbnail);
        final Button favoriteButton = (Button) rootView.findViewById(R.id.favorite_button);

        detailTitle.setText(parcelableExtra.getTitle());
        detailReleaseYear.setText(getYear((parcelableExtra.getReleaseDate())));
        detailReleaseMonth.setText(getMonth(parcelableExtra.getReleaseDate()));
        detailVoteAverage.setText(parcelableExtra.getVoteAverage() + "/10");
        detailOverview.setText(parcelableExtra.getOverView());
        Picasso.with(rootView.getContext())
                .load(parcelableExtra.getBackdrops())
                .into(detailBackdrops);
        Picasso.with(rootView.getContext())
                .load(parcelableExtra.getPosterPath())
                .into(detailThumbnail);

        resolver = getActivity().getContentResolver();

        if (isRowExist(movie_id)) {
            favoriteButton.setText("unfavorite");
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isRowExist(movie_id)) {
                    addMovie(title,
                            movie_id,
                            poster,
                            backdrop,
                            overview,
                            vote_average,
                            release_date);
                    favoriteButton.setText("unfavorite");
                } else {
                    removeMovie(movie_id);
                    favoriteButton.setText("favorite");
                }
            }
        });

        return rootView;
    }

    private String getYear(String date) {
        return date.substring(0, 4);
    }

    private String getMonth(String date) {
        String monthNum = date.substring(5, 7);
        int num = Integer.parseInt(monthNum);
        return new DateFormatSymbols().getMonths()[num - 1];
    }

    private boolean isRowExist(String movieId) {
        Cursor cursor = resolver.query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.MOVIE_ID + " = ?",
                new String[]{movieId},
                null
        );

        if (cursor != null && cursor.getCount() <= 0) {
            Log.d(LOG, "isRowExist: doesn't exist " + cursor.getCount());
            cursor.close();
            return false;
        } else {
            Log.d(LOG, "isRowExist: exists " + cursor.getCount());
            cursor.close();
            return true;
        }
    }

    private void addMovie(String title,
                          String movie_id,
                          String poster,
                          String backdrop,
                          String overview,
                          String vote_average,
                          String release_date) {

        ContentValues values = new ContentValues();

        values.put(MovieContract.MovieEntry.TITLE, title);
        values.put(MovieContract.MovieEntry.MOVIE_ID, movie_id);
        values.put(MovieContract.MovieEntry.POSTER, poster);
        values.put(MovieContract.MovieEntry.BACKDROP, backdrop);
        values.put(MovieContract.MovieEntry.OVERVIEW, overview);
        values.put(MovieContract.MovieEntry.VOTE_AVERAGE, vote_average);
        values.put(MovieContract.MovieEntry.DATE, release_date);

        resolver.insert(MovieContract.MovieEntry.CONTENT_URI, values);
    }

    private void removeMovie(String movie_id) {
        resolver.delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_ID + " = ?",
                new String[]{movie_id});
    }
}
