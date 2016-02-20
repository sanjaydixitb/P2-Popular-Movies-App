package com.bsdsolutions.sanjaydixit.p1_popular_movies_app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    TextView mDate = null,mVoteAverage = null,mSynopsys = null;
    ImageView mPoster = null;
    ProgressBar mLoadingBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String content = intent.getStringExtra(MovieObjectUtils.KEY_OBJECT_CONTENT_EXTRA);

        setUpView(content);

    }

    void setUpView(String content) {
        MovieObject object = new MovieObject(content);

        mPoster = (ImageView)findViewById(R.id.movie_poster_detail);
        mPoster.setAdjustViewBounds(true);
        mDate = (TextView)findViewById(R.id.synopsys_date);
        mSynopsys = (TextView)findViewById(R.id.synopsys_detail);
        mVoteAverage = (TextView)findViewById(R.id.vote_average_detail);
        mLoadingBar = (ProgressBar)findViewById(R.id.detailLoadingAnimation);

        Uri uri = Uri.parse(MovieObjectUtils.IMAGE_PREFIX).buildUpon().appendEncodedPath(object.movie_poster).build();
        Log.v(MovieObjectUtils.LOG_TAG, "Getting image : " + uri.toString());
        Picasso.with(this).load(uri.toString()).into(mPoster, new Callback() {
            @Override
            public void onSuccess() {
                mLoadingBar.setVisibility(View.GONE);
                mPoster.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                //TODO: Add error image
            }
        });

        String release_date = object.release_date;
        String releaseYear = release_date.substring(0,release_date.indexOf("-"));
        mDate.setText(releaseYear);
        mSynopsys.setText(object.plot_synopsis);
        mVoteAverage.setText(String.valueOf(object.vote_average) + "/10");
    }

}
