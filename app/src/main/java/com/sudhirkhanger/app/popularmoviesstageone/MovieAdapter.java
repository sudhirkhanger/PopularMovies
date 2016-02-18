package com.sudhirkhanger.app.popularmoviesstageone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<MovieModel> mMovieModelList;
    private Context mContext;

    public MovieAdapter(Context context, List<MovieModel> movieModelList) {
        mMovieModelList = movieModelList;
        mContext = context;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public MovieViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.imageview);
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
    public void onBindViewHolder(MovieViewHolder movieViewHolder,
                                 int pos) {
        Picasso.with(mContext).
                load(mMovieModelList.get(pos).getMposterUrl()).
                into(movieViewHolder.mImageView);
    }

    public int getItemCount() {
        return mMovieModelList.size();
    }
}
