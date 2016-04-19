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

package com.animbus.music.tasks;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.animbus.music.media.Library;
import com.animbus.music.media.objects.Album;

public class AlbumsTask extends Loader<Album> {

    public AlbumsTask(Context context, Object... params) {
        super(context, params);
    }

    @Override
    protected Album buildObject(@NonNull Cursor cursor) {
        Album album = new Album();
        album.setID(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));
        album.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
        album.setAlbumArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
        album.setAlbumArtPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
        Log.i("AlbumsTask", "Loaded ID " + album.getID());
        return album;
    }

    @Override
    protected Uri getUri() {
        return MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected String getSortOrder() {
        return MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;
    }

    @Override
    protected ContentObserver getObserver() {
        return new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                update(Library.getAlbums());
            }
        };
    }
}
