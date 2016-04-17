package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

import android.graphics.Movie;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sanjaydixit on 11/02/16.
 */
public class MovieObject implements Parcelable {
    //To be used in recyclerView
    public String movie_poster;  //For getting the thumbnail images
    public Integer id;              //For getting trailers and reviews later on.
    public String title, plot_synopsis;
    public double vote_average;

    public Date release_date;

    //To be used in MovieDetails
    public String content;

    protected MovieObject(Parcel in) {
        id = in.readInt();
        title = in.readString();
        plot_synopsis = in.readString();
        movie_poster = in.readString();
        release_date = (Date) in.readSerializable();
        vote_average = in.readDouble();
    }


    public static final Creator<MovieObject> CREATOR = new Creator<MovieObject>() {
        @Override
        public MovieObject createFromParcel(Parcel in) {
            return new MovieObject(in);
        }

        @Override
        public MovieObject[] newArray(int size) {
            return new MovieObject[size];
        }
    };

    public String getMovie_poster() {
        return TextUtils.join("/", new String[]{MovieObjectUtils.IMAGE_BASE_URI, MovieObjectUtils.RESOLUTION, movie_poster});
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(plot_synopsis);
        parcel.writeString(movie_poster);
        parcel.writeSerializable(release_date);
        parcel.writeDouble(vote_average);
    }

}
