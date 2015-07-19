package com.animbus.music.ui.MainScreen;

import android.content.Context;

/**
 * Created by Adrian on 7/15/2015.
 */
public class BackupHub {
    private static BackupHub instance = new BackupHub();

    public static BackupHub get() {
        return instance;
    }

    private BackupHub() {
    }


    public Context fragmentAlbumsCxt;
    public Context fragmentSongsCxt;
    public MainScreen fragmentSongsMyLib;
    public Context fragmentPlaylistsCxt;
    public Context fragmentArtistsCxt;
}
