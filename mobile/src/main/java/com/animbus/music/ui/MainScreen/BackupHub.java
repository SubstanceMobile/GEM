package com.animbus.music.ui.MainScreen;

/**
 * Created by Adrian on 7/15/2015.
 */
public class BackupHub {
    private static final BackupHub instance = new BackupHub();

    public static BackupHub get() {
        return instance;
    }

    private BackupHub() {
    }

    public boolean activated;
    public MainScreen settingsMyLib;
}
