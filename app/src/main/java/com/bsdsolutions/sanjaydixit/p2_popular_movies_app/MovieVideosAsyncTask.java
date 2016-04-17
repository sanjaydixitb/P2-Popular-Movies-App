package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by sanjaydixit on 17/04/16.
 */
public class MovieVideosAsyncTask extends AsyncTask<Integer, Void, Videos> {

    private IMovieResultsCallback<Videos> mCallback;

    private static final Tmdb manager = Tmdb.getInstance();

    public MovieVideosAsyncTask(IMovieResultsCallback<Videos> callback) {
        mCallback = callback;
    }

    @Override
    protected Videos doInBackground(Integer... ids) {

        Integer id = null;

        Log.v(MovieObjectUtils.LOG_TAG,"length of ids : " + ids.length);
        if (ids.length > 0) {
            id = ids[0];
            Log.v(MovieObjectUtils.LOG_TAG,"id:" + id);
        }

        Videos videos = null;
        if (id != null) {
            Call<Videos> call = manager.movieListService().videos(id,null);
            try {
                videos = call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return videos;
    }

    @Override
    protected void onPostExecute(Videos videos) {
        mCallback.onPostExecute(videos);
    }

    @Override
    protected void onCancelled() {
        mCallback.onCancelled();
    }
}
