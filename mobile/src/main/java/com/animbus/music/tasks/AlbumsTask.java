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
import android.database.Cursor;
import android.provider.MediaStore;

import com.animbus.music.media.objects.Album;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumsTask extends BaseTask<Album> {

    public AlbumsTask(Context context, Object... params) {
        super(context, params);
    }

    @Override
    protected List<Album> doInBackground(Object... params) {
        List<Album> generated = new ArrayList<>();
        try {
            Cursor albumsCursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);

            assert albumsCursor != null : "Cursor is null";
            int titleColumn = albumsCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int idColumn = albumsCursor.getColumnIndex(MediaStore.Audio.Albums._ID);
            int artistColumn = albumsCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
            int albumArtColumn = albumsCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);

            albumsCursor.moveToFirst();
            do {
                Album album = new Album();

                album.setId(albumsCursor.getLong(idColumn));
                album.setContext(context);
                album.setAlbumTitle(albumsCursor.getString(titleColumn));
                album.setAlbumArtistName(albumsCursor.getString(artistColumn));
                album.setAlbumArtPath(albumsCursor.getString(albumArtColumn));

                generated.add(album);
                publishProgress(album);
            } while (albumsCursor.moveToNext());
            albumsCursor.close();
        } catch (IndexOutOfBoundsException e) {
            generated = Collections.emptyList();
        }
        return generated;
    }
}
