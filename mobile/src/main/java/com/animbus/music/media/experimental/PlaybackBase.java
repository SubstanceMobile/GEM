package com.animbus.music.media.experimental;

import android.net.Uri;

import com.animbus.music.media.objects.Song;

import java.util.List;

/**
 * Created by Adrian on 11/14/2015.
 */
interface PlaybackBase {
    void init(MediaService service);
    void play(Song song);
    void play(List<Song> songs, int startPos);
    void play(Uri uri);
    void resume();
    void pause();
    void next();
    void prev();
    void stop();
    void repeat(boolean repeating);
    void seek(long time);
    boolean isPlaying();
    boolean isRepeating();
    boolean isInitialized();
}
