package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;

/**
 * Created by sanjaydixit on 17/04/16.
 */
public class MoviesAppHelper {
    private final Activity mActivity;

    public MoviesAppHelper(Activity activity) {
        mActivity = activity;
    }

    public void playVideo(Video video) {
        if (video.site.equals(Video.SITE_YOUTUBE))
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video.key)));
    }

    public void shareTrailer(int messageTemplateResId, Video video) {
        mActivity.startActivity(Intent.createChooser(
                createShareIntent(messageTemplateResId, video.name, video.key),
                mActivity.getString(R.string.share_trailer)));
    }

    public Intent createShareIntent(int messageTemplateResId, String title, String key) {
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(mActivity)
                .setType("text/plain")
                .setText(mActivity.getString(messageTemplateResId, title, " http://www.youtube.com/watch?v=" + key));
        return builder.getIntent();
    }
}
