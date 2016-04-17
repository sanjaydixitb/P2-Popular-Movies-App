package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    public static boolean detailPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.movies_grid_container, new MainActivityFragment())
                .commit();
        }

        if(findViewById(R.id.movie_details_container) != null){
            detailPage = true;
            getSupportFragmentManager().popBackStack();

            MovieDetailsFragment detailFragment = (MovieDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.movie_details_container);
            if(detailFragment == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new MovieDetailsFragment())
                        .commit();
            }
        } else {
            detailPage = false;
        }

    }

}
