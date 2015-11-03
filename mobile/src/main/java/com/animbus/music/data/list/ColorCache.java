package com.animbus.music.data.list;

import com.animbus.music.media.objects.Album;

import java.util.HashMap;

/**
 * Created by Adrian on 11/3/2015.
 */
public class ColorCache {
    private static HashMap<Album, int[]> main = new HashMap<>();
    private static HashMap<Album, int[]> accent = new HashMap<>();

    public static HashMap<Album, int[]> getMain() {
        return main;
    }

    public static HashMap<Album, int[]> getAccent() {
        return accent;
    }

    private ColorCache() {
    }
}
