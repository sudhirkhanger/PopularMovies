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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sudhirkhanger.app.popularmovies.Adapters.ReviewAdapter;
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
    private ArrayList<Trailer> mTrailerArrayList;
    private static final String YT_SHARE = "YouTube Link - ";
    private static final int FIRST_TRAILER_POS = 0;
    private static final String YT_NO_SHARE = "Youtube link not found";

    static final String DETAILS_OBJECT = "movie_object";
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
        setHasOptionsMenu(true);
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

        if (movie != null) {

            final String title = movie.getTitle();
            getActivity().setTitle(title);
            final String movie_id = movie.getId();
            final String poster = movie.getPosterPath();
            final String backdrop = movie.getBackdrops();
            final String overview = movie.getOverView();
            final String vote_average = movie.getVoteAverage();
            final String release_date = movie.getReleaseDate();

            final View view = rootView.findViewById(R.id.details_layout);
            view.setVisibility(View.VISIBLE);

            TextView detailTitle = (TextView) rootView.findViewById(R.id.details_title);
            TextView detailReleaseYear = (TextView) rootView.findViewById(R.id.details_release_year);
            TextView detailReleaseMonth = (TextView) rootView.findViewById(R.id.details_release_month);
            TextView detailVoteAverage = (TextView) rootView.findViewById(R.id.details_vote_average);
            TextView detailOverview = (TextView) rootView.findViewById(R.id.details_overview);
            ImageView detailBackdrops = (ImageView) rootView.findViewById(R.id.details_backdrop);
            ImageView detailThumbnail = (ImageView) rootView.findViewById(R.id.details_thumbnail);
            final Button favoriteButton = (Button) rootView.findViewById(R.id.favorite_button);

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
            reviewView(movie, rootView);
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

        mTrailerArrayList = new ArrayList<>();

        try {
            mTrailerArrayList =
                    new FetchTrailers().execute(movie.getId()).get();
        } catch (ExecutionException | InterruptedException ei) {
            ei.printStackTrace();
        }

        trailerRecyclerView.setAdapter(new TrailerAdapter(view.getContext(), mTrailerArrayList));
    }

    private void reviewView(Movie movie, View view) {

        RecyclerView reviewRecyclerView =
                (RecyclerView) view.findViewById(R.id.review_recyclerview);
        reviewRecyclerView.setHasFixedSize(true);

        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<Review> mReviewArrayList = new ArrayList<>();

        try {
            mReviewArrayList =
                    new FetchReviews().execute(movie.getId()).get();
        } catch (ExecutionException | InterruptedException ei) {
            ei.printStackTrace();
        }

        reviewRecyclerView.setAdapter(new ReviewAdapter(mReviewArrayList));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.menu_yt_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        if (mTrailerArrayList != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    YT_SHARE + mTrailerArrayList.get(FIRST_TRAILER_POS).getLink());
        } else {
            shareIntent.putExtra(Intent.EXTRA_TEXT, YT_NO_SHARE);
        }
        return shareIntent;
    }
}
