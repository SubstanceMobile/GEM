package com.animbus.music.media;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.animbus.music.media.objects.Album;
import com.animbus.music.media.objects.Artist;
import com.animbus.music.media.objects.Genre;
import com.animbus.music.media.objects.Playlist;
import com.animbus.music.media.objects.Song;

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
    private static volatile List<Genre> mGenres = new ArrayList<>();

    private Library() {
    }

    public static void setContext(Context cxt) {
        context = cxt;
    }

    public static void build() {
        buildAlbums();
        buildSongs();
        buildPlaylists();
        buildArtists();
        buildGenres();

        buildDataMesh();

        mBuilt = true;
    }

    private static void buildSongs() {
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
                mSongs.add(s);
            } while (songsCursor.moveToNext());
            songsCursor.close();
        } catch (IndexOutOfBoundsException e) {
            mSongs = Collections.emptyList();
        }
    }

    private static void buildAlbums() {
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

                mAlbums.add(album);
            } while (albumsCursor.moveToNext());
            albumsCursor.close();
        } catch (IndexOutOfBoundsException e) {
            mAlbums = Collections.emptyList();
        }
    }

    private static void buildPlaylists() {
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

                mPlaylists.add(playlist);
            } while (playlistsCursor.moveToNext());
            Collections.sort(mPlaylists, new Comparator<Playlist>() {
                @Override
                public int compare(Playlist lhs, Playlist rhs) {
                    return ((Integer) lhs.getType()).compareTo(rhs.getType());
                }
            });
            playlistsCursor.close();
        } catch (IndexOutOfBoundsException e) {
            mPlaylists = Collections.emptyList();
        }
    }

    private static void loadSongsForPlaylist(Playlist playlist) {
        try {
            Cursor playlistSongsCursor = context.getContentResolver().query(MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.getId()),
                    null, null, null,
                    MediaStore.Audio.Playlists.Members.PLAY_ORDER);

            int idColumn = playlistSongsCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);

            playlistSongsCursor.moveToFirst();
            do {
                playlist.getSongs().add(findSongById(playlistSongsCursor.getLong(idColumn)));
            } while (playlistSongsCursor.moveToNext());
        } catch (IndexOutOfBoundsException e) {
            playlist.setNoSongs();
        }
    }

    private static void buildArtists() {
        //TODO:Add this
    }

    private static void buildGenres() {
        try {
            Cursor genresCursor = context.getContentResolver().query(
                    MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                    null, null, null,
                    MediaStore.Audio.Genres.DEFAULT_SORT_ORDER);

            int titleColumn = genresCursor.getColumnIndex
                    (MediaStore.Audio.Genres.NAME);
            int idColumn = genresCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Genres._ID);

            genresCursor.moveToFirst();
            do {
                Genre genre = new Genre();

                genre.setName(genresCursor.getString(titleColumn));
                genre.setId(genresCursor.getLong(idColumn));

                mGenres.add(genre);
            } while (genresCursor.moveToNext());
            genresCursor.close();
        } catch (IndexOutOfBoundsException e) {
            mGenres = Collections.emptyList();
        }
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

        if (!mPlaylists.isEmpty() && !mSongs.isEmpty()) {
            for (Playlist p : mPlaylists) loadSongsForPlaylist(p);
        }
    }

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

    public static List<Genre> getGenres() {
        return mGenres;
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
    public static Playlist findPlaylistById(long id) {
        for (Playlist playlist : getPlaylists()) if (playlist.getId() == id) return playlist;
        return null;
    }

    @Nullable
    public static Artist findArtistById(long id) {
        return null;
    }

    @Nullable
    public static Genre findGenreById(long id) {
        for (Genre genre : getGenres()) if (genre.getId() == id) return genre;
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search
    ///////////////////////////////////////////////////////////////////////////

    public static List<Album> filterAlbums(String query) {
        List<Album> results = new ArrayList<>();
        for (Album a : getAlbums()) {
            if (a.getAlbumTitle().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(a)) results.add(a);
            }

            if (a.getAlbumArtistName().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(a)) results.add(a);
            }
        }
        return results;
    }

    public static List<Song> filterSongs(String query) {
        List<Song> results = new ArrayList<>();
        for (Song s : getSongs()) {
            if (s.getSongTitle().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(s)) results.add(s);
            }

            if (s.getSongArtist().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(s)) results.add(s);
            }
        }
        return results;
    }

    public static List<Playlist> filterPlaylists(String query) {
        List<Playlist> results = new ArrayList<>();
        for (Playlist p : getPlaylists()) {
            if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(p)) results.add(p);
            }
        }
        return results;
    }

}
