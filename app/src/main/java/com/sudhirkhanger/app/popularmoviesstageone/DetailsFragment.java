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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sudhirkhanger.app.popularmoviesstageone.Model.Movie;

import java.text.DateFormatSymbols;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment {

    private static final String PARCEL_KEY = "movie_parcel";

    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        Movie parcelableExtra = getActivity().getIntent().getParcelableExtra(PARCEL_KEY);

        TextView detailTitle = (TextView) rootView.findViewById(R.id.details_title);
        TextView detailReleaseYear = (TextView) rootView.findViewById(R.id.details_release_year);
        TextView detailReleaseMonth = (TextView) rootView.findViewById(R.id.details_release_month);
        TextView detailVoteAverage = (TextView) rootView.findViewById(R.id.details_vote_average);
        TextView detailOverview = (TextView) rootView.findViewById(R.id.details_overview);
        ImageView detailBackdrops = (ImageView) rootView.findViewById(R.id.details_backdrop);
        ImageView detailThumbnail = (ImageView) rootView.findViewById(R.id.details_thumbnail);

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
}
