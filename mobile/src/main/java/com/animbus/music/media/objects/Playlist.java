package com.animbus.music.media.objects;

import android.content.Context;

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

    public void setNoSongs() {
        this.songs = Collections.EMPTY_LIST;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void addSong(Song s) {

    }

    public void removeSong(Song s) {

    }

    public void addAll(List<Song> songs) {
        for (Song s : songs) addSong(s);
    }

    public void removeAll(List<Song> songs) {
        for (Song s : songs) removeSong(s);
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
