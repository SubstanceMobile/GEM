package com.animbus.music.media.experimental.impls;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import com.animbus.music.media.experimental.MediaService;
import com.animbus.music.media.experimental.PlaybackBase;
import com.animbus.music.media.objects.Song;

import java.util.List;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

/**
 * Created by Adrian on 11/14/2015.
 */
public class LocalPlayback implements PlaybackBase, AudioManager.OnAudioFocusChangeListener {
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private MediaService mService;

    @Override
    public void init(MediaService service) {

    }

    @Override
    public void play(Song song) {

    }

    @Override
    public void play(List<Song> songs, int startPos) {

    }

    @Override
    public void resume() {
        mMediaPlayer.start();
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public void next() {

    }

    @Override
    public void prev() {

    }

    @Override
    public void repeat(boolean repeating) {
        mMediaPlayer.setLooping(repeating);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void duck() {
        mMediaPlayer.setVolume(0.2f, 0.2f);
    }

    public void unDuck() {
        mMediaPlayer.setVolume(1.0f, 1.0f);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AUDIOFOCUS_GAIN:
                if (!isPlaying()) {
                    resume();
                }
                unDuck();
                break;
            case AUDIOFOCUS_LOSS:
                pause();
                break;
            case AUDIOFOCUS_LOSS_TRANSIENT:
                pause();
                break;
            case AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                duck();
                break;
        }
    }



}
