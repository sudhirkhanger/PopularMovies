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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sudhirkhanger.app.popularmoviesstageone.Model.Movie;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class RatingFragment extends Fragment {

    private static final String PAGE = "1";

    public RatingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        // Set column size to 2 for default and portrait
        // and 3 for landscape orientations
        int column = Integer.parseInt(getString(R.string.grid_portrait));
        if (getResources().getConfiguration().orientation == 1) {
            column = Integer.parseInt(getString(R.string.grid_portrait));
        } else if (getResources().getConfiguration().orientation == 2) {
            column = Integer.parseInt(getString(R.string.grid_landscape));
        }

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), column));

        ArrayList<Movie> mMovieArrayList = new ArrayList<Movie>();
        try {
            mMovieArrayList =
                    new FetchMoviesTask().execute(getString(R.string.url_top_rated), PAGE).get();
        } catch (ExecutionException | InterruptedException ei) {
            ei.printStackTrace();
        }
        mRecyclerView.setAdapter(new MovieAdapter(getActivity(), mMovieArrayList));
        return rootView;
    }
}
