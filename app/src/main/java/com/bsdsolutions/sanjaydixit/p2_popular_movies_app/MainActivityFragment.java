package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.app.AlertDialog;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String KEY_MOVIES = "movies";
    private static final String KEY_SELECTED_POSITION = "selected_position";
    private static final String KEY_SORT_ORDER = "sort_order";

    private static final double SCROLL_RATIO = 0.7;

    private static MovieArrayAdapter mMovieArrayAdapter = null;
    private static List<MovieObject> mMoviesList = null;
    private static MovieLoadTask mLoadTask = null;
    private static boolean mSortByPopularity = true;
    private static int mCurrentPosition = -1;
    private static MovieObjectResultsPage mCurrentPage = null;

    private static RecyclerView mRecyclerView = null;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rv = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView)rv;
        ButterKnife.bind(this, rv);
        setHasOptionsMenu(true);
        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mSortByPopularity = savedInstanceState.getBoolean(KEY_SORT_ORDER);
            mCurrentPosition = savedInstanceState.getInt(KEY_SELECTED_POSITION, -1);
            mMoviesList = savedInstanceState.getParcelableArrayList(KEY_MOVIES);
            Log.v(MovieObjectUtils.LOG_TAG,"Recovered [" + mMoviesList.size()+"] number of movies.");
        } else {
            mSortByPopularity = true;
            mCurrentPosition = -1;
            if(mMoviesList == null)
            mMoviesList = new ArrayList<>();
        }

        initRecyclerView();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(KEY_MOVIES, (ArrayList<? extends Parcelable>) mMoviesList);
        savedInstanceState.putInt(KEY_SELECTED_POSITION, mCurrentPosition);
        savedInstanceState.putBoolean(KEY_SORT_ORDER, mSortByPopularity);
    }

    private void initRecyclerView() {

        if(mRecyclerView == null)
        {
            Log.e(MovieObjectUtils.LOG_TAG, "Recycler View is Null!");
            return;
        }

        //Set LayoutManager and Adapter
        mMovieArrayAdapter = new MovieArrayAdapter(getActivity(), getContext(), mMoviesList);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), 3));
        else
            mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), 2));
        mRecyclerView.setAdapter(mMovieArrayAdapter);


        if (mCurrentPosition != -1)
            mRecyclerView.scrollToPosition(mCurrentPosition);

        if (mMoviesList.size() == 0)
            updateMovieList(mSortByPopularity);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING &&
                        (double) ((GridLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition() / (double) mMovieArrayAdapter.getItemCount() >= SCROLL_RATIO)
                    updateMovieList(mSortByPopularity);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main_fragment, menu);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings) {
            DialogFragment newFragment = new SortingOptionDialog();
            newFragment.show(getFragmentManager(), "sorting_criteria");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static void clearMovieList() {
        if(mLoadTask != null && mLoadTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadTask.cancel(true);
        } else {
            mMoviesList.clear();
            mMovieArrayAdapter.notifyDataSetChanged();
        }

        mCurrentPage = null;
    }

    public static void updateMovieList(boolean sortByPopularity) {
        mSortByPopularity = sortByPopularity;
        if(mLoadTask != null && mLoadTask.getStatus() == AsyncTask.Status.RUNNING) {
            Log.v(MovieObjectUtils.LOG_TAG,"Cancelling Running task");
            mLoadTask.cancel(true);
        }
        mLoadTask = new MovieLoadTask(new IMovieResultsCallback<MovieObjectResultsPage>() {
            @Override
            public void onPostExecute(MovieObjectResultsPage page) {
                if (page != null) {
                    mCurrentPage = page;
                    mMoviesList.addAll(page.results);
                    mMovieArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled() {
                mMoviesList.clear();
                mMovieArrayAdapter.notifyDataSetChanged();
            }
        }, mCurrentPage != null ? mCurrentPage.page+1 : 1);
        mLoadTask.execute(sortByPopularity);
    }

    private static class MovieLoadTask extends AsyncTask<Boolean, Void, MovieObjectResultsPage> {

        public IMovieResultsCallback<MovieObjectResultsPage> mCallback;
        private Integer mPageNumber;

        private static final Tmdb manager = Tmdb.getInstance();

        public MovieLoadTask(IMovieResultsCallback<MovieObjectResultsPage> callback, int page) {
            mCallback = callback;
            mPageNumber = page;
        }

        @Override
        protected MovieObjectResultsPage doInBackground(Boolean... params) {

            boolean sortByPopularity = true;
            if(params.length > 0) {
                sortByPopularity = params[0];
            }

            MovieObjectResultsPage page = null;
            if (sortByPopularity) {
                Log.v(MovieObjectUtils.LOG_TAG,"Getting popular page :" + mPageNumber);
                Call<MovieObjectResultsPage> call = manager.movieListService().popular(mPageNumber, null);
                try {
                    page = call.execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.v(MovieObjectUtils.LOG_TAG,"Getting top rated page :" + mPageNumber);
                Call<MovieObjectResultsPage> call = manager.movieListService().topRated(mPageNumber, null);
                try {
                    page = call.execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return page;
        }

        @Override
        protected void onPostExecute(MovieObjectResultsPage moviesPage) {
            mCallback.onPostExecute(moviesPage);
        }

        @Override
        protected void onCancelled() {
            mCallback.onCancelled();
        }
    }

    public static class SortingOptionDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.pick_sorting_criteria)
                    .setItems(R.array.sorting_options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            clearMovieList();
                            updateMovieList(which == 0);
                        }
                    });
            return builder.create();
        }
    }

}