package com.animbus.music.util;

/**
 * Created by Adrian on 7/15/2015.
 */
public class LoadedFuse {
    private static final LoadedFuse instance = new LoadedFuse();

    private LoadedFuse() {
    }

    private boolean activated;

    public static boolean isActivated() {
        return instance.activated;
    }

    public static void trip() {
        instance.activated = true;
    }
}
