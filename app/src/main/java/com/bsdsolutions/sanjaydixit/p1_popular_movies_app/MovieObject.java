package com.bsdsolutions.sanjaydixit.p1_popular_movies_app;

/**
 * Created by sanjaydixit on 11/02/16.
 */
public class MovieObject {

    public String imgPath;  //For getting the thumbnail images
    public int id;          //For getting trailers and reviews later on.

    public String content;

    MovieObject(int movieId, String imagePath, String JSONcontent) {
        imgPath = imagePath;
        id = movieId;
        content = JSONcontent;
    }

}
