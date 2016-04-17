package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

    private static MovieArrayAdapter movieArrayAdapter = null;
    private static List<MovieObject> mMoviesList = null;
    private static MovieLoadTask mLoadTask = null;
    private static boolean mSortByPopularity = true;
    private static int mCurrentPosition = -1;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rv = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rv);
        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mSortByPopularity = (Boolean)savedInstanceState.getSerializable(KEY_SORT_ORDER);
            mCurrentPosition = savedInstanceState.getInt(KEY_SELECTED_POSITION, -1);
            mMoviesList = savedInstanceState.getParcelableArrayList(KEY_MOVIES);
        } else {
            mSortByPopularity = true;
            mCurrentPosition = -1;
            mMoviesList = new ArrayList<>();
        }

        movieArrayAdapter = new MovieArrayAdapter(getActivity(), mMoviesList);
        initRecyclerView();

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

    public static void updateMovieList(boolean sortByPopularity) {
        //If it is already sorted by popularity, no need to fetch data again.
        if(mSortByPopularity == sortByPopularity) {
            return;
        }
        if(mLoadTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadTask.cancel(true);
        } else {
            List<MovieObject> movieObjects = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                movieObjects.add(new MovieObject(""));
            }
            movieArrayAdapter.updateDataSet(movieObjects);
        }
        mLoadTask = new MovieLoadTask();
        mLoadTask.execute(sortByPopularity);
        mSortByPopularity = sortByPopularity;
    }

    private static class MovieLoadTask extends AsyncTask<Boolean, Void, MovieObjectResultsPage> {

        private IMovieResultsCallback<MovieObjectResultsPage> mCallback;
        private Integer mPageNumber;

        private static final Tmdb manager = Tmdb.getInstance();

        @Override
        protected MovieObjectResultsPage doInBackground(Boolean... params) {

            boolean sortByPopularity = true;
            if(params.length > 0) {
                sortByPopularity = params[0];
            }

            MovieObjectResultsPage page = null;
            if (sortByPopularity) {
                Call<MovieObjectResultsPage> call = manager.movieListService().popular(mPageNumber, null);
                try {
                    page = call.execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
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
                            updateMovieList(which == 0);
                        }
                    });
            return builder.create();
        }
    }

}