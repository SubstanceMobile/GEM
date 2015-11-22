package com.animbus.music.media.experimental;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
    static volatile PlaybackBase IMPL;
    private static volatile Context mContext;
    private static volatile MediaService mService;

    private PlaybackRemote() {
    }

    public static void setUp(Context context) {
        mContext = context;
    }

    public static void init(MediaService service) {
        IMPL.init(service);
        mService = service;
    }

    public static void inject(PlaybackBase impl) {
        PlaybackRemote.IMPL = impl;
    }

    private static void startServiceIfNecessary() {
        if (!IMPL.isInitialized())
            mContext.startService(new Intent(mContext, MediaService.class));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Here go all of the controls
    ///////////////////////////////////////////////////////////////////////////

    public static void play(Uri uri) {
        startServiceIfNecessary();
        IMPL.play(uri);
    }

    public static void play(Song song) {
        startServiceIfNecessary();
        IMPL.play(song);
    }

    public static void play(List<Song> songs, int startPos) {
        startServiceIfNecessary();
        IMPL.play(songs, startPos);
    }

    public static void resume() {
        IMPL.resume();
    }

    public static void pause() {
        IMPL.pause();
    }

    public static void next() {
        IMPL.next();
    }

    public static void prev() {
        IMPL.prev();
    }

    public static void toggleRepeat() {
        IMPL.repeat(!IMPL.isRepeating());
    }

    public static void setRepeat(boolean repeating) {
        IMPL.repeat(repeating);
    }

    public static void seek(long time) {
        IMPL.seek(time);
    }

    public static void stop() {
        IMPL.stop();
    }

    ///////////////////////////////////////////////////////////////////////////
    // All of the other tidbits
    ///////////////////////////////////////////////////////////////////////////

    public static MediaSessionCompat.Token getToken() {
        return mService.mSession.getSessionToken();
    }

    public static PlaybackStateCompat getState() {
        return mService.mState;
    }

    public static MediaSessionCompat getSession() {
        return mService.mSession;
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
