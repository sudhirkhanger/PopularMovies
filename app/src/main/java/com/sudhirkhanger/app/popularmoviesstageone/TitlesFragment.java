package com.sudhirkhanger.app.popularmoviesstageone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class TitlesFragment extends Fragment {

    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg";
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
        recyclerView.setAdapter(new MovieAdapter(getContext(), getDataSet(ITEM)));
    }

    private ArrayList<MovieModel> getDataSet(int i) {
        ArrayList arrayList = new ArrayList<MovieModel>();
        for (int index = 0; index < i; index++) {
            MovieModel movieModel = new MovieModel(IMAGE_URL);
            arrayList.add(movieModel);
        }
        return arrayList;
    }
}
