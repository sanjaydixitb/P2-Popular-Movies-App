package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Date;

/**
 * Created by sanjaydixit on 11/02/16.
 */
public class MovieObject implements Parcelable {
    //To be used in recyclerView
    public String poster_path;  //For getting the thumbnail images
    public Integer id;              //For getting trailers and reviews later on.
    public String original_title, overview;
    public double vote_average;

    public Date release_date;

    //To be used in MovieDetailsFragment
    public String content;

    protected MovieObject(Parcel in) {
        id = in.readInt();
        original_title = in.readString();
        overview = in.readString();
        poster_path = in.readString();
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

    public String getPoster_path() {
        return TextUtils.join("/", new String[]{MovieObjectUtils.IMAGE_BASE_URI, MovieObjectUtils.RESOLUTION, poster_path});
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(original_title);
        parcel.writeString(overview);
        parcel.writeString(poster_path);
        parcel.writeSerializable(release_date);
        parcel.writeDouble(vote_average);
    }

}
