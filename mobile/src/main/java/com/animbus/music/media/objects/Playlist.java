package com.animbus.music.media.objects;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.async.Action;
import com.animbus.music.media.Library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Adrian on 7/5/2015.
 */
public class Playlist {
    Context cxt;
    List<Song> songs = new ArrayList<>();
    String name;
    long id;
    int type;

    public Playlist(){}

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

    public void generateSongs(final Context context) {
        new Action<List<Song>>(){
            @NonNull
            @Override
            public String id() {
                return "playlist_" + String.valueOf(getId());
            }

            @Nullable
            @Override
            protected List<Song> run() throws InterruptedException {
                List<Song> generated = new ArrayList<>();
                try {
                    Cursor playlistSongsCursor = context.getContentResolver().query(MediaStore.Audio.Playlists.Members.getContentUri("external", getId()),
                            null, null, null,
                            MediaStore.Audio.Playlists.Members.PLAY_ORDER);

                    int idColumn = playlistSongsCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);

                    playlistSongsCursor.moveToFirst();
                    do {
                        generated.add(Library.findSongById(playlistSongsCursor.getLong(idColumn)));
                    } while (playlistSongsCursor.moveToNext());
                } catch (IndexOutOfBoundsException e) {
                    generated = Collections.emptyList();
                }
                return generated;
            }

            @Override
            protected void done(@Nullable List<Song> result) {
                super.done(result);
                setSongs(result);
            }
        }.execute();
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
