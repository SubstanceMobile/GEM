package com.animbus.music.util;

public class GEMUtil {
    private GEMUtil() {
        //No new statements
    }

    public static String stringForTime(long timeMs) {
        int totalSeconds = (int) timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return  String.format("%02d:%02d", minutes, seconds);
        }
    }
}
