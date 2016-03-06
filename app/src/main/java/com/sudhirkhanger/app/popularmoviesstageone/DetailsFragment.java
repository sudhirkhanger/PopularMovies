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
        TextView detailReleaseDate = (TextView) rootView.findViewById(R.id.details_release_date);
        TextView detailVoteAverage = (TextView) rootView.findViewById(R.id.details_vote_average);
        TextView detailOverview = (TextView) rootView.findViewById(R.id.details_overview);
        ImageView detailBackdrops = (ImageView) rootView.findViewById(R.id.details_backdrop);

        detailTitle.setText(parcelableExtra.getTitle());
        detailReleaseDate.setText(parcelableExtra.getReleaseDate());
        detailVoteAverage.setText(parcelableExtra.getVoteAverage());
        detailOverview.setText(parcelableExtra.getOverView());
        Picasso.with(rootView.getContext())
                .load(parcelableExtra.getBackdrops())
                .into(detailBackdrops);

        return rootView;
    }
}
