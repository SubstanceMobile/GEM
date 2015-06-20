package com.animbus.music;

import android.content.Context;

public class MediaController {
    public static Integer REPEAT_TYPE_NONE = 0, REPEAT_TYPE_ALL = 1, REPEAT_TYPE_INDIVIDUAL = 2;
    MusicService musicService;

    public MediaController(Context context) {
       musicService = new MusicService(context);
    }

    public void startPlayback() {
        musicService.setSong();
    }

    public void addToNowPlaying(){

    }

    public void setRepeat(){}
}
