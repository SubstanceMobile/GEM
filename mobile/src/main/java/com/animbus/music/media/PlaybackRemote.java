package com.animbus.music.media;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.media.session.MediaControllerCompat.TransportControls;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.animbus.music.R;
import com.animbus.music.media.objects.Album;
import com.animbus.music.media.objects.Song;

import java.util.ArrayList;
import java.util.List;

import static com.animbus.music.media.MediaService.*;
import static com.animbus.music.media.MediaService.ACTION_START;

/**
 * Created by Adrian on 11/14/2015.
 */
public class PlaybackRemote {
    private static final String TAG = "PlaybackRemote";
    static volatile TransportControls remote;
    private static volatile Context mContext;
    private static volatile MediaService mService;
    static volatile List<Song> mQueue = new ArrayList<>();
    static volatile int mCurrentSongPos = 0;

    static volatile List<Song> tempSongList;
    static volatile int tempListStartPos;
    static volatile boolean tempRepeating;
    static volatile PlaybackBase tempIMPL;
    static volatile boolean tempNotifyListener;

    public static final PlaybackBase LOCAL = new LocalPlayback();

    private PlaybackRemote() {
    }

    // This is separate from init because the context needs to be set at the very beginning. init is called
    // when the service starts, and the only way to start the service is by using a context.
    // the service is started right before the media request is called, so the remote variable will be initialised and
    // can be used because it is set when init is called, when the service starts.
    public static void setUp(Context context) {
        mContext = context;
    }

    public static void init(MediaService service) {
        mService = service;
        remote = service.mSession.getController().getTransportControls();
        if (tempIMPL != null) {
            service.inject(tempIMPL);
            tempIMPL = null;
        }
    }

    public static void inject(PlaybackBase impl) {
        if (mService != null) mService.inject(impl); else tempIMPL = impl;
    }

    private static void startServiceIfNecessary() {
        if (mService == null)
            mContext.startService(new Intent(ACTION_START).setClass(mContext, MediaService.class));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Here go all of the controls
    ///////////////////////////////////////////////////////////////////////////

    public static void play(Uri uri, boolean updateListenersWithUri) {
        startServiceIfNecessary();
        remote.playFromUri(uri, null);
    }

    public static void play(Song song) {
        startServiceIfNecessary();
        remote.playFromMediaId(String.valueOf(song.getSongID()), null);
    }

    public static void play(List<Song> songs, int startPos) {
        startServiceIfNecessary();
        remote.sendCustomAction(PlaybackBase.ACTION_PLAY_FROM_LIST, null);
        tempSongList = songs;
        tempListStartPos = startPos;
    }

    public static void playQueueItem(int pos) {
        play(getQueue().get(pos));
        setCurrentSongPos(pos);
    }

    public static void resume() {
        remote.play();
    }

    public static void pause() {
        remote.pause();
    }

    public static void next() {
        remote.skipToNext();
    }

    public static void prev() {
        remote.skipToPrevious();
    }

    public static void toggleRepeat() {
        setRepeat(!mService.IMPL.isRepeating());
    }

    public static void setRepeat(boolean repeating) {
        remote.sendCustomAction(PlaybackBase.ACTION_SET_REPEAT, null);
        tempRepeating = repeating;
    }

    public static void seek(long time) {
        remote.seekTo(time);
    }

    public static void stop() {
        remote.stop();
        mService.stopSelf();
    }

    ///////////////////////////////////////////////////////////////////////////
    // All of the queue things
    ///////////////////////////////////////////////////////////////////////////

    public static List<Song> getQueue() {
        return mQueue;
    }

    public static void setQueue(List<Song> queue) {
        mQueue = queue;
    }

    public static void addToQueue(Song s) {
        mQueue.add(s);
    }

    public static void setCurrentSongPos(int currentSongPos) {
        mCurrentSongPos = currentSongPos;
    }

    public static int getCurrentSongPos() {
        return mCurrentSongPos;
    }

    public static Song getCurrentSong() {
        return getQueue().get(getCurrentSongPos());
    }

    public static int getNextSongPos() {
        int pos;
        pos = mCurrentSongPos + 1;
        if (pos > (mQueue.size() - 1)) {
            pos = 0;
        }
        setCurrentSongPos(pos);
        return pos;
    }

    public static int getPrevSongPos() {
        int pos;
        pos = mCurrentSongPos - 1;
        if (pos == 0) {
            pos = (mQueue.size() - 1);
        }
        setCurrentSongPos(pos);
        return pos;
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

    public static boolean isActive() {
        return mService.mSession.isActive();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listener
    ///////////////////////////////////////////////////////////////////////////

    static ArrayList<SongChangedListener> songListeners = new ArrayList<>();
    static ArrayList<StateChangedListener> stateListeners = new ArrayList<>();

    public interface SongChangedListener {
        void onSongChanged(Song newSong);
    }

    public interface StateChangedListener {
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

    protected static void updateSongListeners(Uri uri) {
        Song s = Library.findSongByUri(uri);
        if (s == null) {
            //Builds the song
            s = new Song();
            Album a = new Album();
            s.setSongTitle(mContext.getString(R.string.title_uri));
            s.setSongArtist(mContext.getString(R.string.artist_uri));
            a.setContext(mContext);
            s.setAlbum(a);
            updateSongListeners(s);
        }

        //Sets the queue to this one song
        List<Song> newQueue = new ArrayList<>();
        newQueue.add(s);
        setQueue(newQueue);
        setCurrentSongPos(0);
    }

    protected static void updateStateListeners(PlaybackStateCompat newState) {
        for (StateChangedListener l : stateListeners) l.onStateChanged(newState);
    }

}
