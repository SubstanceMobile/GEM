/*
 * Copyright (C) 2016 Substance Mobile
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
 */

package com.animbus.music.media;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.animbus.music.R;
import com.animbus.music.media.objects.Album;
import com.animbus.music.media.objects.Artist;
import com.animbus.music.media.objects.Playlist;
import com.animbus.music.media.objects.Song;
import com.animbus.music.ui.activity.search.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Library {
    public static volatile Context context;
    private static volatile List<Song> mSongs = new ArrayList<>();
    private static volatile List<Album> mAlbums = new ArrayList<>();
    private static volatile List<Playlist> mPlaylists = new ArrayList<>();
    private static volatile List<Artist> mArtists = new ArrayList<>();

    private Library() {
        //So this class cannot be instantiated with a new statement
    }

    public static void setContext(Context cxt) {
        context = cxt;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Builds the media library
    ///////////////////////////////////////////////////////////////////////////

    public static void build() {
      //TODO: This
    }

    public static void buildAsync() {
        //Albums
        new AsyncTask<Void, Album, List<Album>>() {
            @Override
            protected List<Album> doInBackground(Void... params) {
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

            @Override
            protected void onProgressUpdate(Album... values) {
                super.onProgressUpdate(values);
                buildSongsForAlbums(values);
                updateAlbumListeners(values);
            }

            @Override
            protected void onPostExecute(List<Album> albums) {
                super.onPostExecute(albums);
                mAlbums = albums;
                mAlbumsBuilt = true;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //Songs
        new AsyncTask<Void, Song, List<Song>>() {
            @Override
            protected List<Song> doInBackground(Void... params) {
                List<Song> generated = new ArrayList<>();
                try {
                    Cursor songsCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
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
                        publishProgress(s);
                    } while (songsCursor.moveToNext());
                    songsCursor.close();
                } catch (IndexOutOfBoundsException e) {
                    generated = Collections.emptyList();
                }
                return generated;
            }

            @Override
            protected void onProgressUpdate(Song... values) {
                super.onProgressUpdate(values);
                updateSongListeners(values);
            }

            @Override
            protected void onPostExecute(List<Song> songs) {
                super.onPostExecute(songs);
                mSongs = songs;
                mSongsBuilt = true;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //Playlists
        new AsyncTask<Void, Playlist, List<Playlist>>() {
            @Override
            protected List<Playlist> doInBackground(Void... params) {
                List<Playlist> generated = new ArrayList<>();
                try {
                    Cursor playlistsCursor = context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);

                    assert playlistsCursor != null : "Cursor is null";
                    int titleColumn = playlistsCursor.getColumnIndex(MediaStore.Audio.Playlists.NAME);
                    int idColumn = playlistsCursor.getColumnIndex(MediaStore.Audio.Playlists._ID);

                    playlistsCursor.moveToFirst();
                    do {
                        Playlist playlist = new Playlist();

                        String name = playlistsCursor.getString(titleColumn);
                        playlist.setName(name);
                        playlist.setType(TextUtils.equals(name.toLowerCase(), "favorites") ? 0 : 1);
                        playlist.setId(playlistsCursor.getLong(idColumn));

                        generated.add(playlist);
                        publishProgress(playlist);
                    } while (playlistsCursor.moveToNext());
                    Collections.sort(mPlaylists, new Comparator<Playlist>() {
                        @Override
                        public int compare(Playlist lhs, Playlist rhs) {
                            return ((Integer) lhs.getType()).compareTo(rhs.getType());
                        }
                    });
                    playlistsCursor.close();
                } catch (IndexOutOfBoundsException e) {
                    generated = Collections.emptyList();
                }
                return generated;
            }

            @Override
            protected void onProgressUpdate(Playlist... values) {
                super.onProgressUpdate(values);
                for (Playlist p : values) buildSongsForPlaylist(p);
            }

            @Override
            protected void onPostExecute(List<Playlist> playlists) {
                super.onPostExecute(playlists);
                mPlaylists = playlists;
                updatePlaylistListeners();
                mPlaylistsBuilt = true;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //Artists
        new AsyncTask<Void, Artist, List<Artist>>() {
            @Override
            protected List<Artist> doInBackground(Void... params) {
                //TODO: Implement this
                return null;
            }

            @Override
            protected void onProgressUpdate(Artist... values) {
                super.onProgressUpdate(values);
                updateArtistListeners(values);
            }

            @Override
            protected void onPostExecute(List<Artist> artists) {
                super.onPostExecute(artists);
                mArtists = artists;
                mArtistsBuilt = true;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //Loads songs for the provided albums
    public static void buildSongsForAlbums(Album... values) {
        for (final Album album : values)
            new AsyncTask<Object, Void, List<Song>>() {
            @Override
            protected List<Song> doInBackground(Object... params) {
                List<Song> generated = new ArrayList<>();
                try {
                    Cursor albumSongsCursor = ((Context) params[0]).getContentResolver().query(
                            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            new String[]{}, MediaStore.Audio.Media.ALBUM_ID + "?=",
                            new String[]{String.valueOf((long) params[1])},
                            MediaStore.Audio.Media.TRACK);

                    assert albumSongsCursor != null : "Cursor is null";
                    int idColumn = albumSongsCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    int trackNumber = albumSongsCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);

                    albumSongsCursor.moveToFirst();
                    do {
                        Song s = new Song();


                        s.setId(albumSongsCursor.getLong(idColumn));
                        s.setTrackNumber(albumSongsCursor.getLong(trackNumber));

                        generated.add(s);
                    } while (albumSongsCursor.moveToNext());
                    albumSongsCursor.close();
                } catch (IndexOutOfBoundsException e) {
                    generated = Collections.emptyList();
                }
                return generated;
            }

            @Override
            protected void onPostExecute(List<Song> songs) {
                super.onPostExecute(songs);
                album.setSongs(songs);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context, album.getId(), album);
    }

    public static void injectSongIntoAlbum(Song... values) {
        if (mSongsBuilt) for (Song song : values) {
            for (Album a : getAlbums());
        }
    }

    public static void buildSongsForPlaylist(Playlist... values) {
        for (Playlist playlist: values) new AsyncTask<Object, Void, List<Song>>() {
            @Override
            protected List<Song> doInBackground(Object... params) {
                List<Song> generated = new ArrayList<>();
                try {
                    Cursor playlistSongsCursor = ((Context) params[0]).getContentResolver().query(MediaStore.Audio.Playlists.Members.getContentUri("external", (long) params[1]),
                            null, null, null, MediaStore.Audio.Playlists.Members.PLAY_ORDER);

                    assert playlistSongsCursor != null : "Cursor is null";
                    int idColumn = playlistSongsCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);

                    playlistSongsCursor.moveToFirst();
                    do {
                        generated.add(Library.findSongById(playlistSongsCursor.getLong(idColumn)));
                    } while (playlistSongsCursor.moveToNext());
                    playlistSongsCursor.close();
                } catch (IndexOutOfBoundsException e) {
                    generated = Collections.emptyList();
                }
                return generated;
            }

            @Override
            protected void onPostExecute(List<Song> songs) {
                super.onPostExecute(songs);
                setSongs(songs);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context, playlist.getId());
    }

    private static void registerMediaStoreListener() {

    }

    ///////////////////////////////////////////////////////////////////////////
    // The isBuilt variable
    ///////////////////////////////////////////////////////////////////////////

    private static volatile boolean
            mAlbumsBuilt = false,
            mSongsBuilt = false,
            mArtistsBuilt = false,
            mPlaylistsBuilt = false;

    public static boolean isBuilt() {
        return mAlbumsBuilt && mSongsBuilt && mArtistsBuilt && mPlaylistsBuilt;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Handles UI updates
    ///////////////////////////////////////////////////////////////////////////

    //Artist
    interface AlbumListener {
        void onChange(Album... values);
    }

    static volatile List<AlbumListener> albumListeners = new ArrayList<>();

    public static void registerAlbumListener(AlbumListener albumListener) {
        albumListeners.add(albumListener);
    }

    public static void updateAlbumListeners(Album... values) {
        for (AlbumListener l : albumListeners) l.onChange(values);
    }

    //Song
    interface SongListener {
        void onChange(Song... values);
    }

    static volatile List<SongListener> songListeners = new ArrayList<>();

    public static void registerSongListener(SongListener songListener) {
        songListeners.add(songListener);
    }

    public static void updateSongListeners(Song... values) {
        for (SongListener l : songListeners) l.onChange(values);
    }

    // Artist
    interface ArtistListener {
        void onChange(Artist... values);
    }

    static volatile List<ArtistListener> artistListeners = new ArrayList<>();

    public static void registerArtistListener(ArtistListener artistListener) {
        artistListeners.add(artistListener);
    }

    public static void updateArtistListeners(Artist... values) {
        for (ArtistListener l : artistListeners) l.onChange(values);
    }

    //Playlist
    interface PlaylistListener {
        void onChange();
    }

    static volatile List<PlaylistListener> playlistListeners = new ArrayList<>();

    public static void registerPlaylstListener(PlaylistListener playlistListener) {
        playlistListeners.add(playlistListener);
    }

    public static void updatePlaylistListeners() {
        for (PlaylistListener l : playlistListeners) l.onChange();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playlist Management
    ///////////////////////////////////////////////////////////////////////////

    //TODO: Do this thing

    ///////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////

    public static List<Song> getSongs() {
        return mSongs;
    }

    public static List<Album> getAlbums() {
        return mAlbums;
    }

    public static List<Playlist> getPlaylists() {
        return mPlaylists;
    }

    public static List<Artist> getArtists() {
        return mArtists;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Methods for finding a media object by ID
    ///////////////////////////////////////////////////////////////////////////

    @Nullable
    public static Song findSongById(long id) {
        for (Song song : getSongs()) if (song.getId() == id) return song;
        return null;
    }

    @Nullable
    public static Song findSongByUri(Uri uri) {
        for (Song song : getSongs()) if (song.getSongURI() == uri) return song;
        return null;
    }

    @Nullable
    public static Album findAlbumById(long id) {
        for (Album album : getAlbums()) if (album.getId() == id) return album;
        return null;
    }

    @Nullable
    public static Playlist findPlaylistById(long id) {
        for (Playlist playlist : getPlaylists()) if (playlist.getId() == id) return playlist;
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search
    ///////////////////////////////////////////////////////////////////////////

    public static SearchResult filterAlbums(String query) {
        List<Album> results = new ArrayList<>();
        for (Album a : getAlbums()) {
            if (a.getAlbumTitle().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(a)) results.add(a);
            }

            if (a.getAlbumArtistName().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(a)) results.add(a);
            }
        }
        return new SearchResult(context.getString(R.string.page_albums), results);
    }

    public static SearchResult filterSongs(String query) {
        List<Song> results = new ArrayList<>();
        for (Song s : getSongs()) {
            if (s.getSongTitle().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(s)) results.add(s);
            }

            if (s.getSongArtist().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(s)) results.add(s);
            }
        }
        return new SearchResult(context.getString(R.string.page_songs), results);
    }

    public static SearchResult filterPlaylists(String query) {
        List<Playlist> results = new ArrayList<>();
        for (Playlist p : getPlaylists()) {
            if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(p)) results.add(p);
            }
        }
        return new SearchResult(context.getString(R.string.page_playlists), results);
    }

    public static List<SearchResult> search(String query) {
        List<SearchResult> output = new ArrayList<>();
        filterAlbums(query).addIfNotEmpty(output);
        filterSongs(query).addIfNotEmpty(output);
        filterPlaylists(query).addIfNotEmpty(output);
        return Collections.unmodifiableList(output);
    }

}
