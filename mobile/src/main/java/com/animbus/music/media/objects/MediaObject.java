/*
 * Copyright (C) 2016 Substance Mobile
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
 */

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
