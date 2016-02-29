package com.sudhirkhanger.app.popularmoviesstageone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sudhirkhanger.app.popularmoviesstageone.Model.Constants;
import com.sudhirkhanger.app.popularmoviesstageone.Model.Movie;

import java.util.ArrayList;

public class TitlesFragment extends Fragment {

    private static final int COLUMN = 2;
    private static final int ITEM = 15;

    public TitlesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(R.layout.fragment_titles, container, false);
        setupRecyclerView(rv);
        return rv;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), COLUMN));

        ArrayList<Movie> movieArrayList = new JsonUtil().JsonToArrayList(
                Constants.MOVIE_JSON,
                ITEM);

        MovieAdapter movieAdapter = new MovieAdapter(getContext(),
                movieArrayList);

        recyclerView.setAdapter(
                new MovieAdapter(
                        getContext(),
                        new JsonUtil().JsonToArrayList(
                                Constants.MOVIE_JSON,
                                ITEM)));
    }
}
