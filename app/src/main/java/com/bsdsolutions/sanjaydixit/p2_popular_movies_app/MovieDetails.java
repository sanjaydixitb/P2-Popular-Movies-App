package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

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

    TextView mDate = null,mVoteAverage = null,mSynopsys = null, mTitle = null,mErrorText = null;
    ImageView mPoster = null;
    ProgressBar mLoadingBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        mTitle = (TextView)findViewById(R.id.movie_title);
        mVoteAverage = (TextView)findViewById(R.id.vote_average_detail);
        mLoadingBar = (ProgressBar)findViewById(R.id.detailLoadingAnimation);
        mErrorText = (TextView)findViewById(R.id.fail_load_image_textView_detail);

        mLoadingBar.setVisibility(View.VISIBLE);
        mPoster.setVisibility(View.GONE);
        mErrorText.setVisibility(View.GONE);


        if(object.movie_poster.length() > 0 && object.movie_poster.compareToIgnoreCase("null") != 0) {
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
                    mLoadingBar.setVisibility(View.GONE);
                    mErrorText.setVisibility(View.VISIBLE);
                }
            });
        } else {
            mLoadingBar.setVisibility(View.GONE);
            mErrorText.setVisibility(View.VISIBLE);
        }

        String release_date = object.release_date;
        String releaseYear = release_date;
        int yearDelimiter = release_date.indexOf("-");
        if(yearDelimiter != -1)
            releaseYear = release_date.substring(0,yearDelimiter);

        mDate.setText(releaseYear);
        mTitle.setText(object.title);
        mSynopsys.setText(object.plot_synopsis);
        mVoteAverage.setText(String.valueOf(object.vote_average) + "/10");
    }

}
