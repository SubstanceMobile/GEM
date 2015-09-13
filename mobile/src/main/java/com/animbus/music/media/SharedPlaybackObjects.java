package com.animbus.music.media;

/**
 * Created by Adrian on 8/24/2015.
 */
public class SharedPlaybackObjects {
    private static SharedPlaybackObjects ourInstance = new SharedPlaybackObjects();


    public static SharedPlaybackObjects getInstance() {
        return ourInstance;
    }

    private SharedPlaybackObjects() {
    }


}
