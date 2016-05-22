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

package com.sudhirkhanger.app.popularmovies.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sudhirkhanger.app.popularmovies.Model.Review;
import com.sudhirkhanger.app.popularmovies.Model.Trailer;
import com.sudhirkhanger.app.popularmovies.R;

import java.util.ArrayList;

/**
 * Adapter for showing reviews
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static final String LOG = Trailer.class.getSimpleName();

    private ArrayList<Review> mReviewArrayList;

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        public TextView mAuthor;
        public TextView mContent;

        public ReviewViewHolder(View view) {
            super(view);
            mContent = (TextView) view.findViewById(R.id.content);
            mAuthor = (TextView) view.findViewById(R.id.author);

        }
    }

    public ReviewAdapter(ArrayList<Review> reviewArrayList) {
        mReviewArrayList = reviewArrayList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_item, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, final int position) {
        holder.mAuthor.setText(mReviewArrayList.get(position).getAuthor());
        holder.mContent.setText(mReviewArrayList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewArrayList == null ? 0 : mReviewArrayList.size();
    }
}
