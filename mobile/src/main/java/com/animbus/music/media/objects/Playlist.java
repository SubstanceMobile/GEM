package com.animbus.music.media.objects;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.animbus.music.media.Library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by Adrian on 7/5/2015.
 */
public class Playlist {
    Context cxt;
    List<Song> songs = new ArrayList<>();
    String name;
    long id;
    int type;

    public Playlist() {
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Manages the title of the playlist
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //These are the songs in the
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void addSong(Song s) {
        getSongs().add(s);
    }

    public void removeSong(Song s) {
        getSongs().remove(s);
    }

    public void addAll(List<Song> songs) {
        for (Song s : songs) addSong(s);
    }

    public void removeAll(List<Song> songs) {
        for (Song s : songs) removeSong(s);
    }

    public void generateSongs(Context context) {
        new AsyncTask<Object, Void, List<Song>>() {
            @Override
            protected List<Song> doInBackground(Object... params) {
                List<Song> generated = new ArrayList<>();
                try {
                    Cursor playlistSongsCursor = ((Context) params[1]).getContentResolver().query(MediaStore.Audio.Playlists.Members.getContentUri("external", (long) params[2]),
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
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context, getId());
    }

    ///////////////////////////////////////////////////////////////////////////
    // ID
    ///////////////////////////////////////////////////////////////////////////

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Order
    ///////////////////////////////////////////////////////////////////////////

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
