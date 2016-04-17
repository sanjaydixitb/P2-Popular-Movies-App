package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

/**
 * Created by sanjaydixit on 17/04/16.
 */
public enum AppendToResponseItem {

    VIDEOS("videos"),
    RELEASE_DATES("release_dates"),
    CREDITS("credits"),
    SIMILAR("similar"),
    IMAGES("images"),
    EXTERNAL_IDS("external_ids");

    private final String value;

    AppendToResponseItem(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
