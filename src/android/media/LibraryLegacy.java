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

package com.animbus.music.media;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.animbus.music.media.objects.Album;
import com.animbus.music.media.objects.Playlist;
import com.animbus.music.media.objects.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This is a temporary class. It contains the old code for the library loading. Used only if the options class wants it.
 * TODO: Remove
 */
public class LibraryLegacy {
    interface Data {
        void done(List<Song> songs, List<Album> albums, List<Playlist> playlists);
    }

    public static boolean use() {
        return false;
    }

    public static void build(Data listener, Context context) {
        List<Album> albums = buildAlbums(context);
        List<Song> songs = buildSongs(context);
        List<Playlist> playlists = buildPlaylists(context);
        buildDataMesh(songs, playlists, context);
        listener.done(songs, albums, playlists);
    }

    private static List<Song> buildSongs(Context context) {
        ArrayList<Song> mSongs = new ArrayList<>();
        try {
            final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
            Cursor songsCursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, where, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

            assert songsCursor != null;
            int titleColumn = songsCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = songsCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = songsCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int durColumn = songsCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            int albumIdColumn = songsCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int trackNumber = songsCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);

            songsCursor.moveToFirst();
            do {
                Song s = new Song();
                s.setTitle(songsCursor.getString(titleColumn));
                s.setSongArtist(songsCursor.getString(artistColumn));
                s.setID(songsCursor.getLong(idColumn));
                s.setAlbumID(songsCursor.getLong(albumIdColumn));
                s.setSongDuration(songsCursor.getLong(durColumn));
                s.setTrackNumber(songsCursor.getInt(trackNumber));
                s.lock();
                mSongs.add(s);
            } while (songsCursor.moveToNext());
            songsCursor.close();
        } catch (IndexOutOfBoundsException ignored) {

        }
        return mSongs;
    }

    private static List<Album> buildAlbums(Context context) {
        ArrayList<Album> mAlbums = new ArrayList<>();
        try {
            Cursor albumsCursor = context.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    null, null, null,
                    MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);

            assert albumsCursor != null;
            int titleColumn = albumsCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ALBUM);
            int idColumn = albumsCursor.getColumnIndex
                    (MediaStore.Audio.Albums._ID);
            int artistColumn = albumsCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ARTIST);
            int albumArtColumn = albumsCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ALBUM_ART);

            albumsCursor.moveToFirst();
            do {
                Album album = new Album();

                album.setID(albumsCursor.getLong(idColumn));
                album.setContext(context);

                album.setTitle(albumsCursor.getString(titleColumn));
                album.setAlbumArtistName(albumsCursor.getString(artistColumn));

                album.setAlbumArtPath(albumsCursor.getString(albumArtColumn));

                mAlbums.add(album);
            } while (albumsCursor.moveToNext());
            albumsCursor.close();
        } catch (IndexOutOfBoundsException ignored) {

        }
        return mAlbums;
    }

    private static List<Playlist> buildPlaylists(Context context) {
        ArrayList<Playlist> mPlaylists = new ArrayList<>();
        try {
            Cursor playlistsCursor = context.getContentResolver().query(
                    MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    null, null, null,
                    MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);

            assert playlistsCursor != null;
            int titleColumn = playlistsCursor.getColumnIndex
                    (MediaStore.Audio.Playlists.NAME);
            int idColumn = playlistsCursor.getColumnIndex
                    (MediaStore.Audio.Playlists._ID);

            playlistsCursor.moveToFirst();
            do {
                Playlist playlist = new Playlist();

                String name = playlistsCursor.getString(titleColumn);
                playlist.setName(name);
                playlist.setType(TextUtils.equals(name.toLowerCase(), "favorites") ? 0 : 1);
                playlist.setId(playlistsCursor.getLong(idColumn));

                mPlaylists.add(playlist);
            } while (playlistsCursor.moveToNext());
            Collections.sort(mPlaylists, new Comparator<Playlist>() {
                @Override
                public int compare(Playlist lhs, Playlist rhs) {
                    return ((Integer) lhs.getType()).compareTo(rhs.getType());
                }
            });
            playlistsCursor.close();
        } catch (IndexOutOfBoundsException ignored) {

        }
        return mPlaylists;
    }

    private static void loadSongsForPlaylist(Playlist playlist, Context context) {
        try {
            Cursor playlistSongsCursor = context.getContentResolver().query(MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.getId()),
                    null, null, null,
                    MediaStore.Audio.Playlists.Members.PLAY_ORDER);

            assert playlistSongsCursor != null;
            int idColumn = playlistSongsCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);

            playlistSongsCursor.moveToFirst();
            do {
                playlist.getSongs().add(Library.findSongById(playlistSongsCursor.getLong(idColumn)));
            } while (playlistSongsCursor.moveToNext());
            playlistSongsCursor.close();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private static void buildDataMesh(List<Song> mSongs, List<Playlist> mPlaylists, Context c) {
        if (!mPlaylists.isEmpty() && !mSongs.isEmpty()) {
            for (Playlist p : mPlaylists) loadSongsForPlaylist(p, c);
        }
    }

}
