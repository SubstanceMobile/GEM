package com.animbus.music.media.experimental;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.animbus.music.media.objects.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 11/14/2015.
 */
public class PlaybackRemote {
    private static final String TAG = "PlaybackRemote";

    private static PlaybackRemote instance = new PlaybackRemote();

    public static PlaybackRemote get() {
        return instance;
    }

    private PlaybackRemote() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Here go all of the controls
    ///////////////////////////////////////////////////////////////////////////

    public static void play(Song song) {

    }

    public static void play(List<Song> songs, int startPos) {
        play(songs.get(startPos));
    }

    public static void resume() {

    }

    public static void pause() {

    }

    public static void next() {

    }

    public static void prev() {

    }

    public static void toggleRepeat() {

    }

    public static void setRepeat(boolean repeating) {

    }

    public static void toggleShuffle() {

    }

    public static void setShuffle() {

    }

    public static void stop() {

    }

    ///////////////////////////////////////////////////////////////////////////
    // All of the other tidbits
    ///////////////////////////////////////////////////////////////////////////

    public static MediaSessionCompat.Token getToken() {
        return null;
    }

    public static PlaybackStateCompat getState() {
        return null;
    }

    public static MediaSessionCompat getSession() {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listener
    ///////////////////////////////////////////////////////////////////////////

    static ArrayList<SongChangedListener> songListeners = new ArrayList<>();
    static ArrayList<StateChangedListener> stateListeners = new ArrayList<>();

    interface SongChangedListener {
        void onSongChanged(Song newSong);
    }

    interface StateChangedListener {
        void onStateChanged(PlaybackStateCompat newState);
    }

    public static void registerSongListener(SongChangedListener listener) {
        if (!songListeners.contains(listener)) songListeners.add(listener);
        else Log.d(TAG, "This listener is already registered");
    }

    public static void registerStateListener(StateChangedListener listener) {
        if (!stateListeners.contains(listener)) stateListeners.add(listener);
        else Log.d(TAG, "This listener is already registered");
    }

    protected static void updateSongListeners(Song newSong) {
        for (SongChangedListener l : songListeners) l.onSongChanged(newSong);
    }

    protected static void updateStateListeners(PlaybackStateCompat newState) {
        for (StateChangedListener l : stateListeners) l.onStateChanged(newState);
    }

}
