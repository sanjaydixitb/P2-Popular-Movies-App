package com.bsdsolutions.sanjaydixit.p1_popular_movies_app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static MovieArrayAdapter movieArrayAdapter = null;
    private static MovieLoadTask mLoadTask = null;
    private static boolean mSortByPopularity = true;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mLoadTask = new MovieLoadTask();
        mLoadTask.execute(mSortByPopularity);

        //has Options menu
        setHasOptionsMenu(true);

        //Dummy Data to show loading at the beginning if internet connection is slow
        List<MovieObject> movieObjects = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            movieObjects.add(new MovieObject(""));
        }

        RecyclerView rv = (RecyclerView) inflater.inflate(R.layout.fragment_main, container, false);

        //Set LayoutManager and Adapter
        movieArrayAdapter = new MovieArrayAdapter(getContext(), movieObjects);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            rv.setLayoutManager(new GridLayoutManager(rv.getContext(), 3));
        else
            rv.setLayoutManager(new GridLayoutManager(rv.getContext(), 2));
        rv.setAdapter(movieArrayAdapter);

        return rv;
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
        }
        return super.onOptionsItemSelected(item);
    }

    public static void updateMovieList(boolean sortByPopularity) {
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

    private static class MovieLoadTask extends AsyncTask<Boolean, Void, List<MovieObject>> {

        private List<MovieObject> getMovieList(String movieJsonStr) throws JSONException {

            List<MovieObject> movieObjectList = new ArrayList<>();
            JSONObject object = new JSONObject(movieJsonStr);

            JSONArray results = object.getJSONArray("results");

            int length = results.length();
            Log.d(MovieObjectUtils.LOG_TAG,"Got " + length + " movies!");

            for(int i=0; i<length ; i++) {
                JSONObject movie = results.getJSONObject(i);
                MovieObject obj = new MovieObject(movie);
                movieObjectList.add(obj);
            }

            return movieObjectList;

        }

        @Override
        protected List<MovieObject> doInBackground(Boolean... params) {

            HttpURLConnection urlConnection = null;

            BufferedReader reader = null;

            String movieJsonStr;
            List<MovieObject> updatedList = new ArrayList<>();

            boolean sortByPopularity = true;

            if(params.length > 0) {
                sortByPopularity = params[0];
            }

            //Using code from Sunshine App to get Movie Data!

            try {
                final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
                final String SORT_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = null;

                if(sortByPopularity) {
                    builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                            .appendQueryParameter(SORT_PARAM, "popularity.desc")
                            .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY).build();
                }
                else {
                    builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                            .appendQueryParameter(SORT_PARAM, "vote_average.desc")
                            .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY).build();
                }
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return updatedList;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return updatedList;
                }
                movieJsonStr = buffer.toString();
                updatedList = getMovieList(movieJsonStr);
            } catch (IOException e) {
                Log.e(MovieObjectUtils.LOG_TAG, "Error ", e);
            } catch (JSONException e) {
                Log.e(MovieObjectUtils.LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(MovieObjectUtils.LOG_TAG, "Error closing stream", e);
                    }
                }

                return updatedList;
            }
        }

        @Override
        protected void onPostExecute(List<MovieObject> movieObjects) {
            movieArrayAdapter.updateDataSet(movieObjects);
            super.onPostExecute(movieObjects);
        }

        @Override
        protected void onCancelled() {
            //Dummy Data to show loading at the beginning if internet connection is slow
            List<MovieObject> movieObjects = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                movieObjects.add(new MovieObject(""));
            }
            movieArrayAdapter.updateDataSet(movieObjects);
            super.onCancelled();
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