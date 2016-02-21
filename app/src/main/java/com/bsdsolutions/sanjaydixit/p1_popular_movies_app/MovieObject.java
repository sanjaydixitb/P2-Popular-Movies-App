package com.bsdsolutions.sanjaydixit.p1_popular_movies_app;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sanjaydixit on 11/02/16.
 */
public class MovieObject {
    //To be used in recyclerView
    public String movie_poster;  //For getting the thumbnail images
    public int id;              //For getting trailers and reviews later on.
    public String title, release_date, plot_synopsis;
    public double vote_average;

    //To be used in MovieDetails
    public String content;

    MovieObject(String JSONContent) {
        parseMovieObject(JSONContent);
    }

    MovieObject(JSONObject movieObject) {
        parseMovieObject(movieObject);
    }

    void parseMovieObject(String JSONContent) {
        try{
            parseMovieObject(new JSONObject(JSONContent));
            content = JSONContent;
        } catch (JSONException e) {
            Log.e(MovieObjectUtils.LOG_TAG,"Exception : " + e);
            //Put Default Values
            movie_poster = "";
            id = -1;
            title = "";
            release_date = "";
            plot_synopsis = "";
            vote_average = 0;
            content = "";
        }
    }

    void parseMovieObject(JSONObject movie) {

        final String KEY_POSTER_PATH = "poster_path";
        final String KEY_ID = "id";
        final String KEY_TITLE = "original_title";
        final String KEY_RELEASE_DATE = "release_date";
        final String KEY_PLOT_SYNOPSYS = "overview";
        final String KEY_VOTE_AVERAGE = "vote_average";

        try {
            movie_poster = movie.getString(KEY_POSTER_PATH);
            id = movie.getInt(KEY_ID);
            title = movie.getString(KEY_TITLE);
            release_date = movie.getString(KEY_RELEASE_DATE);
            plot_synopsis = movie.getString(KEY_PLOT_SYNOPSYS);
            vote_average = movie.getDouble(KEY_VOTE_AVERAGE);
            content = movie.toString();
            Log.d(MovieObjectUtils.LOG_TAG,"Movie details loaded : " + title);

        } catch (JSONException e) {
            Log.e(MovieObjectUtils.LOG_TAG,"Exception while loading movie details!",e);
            //Put Default Values
            movie_poster = "";
            id = -1;
            title = "";
            release_date = "";
            plot_synopsis = "";
            vote_average = 0;
            content = "";
        }
    }

}
