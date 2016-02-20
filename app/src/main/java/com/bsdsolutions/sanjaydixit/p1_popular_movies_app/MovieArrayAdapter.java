package com.bsdsolutions.sanjaydixit.p1_popular_movies_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sanjaydixit on 11/02/16.
 */
public class MovieArrayAdapter extends RecyclerView.Adapter<MovieArrayAdapter.MovieViewHolder> {

    private Context mContext;
    private List<MovieObject> movieObjectList;

    MovieArrayAdapter(Context context, List<MovieObject> movieObjects) {
        movieObjectList = movieObjects;
        mContext = context;
    }

    public void updateDataSet(List<MovieObject> movieObjects) {
        movieObjectList = movieObjects;
        this.notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridview_item_layout,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        final MovieObject object = movieObjectList.get(position);
        holder.mLoadingBar.setVisibility(View.VISIBLE);
        holder.mImageView.setVisibility(View.GONE);
        if(object.imgPath.compareToIgnoreCase("") != 0)
        {
            final String IMAGE_BASE_URI = "http://image.tmdb.org/t/p/";
            final String RESOLUTION = "w185";
            Uri uri = Uri.parse(IMAGE_BASE_URI).buildUpon().appendPath(RESOLUTION).appendEncodedPath(object.imgPath).build();
            Log.v(MovieObjectUtils.LOG_TAG,"Getting image : " + uri.toString());
            Picasso.with(mContext).load(uri.toString()).into(holder.mImageView, new Callback() {
                @Override
                public void onSuccess() {
                    holder.mLoadingBar.setVisibility(View.GONE);
                    holder.mImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    //TODO: Add error image
                }
            });
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,DetailActivity.class);
                intent.putExtra(MovieObjectUtils.KEY_OBJECT_ID_EXTRA,object.id);
                intent.putExtra(MovieObjectUtils.KEY_OBJECT_CONTENT_EXTRA,object.content);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movieObjectList.size();
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public ImageView mImageView;
        public ProgressBar mLoadingBar;

        MovieViewHolder(View inView) {
            super(inView);
            mView = inView;
            mImageView = (ImageView)inView.findViewById(R.id.gridView_movie_thumbnail);
            mImageView.setAdjustViewBounds(true);
            mLoadingBar = (ProgressBar)inView.findViewById(R.id.loadingAnimation);
        }

    }

}
