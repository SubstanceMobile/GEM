package com.animbus.music.media;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.animbus.music.media.objects.Album;
import com.animbus.music.media.objects.Artist;
import com.animbus.music.media.objects.Playlist;
import com.animbus.music.media.objects.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaData {
    public static final MediaData instance = new MediaData();
    public Context context;
    private List<Song> mSongs = new ArrayList<>();
    private List<Album> mAlbums = new ArrayList<>();
    private List<Playlist> mPlaylists = new ArrayList<>();
    private List<Artist> mArtists = new ArrayList<>();

    private boolean mBuilt = false;

    private MediaData() {

    }

    public static MediaData get(Context cxt) {
        instance.context = cxt;
        return instance;
    }

    public static MediaData get() {
        return instance;
    }

    public void build() {
        buildAlbums();
        buildSongs();
        buildPlaylists();
        buildArtists();

        buildDataMesh();

        mBuilt = true;
    }

    private void buildSongs() {
        try {
            final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
            Cursor songsCursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, where, null,
                    MediaStore.Audio.Media.TITLE);

            int titleColumn = songsCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = songsCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = songsCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int durColumn = songsCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            int albumIdColumn = songsCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);

            songsCursor.moveToFirst();
            do {
                Song s = new Song();
                s.setSongTitle(songsCursor.getString(titleColumn));
                s.setSongArtist(songsCursor.getString(artistColumn));
                s.setSongID(songsCursor.getLong(idColumn));
                s.setAlbumID(songsCursor.getLong(albumIdColumn));
                s.setSongDuration(songsCursor.getLong(durColumn));
                mSongs.add(s);
            } while (songsCursor.moveToNext());
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            mSongs = Collections.emptyList();
        }
    }

    private void buildAlbums() {
        try {
            Cursor albumsCursor = context.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    null, null, null,
                    MediaStore.Audio.Albums.ALBUM);

            int titleColumn = albumsCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums.ALBUM);
            int idColumn = albumsCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums._ID);
            int artistColumn = albumsCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums.ARTIST);
            int albumArtColumn = albumsCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums.ALBUM_ART);

            albumsCursor.moveToFirst();
            do {
                Album a = new Album();
                a.setAlbumTitle(albumsCursor.getString(titleColumn));
                a.setAlbumArtistName(albumsCursor.getString(artistColumn));
                a.setId(albumsCursor.getLong(idColumn));
                a.setAlbumArt(BitmapFactory.decodeFile(albumsCursor.getString(albumArtColumn)));
                a.setContext(context);
                mAlbums.add(a);
            } while (albumsCursor.moveToNext());
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            mAlbums = Collections.emptyList();
        }
    }

    private void buildPlaylists() {
        try {
            Cursor playlistsCursor = context.getContentResolver().query(
                    MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    null, null, null,
                    MediaStore.Audio.Playlists.NAME);
            int titleColumn = playlistsCursor.getColumnIndex
                    (MediaStore.Audio.Playlists.NAME);
            int idColumn = playlistsCursor.getColumnIndex
                    (MediaStore.Audio.Playlists._ID);

            playlistsCursor.moveToFirst();
            do {
                Playlist p = new Playlist();
                p.setPlaylistName(playlistsCursor.getString(titleColumn));
                p.setId(playlistsCursor.getLong(idColumn));
                mPlaylists.add(p);
            } while (playlistsCursor.moveToNext());
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            mPlaylists = Collections.emptyList();
        }
    }

    private void buildArtists() {
        //TODO:Add this
    }

    /**
     * This takes the individual lists and combines objects.
     * So it basically links songs and albums.
     */
    private void buildDataMesh() {
        if (!mSongs.isEmpty() && !mAlbums.isEmpty()) {
            for (Album a : mAlbums) {
                for (Song s : mSongs) {
                    if (s.getAlbumID() == a.getId()) {
                        a.getSongs().add(s);
                        s.setAlbum(a);
                    }
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////

    public List<Song> getSongs() {
        return mSongs;
    }

    public List<Album> getAlbums() {
        return mAlbums;
    }

    public List<Playlist> getPlaylists() {
        return mPlaylists;
    }

    public List<Artist> getArtists() {
        return mArtists;
    }

    public boolean isBuilt() {
        return mBuilt;
    }

    public Song findSongById(long id) {
        Song s = null;
        for (Song song : mSongs) {
            if (song.getSongID() == id) {
                s = song;
                break;
            }
        }
        return s;
    }

    public Album findAlbumById(long id) {
        Album a = null;
        for (Album album : mAlbums) {
            if (album.getId() == id) {
                a = album;
                break;
            }
        }
        return a;
    }

    private class AlbumListener implements Response.Listener<Bitmap>, Response.ErrorListener {
        Album a;

        public AlbumListener(Album a) {
            this.a = a;
        }

        @Override
        public void onResponse(Bitmap response) {
            a.setAlbumArt(response);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            a.setAlbumArt(a.getDefaultArt());
        }
    }
}
