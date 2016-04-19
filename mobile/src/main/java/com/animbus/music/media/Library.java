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
import android.net.Uri;
import android.support.annotation.Nullable;

import com.animbus.music.R;
import com.animbus.music.media.objects.Album;
import com.animbus.music.media.objects.Artist;
import com.animbus.music.media.objects.Playlist;
import com.animbus.music.media.objects.Song;
import com.animbus.music.tasks.AlbumsTask;
import com.animbus.music.tasks.ArtistsTask;
import com.animbus.music.tasks.Loader.TaskListener;
import com.animbus.music.tasks.PlaylistsTask;
import com.animbus.music.tasks.SongsTask;
import com.animbus.music.ui.activity.search.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Library {
    public static volatile Context context;
    private static volatile List<Song> mSongs = new ArrayList<>();
    private static volatile List<Album> mAlbums = new ArrayList<>();
    private static volatile List<Playlist> mPlaylists = new ArrayList<>();
    private static volatile List<Artist> mArtists = new ArrayList<>();

    private static volatile SongsTask mSongsTask;
    private static volatile AlbumsTask mAlbumsTask;
    private static volatile PlaylistsTask mPlaylistsTask;
    private static volatile ArtistsTask mArtistsTask;

    public Library(Context context) {
        Library.context = context.getApplicationContext();

        //Creates tasks
        mSongsTask = new SongsTask(context);
        mAlbumsTask = new AlbumsTask(context);
        mPlaylistsTask = new PlaylistsTask(context);
        mArtistsTask = new ArtistsTask(context);


        //Adds all non-UI listeners to tasks
        mSongsTask.addListener(new TaskListener<Song>() {
            @Override
            public void onOneLoaded(Song item, int pos) {
                updateLinks();
            }

            @Override
            public void onCompleted(List<Song> result) {
                mSongs = result;
            }
        });
        mAlbumsTask.addListener(new TaskListener<Album>() {
            @Override
            public void onOneLoaded(Album item, int pos) {
                updateLinks();
            }

            @Override
            public void onCompleted(List<Album> result) {
                mAlbums = result;
            }
        });
        mPlaylistsTask.addListener(new TaskListener<Playlist>() {
            @Override
            public void onOneLoaded(Playlist item, int pos) {
                updateLinks();
            }

            @Override
            public void onCompleted(List<Playlist> result) {
                mPlaylists = result;
            }
        });
        mArtistsTask.addListener(new TaskListener<Artist>() {
            @Override
            public void onOneLoaded(Artist item, int pos) {
                updateLinks();
            }

            @Override
            public void onCompleted(List<Artist> result) {
                mArtists = result;
            }
        });
    }

    public static void setContext(Context cxt) {
        context = cxt;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Builds the media library
    ///////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("all")
    public static void build() {
        if (LibraryLegacy.use()) {
            LibraryLegacy.build(new LibraryLegacy.Data() {
                @Override
                public void done(List<Song> songs, List<Album> albums, List<Playlist> playlists) {
                    mSongs = songs;
                    mAlbums = albums;
                    mPlaylists = playlists;
                    mArtists = new ArrayList<>();
                }
            }, context);
        } else {
            mSongsTask.run();
            mAlbumsTask.run();
            mPlaylistsTask.run();
            mArtistsTask.run();
        }
    }

    private static void updateLinks() {
        //TODO
    }

    ///////////////////////////////////////////////////////////////////////////
    // Update Listener from MediaStore
    ///////////////////////////////////////////////////////////////////////////

    public static void registerMediaStoreListeners() {
        mSongsTask.registerMediaStoreListener();
        mAlbumsTask.registerMediaStoreListener();
        mPlaylistsTask.registerMediaStoreListener();
        mArtistsTask.registerMediaStoreListener();
    }

    public static void unregisterMediaStoreListeners() {
        mSongsTask.unregisterMediaStoreListener();
        mAlbumsTask.unregisterMediaStoreListener();
        mPlaylistsTask.unregisterMediaStoreListener();
        mArtistsTask.unregisterMediaStoreListener();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods for adding listeners to tasks
    ///////////////////////////////////////////////////////////////////////////

    public static void registerSongListener(TaskListener<Song> songListener) {
        mSongsTask.addListener(songListener);
    }

    public static void registerAlbumListener(TaskListener<Album> albumListener) {
        mAlbumsTask.addListener(albumListener);
    }

    public static void registerPlaylistListener(TaskListener<Playlist> playlistListener) {
        mPlaylistsTask.addListener(playlistListener);
    }

    public static void registerArtistListener(TaskListener<Artist> artistListener) {
        mArtistsTask.addListener(artistListener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playlist Management
    ///////////////////////////////////////////////////////////////////////////

    //TODO

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
        for (Song song : getSongs()) if (song.getID() == id) return song;
        return null;
    }

    @Nullable
    public static Song findSongByUri(Uri uri) {
        for (Song song : getSongs()) if (song.getUri() == uri) return song;
        return null;
    }

    @Nullable
    public static Album findAlbumById(long id) {
        for (Album album : getAlbums()) if (album.getID() == id) return album;
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
            if (a.getTitle().toLowerCase().contains(query.toLowerCase())) {
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
            if (s.getTitle().toLowerCase().contains(query.toLowerCase())) {
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
