package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sanjaydixit on 11/02/16.
 */
public class MovieArrayAdapter extends RecyclerView.Adapter<MovieArrayAdapter.MovieViewHolder> {

    private Context mContext;
    private FragmentActivity mActivity;
    private List<MovieObject> movieObjectList;

    MovieArrayAdapter(FragmentActivity activity, Context context, List<MovieObject> movieObjects) {
        mActivity = activity;
        movieObjectList = movieObjects;
        mContext = context;
    }

    public void updateDataSet(List<MovieObject> movieObjects) {
        if(movieObjects.size() == 0) {
            //Dummy objects have size 20. Size == 0 means failed to retrieve data from internet.
            Toast.makeText(mContext,"Failed to fetch movie data!", Toast.LENGTH_SHORT).show();
        }
        movieObjectList = movieObjects;
        this.notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_layout,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        final MovieObject object = movieObjectList.get(position);

        final TextView errorText = holder.mErrorText;
        final ImageView imageView = holder.mImageView;
        final ProgressBar loadingBar = holder.mLoadingBar;

        final String posterPath = object.poster_path;

        loadingBar.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

        //Poster path is empty for dummy data and null if image is not available in the database.
        if(posterPath.compareToIgnoreCase("") != 0 && posterPath.compareToIgnoreCase("null") != 0)
        {
            Uri uri = Uri.parse(MovieObjectUtils.IMAGE_PREFIX).buildUpon().appendEncodedPath(posterPath).build();
            //Log.v(MovieObjectUtils.LOG_TAG,"Getting image : " + uri.toString());
            Picasso.with(mContext).load(uri.toString()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    holder.mMovieLoaded = true;
                    loadingBar.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    holder.mMovieLoaded = true;
                    errorText.setVisibility(View.VISIBLE);
                    loadingBar.setVisibility(View.GONE);
                }
            });
        }
        else
        {
            if(posterPath.compareToIgnoreCase("null") == 0) {   //Image Not Available
                holder.mMovieLoaded = true;
                loadingBar.setVisibility(View.GONE);
                errorText.setVisibility(View.VISIBLE);
            }
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mMovieLoaded) {
                    MovieDetailsFragment detailsFragment;
                    if(MainActivity.detailPage) {
                        detailsFragment = (MovieDetailsFragment)mActivity.getSupportFragmentManager().findFragmentById(R.id.movie_details_container);
                        detailsFragment.setCheckBoxListener((IFavoriteCheckboxUpdateListener)mActivity);
                        detailsFragment.updateContent(object);
                    } else {
                        detailsFragment= MovieDetailsFragment.create(object);
                        detailsFragment.setCheckBoxListener((IFavoriteCheckboxUpdateListener)mActivity);
                        mActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movies_grid_container, detailsFragment, null)
                                .addToBackStack(null)
                                .commit();
                    }
                } else {
                    Toast.makeText(mContext,mContext.getString(R.string.loading_movie_details),Toast.LENGTH_SHORT).show();
                }
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
        public TextView mErrorText;         //Can be used to display other error messages later
        public volatile boolean mMovieLoaded = false;

        MovieViewHolder(View inView) {
            super(inView);
            mView = inView;
            mImageView = (ImageView)inView.findViewById(R.id.gridView_movie_thumbnail);
            mImageView.setAdjustViewBounds(true);
            mLoadingBar = (ProgressBar)inView.findViewById(R.id.loadingAnimation);
            mErrorText = (TextView)inView.findViewById(R.id.fail_load_image_textView);
        }

    }

}
