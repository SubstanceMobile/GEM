package com.animbus.music.media;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.afollestad.async.Action;
import com.afollestad.async.Async;
import com.afollestad.async.Done;
import com.afollestad.async.Result;
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
    private static volatile boolean mBuilt = false;

    private static volatile List<Song> mSongs = new ArrayList<>();
    private static volatile List<Album> mAlbums = new ArrayList<>();
    private static volatile List<Playlist> mPlaylists = new ArrayList<>();
    private static volatile List<Artist> mArtists = new ArrayList<>();

    private Library() {
    }

    public static void setContext(Context cxt) {
        context = cxt;
    }

    public static void buildAsync() {
        Action<List<Album>> albums = new Action<List<Album>>() {
            @NonNull
            @Override
            public String id() {
                return "albums";
            }

            @Nullable
            @Override
            protected List<Album> run() throws InterruptedException {
                List<Album> generated = new ArrayList<>();
                try {
                    Cursor albumsCursor = context.getContentResolver().query(
                            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            null, null, null,
                            MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);

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

                        album.setId(albumsCursor.getLong(idColumn));
                        album.setContext(context);

                        album.setAlbumTitle(albumsCursor.getString(titleColumn));
                        album.setAlbumArtistName(albumsCursor.getString(artistColumn));

                        album.setAlbumArtPath(albumsCursor.getString(albumArtColumn));

                        /*album.generateSongs();*/

                        generated.add(album);
                        updateUi(album);
                    } while (albumsCursor.moveToNext());
                    albumsCursor.close();
                } catch (IndexOutOfBoundsException e) {
                    generated = Collections.emptyList();
                }
                return generated;
            }

            protected void updateUi(Album a) {
                Library.mHandler.sendMessage(Message.obtain(Library.mHandler, Library.ALBUM_LOADED, a));
            }

            @Override
            protected void done(@Nullable List<Album> result) {
                super.done(result);
                mAlbums = result;
            }
        };
        Action<List<Song>> songs = new Action<List<Song>>() {
            @NonNull
            @Override
            public String id() {
                return "songs";
            }

            @Nullable
            @Override
            protected List<Song> run() throws InterruptedException {
                List<Song> generated = new ArrayList<>();
                try {
                    final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
                    Cursor songsCursor = context.getContentResolver().query(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            null, where, null,
                            MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

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
                        s.setSongTitle(songsCursor.getString(titleColumn));
                        s.setSongArtist(songsCursor.getString(artistColumn));
                        s.setSongID(songsCursor.getLong(idColumn));
                        s.setAlbumID(songsCursor.getLong(albumIdColumn));
                        s.setSongDuration(songsCursor.getLong(durColumn));
                        s.setTrackNumber(songsCursor.getInt(trackNumber));
                        generated.add(s);
                        updateUi(s);
                    } while (songsCursor.moveToNext());
                    songsCursor.close();
                } catch (IndexOutOfBoundsException e) {
                    generated = Collections.emptyList();
                }
                return generated;
            }

            protected void updateUi(Song s) {
                Library.mHandler.sendMessage(Message.obtain(Library.mHandler, Library.SONG_LOADED, s));
            }

            @Override
            protected void done(@Nullable List<Song> result) {
                super.done(result);
                mSongs = result;
            }
        };
        Action<List<Artist>> artists = new Action<List<Artist>>() {
            @NonNull
            @Override
            public String id() {
                return "artists";
            }

            @Nullable
            @Override
            protected List<Artist> run() throws InterruptedException {
                List<Artist> generated = new ArrayList<>();
                try {
                    //TODO: Implement this
                } catch (ArrayIndexOutOfBoundsException e) {
                    generated = Collections.emptyList();
                }
                return generated;
            }

            protected void updateUi(Artist a) {
                Library.mHandler.sendMessage(Message.obtain(Library.mHandler, Library.ARTIST_LOADED, a));
            }

            @Override
            protected void done(@Nullable List<Artist> result) {
                super.done(result);
                mArtists = result;
            }
        };
        Action<List<Playlist>> playlists = new Action<List<Playlist>>() {
            @NonNull
            @Override
            public String id() {
                return "playlists";
            }

            @Nullable
            @Override
            protected List<Playlist> run() throws InterruptedException {
                List<Playlist> generated = new ArrayList<>();
                try {
                    Cursor playlistsCursor = context.getContentResolver().query(
                            MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                            null, null, null,
                            MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);

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
                        playlist.generateSongs(context);

                        generated.add(playlist);
                        updateUi(playlist);
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

            protected void updateUi(Playlist p) {
                Library.mHandler.sendMessage(Message.obtain(Library.mHandler, Library.PLAYLIST_LOADED, p));
            }

            @Override
            protected void done(@Nullable List<Playlist> result) {
                super.done(result);
                mPlaylists = result;
            }
        };
        Async.parallel(albums, songs, artists, playlists).done(new Done() {
            @Override
            public void result(@NonNull Result result) {
                buildDataMesh();
                mBuilt = true;
            }
        });
    }

    /**
     * This takes the individual lists and combines objects.
     * So it basically links songs and albums, songs and playlists, albums and artists (which ends up linking songs and artists), etc, etc.
     */
    private static void buildDataMesh() {
        if (!mSongs.isEmpty() && !mAlbums.isEmpty()) {
            for (Album a : mAlbums) {
                for (Song s : mSongs) {
                    if (s.getAlbumID() == a.getId()) {
                        a.addSong(s);
                        s.setAlbum(a);
                    }
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Handles UI updates from the async loading going on in the background
    ///////////////////////////////////////////////////////////////////////////

    public static final int ALBUM_LOADED = 0;
    public static final int ALBUM_SONG_LOADED = 1;
    public static final int SONG_LOADED = 2;
    public static final int ARTIST_LOADED = 3;
    public static final int PLAYLIST_LOADED = 4;
    public static final int PLAYLIST_SONG_LOADED = 5;
    public static final Handler mHandler = new Handler(Looper.getMainLooper()) {

    };

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

    public static boolean isBuilt() {
        return mBuilt;
    }

    @Nullable
    public static Song findSongById(long id) {
        for (Song song : getSongs()) if (song.getSongID() == id) return song;
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
    public static Artist findArtistById(long id) {
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
