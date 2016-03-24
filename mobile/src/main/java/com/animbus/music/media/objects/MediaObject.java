/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
