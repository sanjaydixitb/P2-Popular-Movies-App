package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieDetailsFragment extends Fragment {

    private static final String KEY_MOVIE = "movie";
    private static final String KEY_RUNTIME = "runtime";
    private static final String KEY_VIDEOS = "results";
    private static final String KEY_REVIEWS = "reviews";

    private static Integer mRuntime;
    private static List<Video> mVideoList;
    private static List<Review> mReviewList;
    private MovieObject mMovie;
    private static Video mTrailer;
    private MenuItem mMenuItemShare;
    private MoviesAppHelper mHelper;
    private boolean mFavorite = false;

    private static MovieReviewsAsyncTask mMovieReviewsAsyncTask = null;
    private static MovieVideosAsyncTask mMovieVideosAsyncTask = null;

    private List<Runnable> mDeferredUiOperations = new ArrayList<>();

    @Bind(R.id.movie_title) TextView mTitleView;
    @Bind(R.id.movie_poster_detail) ImageView mPosterView;
    @Bind(R.id.synopsys_date) TextView mDateView;
    @Bind(R.id.vote_average_detail) TextView mVoteAverageView;
    @Bind(R.id.synopsys_detail) TextView mSynopsysView;
    @Bind(R.id.videos) ViewGroup mVideosView;
    @Bind(R.id.reviews) ViewGroup mReviewsView;
    @Bind(R.id.detailLoadingAnimation) ProgressBar mLoadingBarView;
    @Bind(R.id.fail_load_image_textView_detail) TextView mErrorTextView;
    @Bind(R.id.favorite_check_box) CheckBox mFavoriteCheckBox;

    public MovieDetailsFragment() {

    }

    public static MovieDetailsFragment create(MovieObject movie) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_MOVIE, movie);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHelper = new MoviesAppHelper(activity);
    }

    private void clearContent() {
        if(mReviewsView != null) {
            int count = mReviewsView.getChildCount();
            if (count > 2) {
                mReviewsView.removeViews(2, count - 2);
            }
        }
        if(mVideosView != null) {
            int count = mVideosView.getChildCount();
            if (count > 2) {
                mVideosView.removeViews(2, count - 2);
            }
        }
        if(mFavoriteCheckBox != null)
            mFavoriteCheckBox.setChecked(false);
    }

    public void updateContent(MovieObject object) {
        clearContent();
        mMovie = object;
        if (mMovie != null) {
            mTitleView.setText(mMovie.original_title);
            SharedPreferences preferences = getActivity().getSharedPreferences(MovieObjectUtils.KEY_PREFERENCES,Context.MODE_PRIVATE);
            if(preferences.contains(String.valueOf(mMovie.id))) {
                mFavorite = true;
            } else {
                mFavorite = false;
            }
            mFavoriteCheckBox.setChecked(mFavorite);
            mFavoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mFavorite = isChecked;
                    SharedPreferences prefs = getActivity().getSharedPreferences(MovieObjectUtils.KEY_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    if(mFavorite) {
                        //Add to shared pref
                        if(prefs != null && editor != null) {
                            editor.putBoolean(String.valueOf(mMovie.id),true);
                            editor.commit();
                        }
                    } else {
                        //remove from shared pref
                        if(prefs != null && editor != null) {
                            editor.remove(String.valueOf(mMovie.id));
                            editor.commit();
                        }
                    }
                }
            });
            String posterPath = mMovie.getPoster_path();
            if(posterPath.compareToIgnoreCase("") != 0 && posterPath.compareToIgnoreCase("null") != 0)
            {
                Picasso.with(getActivity()).load(posterPath).into(mPosterView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mLoadingBarView.setVisibility(View.GONE);
                        mPosterView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        mErrorTextView.setVisibility(View.VISIBLE);
                        mLoadingBarView.setVisibility(View.GONE);
                    }
                });
            }
            else
            {
                if(posterPath.compareToIgnoreCase("null") == 0) {   //Image Not Available
                    mLoadingBarView.setVisibility(View.GONE);
                    mErrorTextView.setVisibility(View.VISIBLE);
                }
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(mMovie.release_date);
            mDateView.setText(Integer.toString(cal.get(Calendar.YEAR)));
            mVoteAverageView.setText(String.format(getResources().getString(R.string.vote_average_format), mMovie.vote_average));
            mSynopsysView.setText(mMovie.overview);

            getVideos();
            getReviews();

        } else {
            mTitleView.setText(getResources().getString(R.string.message_select_movie));
            mFavoriteCheckBox.setChecked(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        setHasOptionsMenu(true);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.details_fragment, container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mRuntime = savedInstanceState.getInt(KEY_RUNTIME);
            mVideoList = savedInstanceState.getParcelableArrayList(KEY_VIDEOS);
            mReviewList = savedInstanceState.getParcelableArrayList(KEY_REVIEWS);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getArguments() != null) {
            MovieObject movie = getArguments().getParcelable(KEY_MOVIE);
            updateContent(movie);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_details_fragment, menu);
        mMenuItemShare = menu.findItem(R.id.menu_share);

        Drawable drawable = mMenuItemShare.getIcon();
        if (drawable != null) {
            // If we don't mutate the drawable, then all drawable's with this id will have a color
            // filter applied to it.
            drawable.mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

        tryExecuteDeferredUiOperations();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_share) {
            if (mTrailer != null)
                mHelper.shareTrailer(R.string.share_template, mTrailer);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRuntime != null) outState.putInt(KEY_RUNTIME, mRuntime);
        if (mReviewList != null) outState.putParcelableArrayList(KEY_REVIEWS, new ArrayList<>(mReviewList));
        if (mVideoList != null) outState.putParcelableArrayList(KEY_VIDEOS, new ArrayList<Video>(mVideoList));
    }

    @Override
    public void onDestroyView() {
        if(mMovieReviewsAsyncTask != null && mMovieReviewsAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mMovieReviewsAsyncTask.cancel(true);
            mMovieReviewsAsyncTask = null;
        }
        if(mMovieVideosAsyncTask != null && mMovieVideosAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mMovieVideosAsyncTask.cancel(true);
            mMovieVideosAsyncTask = null;
        }
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void getReviews() {
        if(mMovieReviewsAsyncTask != null && mMovieReviewsAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mMovieReviewsAsyncTask.cancel(true);
            mMovieReviewsAsyncTask = null;
        }
        mMovieReviewsAsyncTask =  new MovieReviewsAsyncTask(new IMovieResultsCallback<List<Review>>() {
            @Override
            public void onPostExecute(List<Review> reviews) {
                loadReviews(reviews);
            }

            @Override
            public void onCancelled() {
            }
        });
        mMovieReviewsAsyncTask.execute(mMovie.id);
    }

    private void loadReviews(List<Review> reviewList) {
        mReviewList = reviewList;

        boolean hasReviews = false;

        int count = mReviewsView.getChildCount();
        if(count > 2) {
            mReviewsView.removeViews(2,count-2);
        }
        if (reviewList.size() > 0) {
            for (Review review : reviewList) {
                if (TextUtils.isEmpty(review.author)) {
                    continue;
                }
                final LayoutInflater inflater = LayoutInflater.from(getActivity());

                final View reviewView = inflater.inflate(R.layout.details_fragment_review_item, mReviewsView, false);
                final TextView reviewAuthorView = (TextView)reviewView.findViewById(R.id.review_author);
                final TextView reviewContentView = (TextView)reviewView.findViewById(R.id.review_content);

                reviewAuthorView.setText(review.author);
                reviewContentView.setText(review.content);

                mReviewsView.addView(reviewView);
                hasReviews = true;
            }
        }

        mReviewsView.setVisibility(hasReviews ? View.VISIBLE : View.GONE);
    }

    private void getVideos() {
        if(mMovieVideosAsyncTask != null && mMovieVideosAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mMovieVideosAsyncTask.cancel(true);
            mMovieVideosAsyncTask = null;
        }
        mMovieVideosAsyncTask = new MovieVideosAsyncTask(new IMovieResultsCallback<Videos>() {
            @Override
            public void onPostExecute(Videos videoList1) {
                loadVideos(videoList1.results);
            }

            @Override
            public void onCancelled() {
            }
        });
        Log.v(MovieObjectUtils.LOG_TAG,"Getting videos for movie " + mMovie.original_title + " id : " + mMovie.id);
        mMovieVideosAsyncTask.execute(mMovie.id);
        return;
    }

    private void loadVideos(List<Video> videoList) {
        mVideoList = videoList;


        boolean hasVideos = false;

        int count = mVideosView.getChildCount();
        if(count > 2) {
            mVideosView.removeViews(2,count-2);
        }

        if (videoList.size() > 0) {
            for (Video video : videoList) {
                if (video.type.equals(Video.TYPE_TRAILER)) {
                    mTrailer = video;
                }
            }
            final LayoutInflater inflater = LayoutInflater.from(getActivity());

            for (Video video : videoList) {
                final View videoItem = inflater.inflate(R.layout.details_fragment_video_item, mVideosView, false);
                final TextView videoTitleView = (TextView)videoItem.findViewById(R.id.video_title);

                videoTitleView.setText(video.site + ": " + video.name);
                videoItem.setTag(video);
                videoItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHelper.playVideo((Video) v.getTag());
                    }
                });

                mVideosView.addView(videoItem);
                hasVideos = true;
            }
        }

        showShareMenuItemDeferred(mTrailer != null);
        mVideosView.setVisibility(hasVideos ? View.VISIBLE : View.GONE);
    }

    private void showShareMenuItemDeferred(final boolean visible) {
        mDeferredUiOperations.add(new Runnable() {
            @Override
            public void run() {
                mMenuItemShare.setVisible(visible);
            }
        });
        tryExecuteDeferredUiOperations();
    }

    private void tryExecuteDeferredUiOperations() {
        if (mMenuItemShare != null) {
            for (Runnable r : mDeferredUiOperations) {
                r.run();
            }
            mDeferredUiOperations.clear();
        }
    }

}
