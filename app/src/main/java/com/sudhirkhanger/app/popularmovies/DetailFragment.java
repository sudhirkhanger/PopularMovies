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
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sudhirkhanger.app.popularmovies.Adapters.TrailerAdapter;
import com.sudhirkhanger.app.popularmovies.FetchTasks.FetchReviews;
import com.sudhirkhanger.app.popularmovies.FetchTasks.FetchTrailers;
import com.sudhirkhanger.app.popularmovies.Model.Movie;
import com.sudhirkhanger.app.popularmovies.Model.MovieContract;
import com.sudhirkhanger.app.popularmovies.Model.Review;
import com.sudhirkhanger.app.popularmovies.Model.Trailer;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Fragment shows detailed information about the movies.
 */
public class DetailFragment extends Fragment {

    private ContentResolver resolver;

    static final String DETAILS_OBJECT = "movie_object";
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
    }

    public static DetailFragment newInstance(Movie movie) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DETAILS_OBJECT, movie);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        Movie movie = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            movie = bundle.getParcelable(DETAILS_OBJECT);
        } else if (savedInstanceState != null) {
            movie = savedInstanceState.getParcelable(DETAILS_OBJECT);
        }

        resolver = getActivity().getContentResolver();

        final Button favoriteButton = (Button) rootView.findViewById(R.id.favorite_button);
        favoriteButton.setVisibility(View.INVISIBLE);

        if (movie != null) {


            favoriteButton.setVisibility(View.VISIBLE);

            final String title = movie.getTitle();
            getActivity().setTitle(title);
            final String movie_id = movie.getId();
            final String poster = movie.getPosterPath();
            final String backdrop = movie.getBackdrops();
            final String overview = movie.getOverView();
            final String vote_average = movie.getVoteAverage();
            final String release_date = movie.getReleaseDate();

            TextView detailTitle = (TextView) rootView.findViewById(R.id.details_title);
            TextView detailReleaseYear = (TextView) rootView.findViewById(R.id.details_release_year);
            TextView detailReleaseMonth = (TextView) rootView.findViewById(R.id.details_release_month);
            TextView detailVoteAverage = (TextView) rootView.findViewById(R.id.details_vote_average);
            TextView detailOverview = (TextView) rootView.findViewById(R.id.details_overview);
            ImageView detailBackdrops = (ImageView) rootView.findViewById(R.id.details_backdrop);
            ImageView detailThumbnail = (ImageView) rootView.findViewById(R.id.details_thumbnail);
//            final Button favoriteButton = (Button) rootView.findViewById(R.id.favorite_button);

            detailTitle.setText(movie.getTitle());
            detailReleaseYear.setText(getYear((movie.getReleaseDate())));
            detailReleaseMonth.setText(getMonth(movie.getReleaseDate()));
            detailVoteAverage.setText(movie.getVoteAverage() + "/10");
            detailOverview.setText(movie.getOverView());
            Picasso.with(rootView.getContext())
                    .load(movie.getBackdrops())
                    .into(detailBackdrops);
            Picasso.with(rootView.getContext())
                    .load(movie.getPosterPath())
                    .into(detailThumbnail);

            if (isRowExist(movie_id)) {
                favoriteButton.setText(R.string.unfavorite_Button);
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
                        favoriteButton.setText(R.string.unfavorite_Button);
                    } else {
                        removeMovie(movie_id);
                        favoriteButton.setText(R.string.favorite_Button);
                    }
                }
            });

            trailerView(movie, rootView);

            // Trailer print keys
            ArrayList<Review> mReviewsArrayList = new ArrayList<>();
            try {
                mReviewsArrayList =
                        new FetchReviews().execute(movie.getId()).get();
            } catch (ExecutionException | InterruptedException ei) {
                ei.printStackTrace();
            }

            if (mReviewsArrayList != null) {
                for (Review review : mReviewsArrayList) {
                    Log.d(LOG_TAG, review.toString());
                }
            }
        }
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
            Log.d(LOG_TAG, "isRowExist: doesn't exist " + cursor.getCount());
            cursor.close();
            return false;
        } else {
            Log.d(LOG_TAG, "isRowExist: exists " + cursor.getCount());
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

    private void trailerView(Movie movie, View view) {

        RecyclerView trailerRecyclerView =
                (RecyclerView) view.findViewById(R.id.trailer_recyclerview);
        trailerRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        trailerRecyclerView.setLayoutManager(layoutManager);

        ArrayList<Trailer> mTrailerArrayList = new ArrayList<>();

        try {
            mTrailerArrayList =
                    new FetchTrailers().execute(movie.getId()).get();
        } catch (ExecutionException | InterruptedException ei) {
            ei.printStackTrace();
        }

        trailerRecyclerView.setAdapter(new TrailerAdapter(view.getContext(), mTrailerArrayList));
    }
}
