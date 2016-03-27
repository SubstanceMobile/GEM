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

import com.animbus.music.media.Library;
import com.animbus.music.media.objects.Song;

/**
 * Created by Adrian on 3/25/2016.
 */
public class SongsTask extends Loader<Song> {

    public SongsTask(Context context, Object... params) {
        super(context, params);
    }

    @Override
    protected Song load(@NonNull Cursor cursor) {
        Song song = new Song();
        song.setSongTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        song.setSongArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        song.setId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
        song.setAlbumID(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
        song.setSongDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
        song.setTrackNumber(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)));
        return song;
    }

    @Override
    protected Uri getUri() {
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected String getSelection() {
        return MediaStore.Audio.Media.IS_MUSIC + "=1";
    }

    @Override
    protected String getSortOrder() {
        return MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
    }

    @Override
    protected ContentObserver getObserver() {
        return new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                update(Library.getSongs());
            }
        };
    }
}
