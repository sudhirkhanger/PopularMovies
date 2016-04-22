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

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sudhirkhanger.app.popularmoviesstageone.Model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter feeds the RecyclerView.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mMovieList;
    private Context mContext;
    private static final String PARCEL_KEY = "movie_parcel";

    public MovieAdapter(Context context, ArrayList<Movie> movieList) {
        mMovieList = movieList;
        mContext = context;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;
        public View mView;

        public MovieViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.imageview);
            mTextView = (TextView) view.findViewById(R.id.list_title);
        }
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                           int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.list_item, viewGroup, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder movieViewHolder,
                                 final int pos) {
        movieViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Movie movieParcel = mMovieList.get(pos);
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra(PARCEL_KEY, movieParcel);

                context.startActivity(intent);
            }
        });

        Picasso.with(mContext)
                .load(mMovieList.get(pos).getPosterPath())
                .into(movieViewHolder.mImageView);
        movieViewHolder.mTextView.setText(mMovieList.get(pos).getTitle());
    }

    public int getItemCount() {
        return mMovieList.size();
    }
}
