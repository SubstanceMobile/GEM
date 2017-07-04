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

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE;

public abstract class MediaObject {
    private static final MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
    public MediaMetadataCompat data;

    protected void putLong(String key, long value) {
        if (isLocked()) throw new Error("Object locked. Cannot edit");
        builder.putLong(key, value);
        data = builder.build();
    }

    protected void putString(String key, String value) {
        if (isLocked()) throw new Error("Object locked. Cannot edit");
        builder.putString(key, value);
        data = builder.build();
    }

    protected void putBitmap(String key, Bitmap value) {
        if (isLocked()) throw new Error("Object locked. Cannot edit");
        builder.putBitmap(key, value);
        data = builder.build();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Uri
    ///////////////////////////////////////////////////////////////////////////

    protected abstract Uri getBaseUri();

    public Uri getUri() {
        return ContentUris.withAppendedId(getBaseUri(), getID());
    }

    ///////////////////////////////////////////////////////////////////////////
    //Title
    ///////////////////////////////////////////////////////////////////////////

    public String getTitle() {
        return data.getString(METADATA_KEY_TITLE);
    }

    public MediaObject setTitle(String songTitle) {
        putString(METADATA_KEY_TITLE, songTitle);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // ID
    ///////////////////////////////////////////////////////////////////////////

    long id;

    public long getID() {
        return id;
    }

    public MediaObject setID(long id) {
        this.id = id;
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Handles time data
    ///////////////////////////////////////////////////////////////////////////

    boolean locked = false;
    long TIME_LOADED = 0;

    public MediaObject lock() {
        TIME_LOADED = System.currentTimeMillis();
        locked = true;
        return this;
    }

    public MediaObject unlock() {
        TIME_LOADED = 0;
        locked = false;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public long getTimeLoaded() {
        return TIME_LOADED;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Context
    ///////////////////////////////////////////////////////////////////////////

    private Context cxt;

    protected void onContextSet(Context context) {
        //Override if you want to do something when the context is set
    }

    protected boolean isContextRequired() {
        //Override to change
        return false;
    }

    public MediaObject setContext(Context cxt) {
        if (isContextRequired()) {
            this.cxt = cxt;
            onContextSet(cxt);
        } else Log.d(getClass().getSimpleName(), "Context was not requested. Ignoring");
        return this;
    }

    @Nullable
    public Context getContext() {
        return cxt.getApplicationContext();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Position in list
    ///////////////////////////////////////////////////////////////////////////

    int posInList;

    public MediaObject setPosInList(int posInList) {
        this.posInList = posInList;
        return this;
    }

    public int getPosInList() {
        return posInList;
    }
}
