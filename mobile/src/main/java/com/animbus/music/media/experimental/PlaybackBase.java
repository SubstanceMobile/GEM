package com.animbus.music.media.experimental;

import com.animbus.music.media.objects.Song;

import java.util.List;

/**
 * Created by Adrian on 11/14/2015.
 */
public interface PlaybackBase {
    void play(Song song);
    void play(List<Song> songs, int startPos);
    void resume();
    void pause();
    void next();
    void prev();
    void repeat(boolean repeating);
    void shuffle(boolean shuffling);
}
