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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sudhirkhanger.app.popularmovies.Model.Trailer;
import com.sudhirkhanger.app.popularmovies.R;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private static final String LOG = Trailer.class.getSimpleName();

    private ArrayList<Trailer> mTrailerArrayList;
    private Context mContext;

    public static class TrailerViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public TrailerViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.trailer_imageview);
        }
    }

    public TrailerAdapter(Context context,
                          ArrayList<Trailer> trailerArrayList) {
        mContext = context;
        mTrailerArrayList = trailerArrayList;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_list_item, parent, false);

        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, final int position) {

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(mTrailerArrayList.get(position).getLink()));
                v.getContext().startActivity(intent);
            }
        });

        Picasso.with(mContext)
                .load(mTrailerArrayList.get(position).getImage())
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mTrailerArrayList == null ? 0 : mTrailerArrayList.size();
    }
}
