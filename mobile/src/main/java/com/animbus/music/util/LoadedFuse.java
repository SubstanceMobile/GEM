package com.animbus.music.util;

public class LoadedFuse {
    private LoadedFuse() {
    }

    private static volatile boolean activated = false;

    public static boolean isActivated() {
        return activated;
    }

    public static void trip() {
        activated = true;
    }
}
