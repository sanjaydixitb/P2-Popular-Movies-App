package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

/**
 * Created by sanjaydixit on 17/04/16.
 */
public interface IMovieResultsCallback<G> {

    public void onPostExecute(G result);

    public void onCancelled();
}
