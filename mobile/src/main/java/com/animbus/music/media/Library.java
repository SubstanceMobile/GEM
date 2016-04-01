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
import com.animbus.music.tasks.Loader;
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
        mSongsTask.addListener(new Loader.TaskListener<Song>() {
            @Override
            public void onOneLoaded(Song item) {
                updateLinks();
            }

            @Override
            public void onCompleted(List<Song> result) {
                mSongsBuilt = true;
                mSongs = result;
            }
        });
        mAlbumsTask.addListener(new Loader.TaskListener<Album>() {
            @Override
            public void onOneLoaded(Album item) {
                updateLinks();
            }

            @Override
            public void onCompleted(List<Album> result) {
                mAlbumsBuilt = true;
                mAlbums = result;
            }
        });
        mPlaylistsTask.addListener(new Loader.TaskListener<Playlist>() {
            @Override
            public void onOneLoaded(Playlist item) {
                updateLinks();
            }

            @Override
            public void onCompleted(List<Playlist> result) {
                mPlaylistsBuilt = true;
                mPlaylists = result;
            }
        });
        mArtistsTask.addListener(new Loader.TaskListener<Artist>() {
            @Override
            public void onOneLoaded(Artist item) {
                updateLinks();
            }

            @Override
            public void onCompleted(List<Artist> result) {
                mArtistsBuilt = true;
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

    public static void build() {
        mSongsTask.run();
        mAlbumsTask.run();
        mPlaylistsTask.run();
        mArtistsTask.run();
    }

    private static void updateLinks() {
        for (Song s : getSongs()) {
            Album a = findAlbumById(s.getAlbumID());
            if (a != null) {
                //Link 'em

            }
        }

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
    // The isBuilt variable
    ///////////////////////////////////////////////////////////////////////////

    private static volatile boolean
            mAlbumsBuilt = false,
            mSongsBuilt = false,
            mArtistsBuilt = false,
            mPlaylistsBuilt = false;

    public static boolean isBuilt() {
        return areSongsBuilt() && areAlbumsBuilt() && arePlaylistsBuilt() && areArtistsBuilt();
    }

    public static boolean areSongsBuilt() {
        return mSongsBuilt;
    }

    public static boolean areAlbumsBuilt() {
        return mAlbumsBuilt;
    }

    public static boolean arePlaylistsBuilt() {
        return mPlaylistsBuilt;
    }

    public static boolean areArtistsBuilt() {
        return mArtistsBuilt;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods for adding listeners to tasks
    ///////////////////////////////////////////////////////////////////////////

    public static void registerSongListener(Loader.TaskListener<Song> songListener) {
        mSongsTask.addListener(songListener);
    }

    public static void registerAlbumListener(Loader.TaskListener<Album> albumListener) {
        mAlbumsTask.addListener(albumListener);
    }

    public static void registerPlaylstListener(Loader.TaskListener<Playlist> playlistListener) {
        mPlaylistsTask.addListener(playlistListener);
    }

    public static void registerArtistListener(Loader.TaskListener<Artist> artistListener) {
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
