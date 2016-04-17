package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by sanjaydixit on 17/04/16.
 */
public class MovieReviewsAsyncTask extends AsyncTask<Integer, Void, List<Review>> {

    private IMovieResultsCallback<List<Review>> mCallback;

    private static final Tmdb manager = Tmdb.getInstance();

    public MovieReviewsAsyncTask(IMovieResultsCallback<List<Review>> callback) {
        mCallback = callback;
    }

    @Override
    protected List<Review> doInBackground(Integer... ids) {

        Integer id = null;

        if (ids.length > 0) {
            id = ids[0];
        }

        List<Review> reviewList = new ArrayList<>();
        ReviewResultsPage current = null;
        if (id != null) {
            while (current == null || !current.page.equals(current.total_pages)) {
                Call<ReviewResultsPage> call = manager.movieListService().reviews(id,
                        current != null ? current.page+1 : 1, null);
                try {
                    current = call.execute().body();
                    reviewList.addAll(current.results);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        return reviewList;
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        mCallback.onPostExecute(reviews);
    }

    @Override
    protected void onCancelled() {
        mCallback.onCancelled();
    }
}

