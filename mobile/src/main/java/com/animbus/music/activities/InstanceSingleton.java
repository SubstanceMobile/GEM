package com.animbus.music.activities;

import android.content.Context;

/**
 * Created by Adrian on 7/15/2015.
 */
public class InstanceSingleton {
    private static InstanceSingleton instance = new InstanceSingleton();

    public static InstanceSingleton getInstance() {
        return instance;
    }

    private InstanceSingleton() {
    }


    public Context fragmentAlbumsCxt;
    public Context fragmentSongsCxt;
    public MyLibrary fragmentSongsMyLib;
    public Context fragmentPlaylistsCxt;
    public Context fragmentArtistsCxt;
}
