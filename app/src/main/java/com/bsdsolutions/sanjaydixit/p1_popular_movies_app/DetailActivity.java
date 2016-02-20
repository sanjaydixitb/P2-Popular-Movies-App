package com.bsdsolutions.sanjaydixit.p1_popular_movies_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        int id = -1;
        id = intent.getIntExtra(MovieObjectUtils.KEY_OBJECT_ID_EXTRA,-1);
        String content = intent.getStringExtra(MovieObjectUtils.KEY_OBJECT_CONTENT_EXTRA);
        Log.d(MovieObjectUtils.LOG_TAG,"Received id : " + id);

        TextView tv = (TextView)findViewById(R.id.detail_activity_textView);

        if(id == -1) {
            tv.setText("Movie not found!");
        } else {
            tv.setText("Movie id : " + id + " \n" + content);
        }

    }
}
