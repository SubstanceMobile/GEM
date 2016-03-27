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

import com.animbus.music.media.objects.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Adrian on 3/25/2016.
 */
public class SongsTask extends Loader<Song> {

    public SongsTask(Context context, Object... params) {
        super(context, params);
    }

    @Override
    protected List<Song> doLoad(Object... params) {
        List<Song> generated = new ArrayList<>();
        try {
            Cursor songsCursor = getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Audio.Media.IS_MUSIC + "=1", null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);


            assert songsCursor != null : "Cursor is null";
            int titleColumn = songsCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = songsCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int albumIdColumn = songsCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int artistColumn = songsCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int durColumn = songsCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int trackNumber = songsCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);

            songsCursor.moveToFirst();
            do {
                Song s = new Song();

                s.setSongTitle(songsCursor.getString(titleColumn));
                s.setSongArtist(songsCursor.getString(artistColumn));
                s.setId(songsCursor.getLong(idColumn));
                s.setAlbumID(songsCursor.getLong(albumIdColumn));
                s.setSongDuration(songsCursor.getLong(durColumn));
                s.setTrackNumber(songsCursor.getLong(trackNumber));

                generated.add(s);
                notifyOneLoaded(s);
            } while (songsCursor.moveToNext());
            songsCursor.close();
        } catch (IndexOutOfBoundsException e) {
            generated = Collections.emptyList();
        }
        return generated;
    }
}
