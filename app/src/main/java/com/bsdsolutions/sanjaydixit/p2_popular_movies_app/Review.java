package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sanjaydixit on 17/04/16.
 */
public class Review implements  Parcelable{

    public String id;
    public String author;
    public String content;
    public String url;

    protected Review(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
    }

}
