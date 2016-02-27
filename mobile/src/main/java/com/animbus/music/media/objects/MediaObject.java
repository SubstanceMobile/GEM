package com.animbus.music.media.objects;

import android.graphics.Bitmap;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;

class MediaObject {
    private static final MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
    public MediaMetadataCompat data;

    protected void putLong(String key, long value) {
        builder.putLong(key, value);
        data = builder.build();
    }

    protected void putString(String key, String value) {
        builder.putString(key, value);
        data = builder.build();
    }

    protected void putBitmap(String key, Bitmap value) {
        builder.putBitmap(key, value);
        data = builder.build();
    }

    protected void putRating(String key, RatingCompat value) {
        builder.putRating(key, value);
        data = builder.build();
    }

    protected void putText(String key, CharSequence value) {
        builder.putText(key, value);
        data = builder.build();
    }
}
