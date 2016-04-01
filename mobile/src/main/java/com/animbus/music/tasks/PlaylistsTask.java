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
import android.text.TextUtils;

import com.animbus.music.media.Library;
import com.animbus.music.media.objects.Playlist;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Adrian on 3/25/2016.
 */
public class PlaylistsTask extends Loader<Playlist> {
    public PlaylistsTask(Context context, Object... params) {
        super(context, params);
    }

    @Override
    protected Playlist buildObject(@NonNull Cursor cursor) {
        Playlist playlist = new Playlist();
        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
        playlist.setName(name);
        playlist.setType(TextUtils.equals(name.toLowerCase(), "favorites") ? 0 : 1);
        playlist.setId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)));
        return playlist;
    }

    @Override
    protected void sort(List<Playlist> data) {
        Collections.sort(data, new Comparator<Playlist>() {
            @Override
            public int compare(Playlist lhs, Playlist rhs) {
                return ((Integer) lhs.getType()).compareTo(rhs.getType());
            }
        });
    }

    @Override
    protected Uri getUri() {
        return MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected String getSortOrder() {
        return MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER;
    }

    @Override
    protected ContentObserver getObserver() {
        return new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                update(Library.getPlaylists());
            }
        };
    }
}
