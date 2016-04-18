package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements IFavoriteCheckboxUpdateListener {

    public static boolean detailPage = false;

    public static MainActivityFragment mRecyclerFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null) {
            mRecyclerFragment = new MainActivityFragment();
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.movies_grid_container, mRecyclerFragment)
                .commit();
        }

        if(findViewById(R.id.movie_details_container) != null){
            detailPage = true;
            getSupportFragmentManager().popBackStack();

            MovieDetailsFragment detailFragment = (MovieDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.movie_details_container);
            if(detailFragment == null){
                detailFragment = new MovieDetailsFragment();
                detailFragment.setCheckBoxListener(this);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, detailFragment)
                        .commit();
            }
        } else {
            detailPage = false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings) {
            //should be in fragment but fragment's onOptionsItemSelected is not getting called sometimes
            MainActivityFragment.showOptionDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void checkBoxUpdated(MovieObject movieObject, boolean added) {
        mRecyclerFragment.updateFavoriteList(movieObject,added);
    }
}
