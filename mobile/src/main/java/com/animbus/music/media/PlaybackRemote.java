package com.animbus.music.media;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.media.session.MediaControllerCompat.TransportControls;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import com.animbus.music.R;
import com.animbus.music.media.objects.Album;
import com.animbus.music.media.objects.Song;
import com.animbus.music.media.stable.PlaybackManager;
import com.animbus.music.media.stable.QueueManager;
import com.animbus.music.media.stable.ServiceHelper;
import com.animbus.music.util.Options;

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
    static volatile Song tempSong;
    static volatile Uri tempUri;
    static volatile int tempCommand = -1;

    public static final PlaybackBase LOCAL = new LocalPlayback();

    private PlaybackRemote() {
    }

    // This is separate from init because the context needs to be set at the very beginning. init is called
    // when the service starts, and the only way to start the service is by using a context.
    // the service is started right before the media request is called, so the remote variable will be initialised and
    // can be used because it is set when init is called, when the service starts.
    public static void setUp(Context context) {
        mContext = context;
        ServiceHelper.get(context).initService();
    }

    public static void init(MediaService service) {
        mService = service;
        remote = service.mSession.getController().getTransportControls();
        if (tempIMPL != null) {
            service.inject(tempIMPL);
            tempIMPL = null;
        }


        if (tempCommand == 0) {
            play(tempUri);
        } else if (tempCommand == 1) {
            play(tempSong);
        } else if (tempCommand == 2) {
            play(tempSongList, tempListStartPos);
        }
        tempUri = null;
        tempSong = null;
        tempSongList = null;
        tempListStartPos = -1;
        tempCommand = -1;
    }

    public static void inject(PlaybackBase impl) {
        if (mService != null) mService.inject(impl); else tempIMPL = impl;
    }

    public static boolean startServiceIfNecessary() {
        if (mService == null)
            mContext.startService(new Intent(ACTION_START).setClass(mContext, MediaService.class));
        return mService == null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Here go all of the controls
    ///////////////////////////////////////////////////////////////////////////

    public static void play(Uri uri) {
        if (Options.useStableService()) {
            Toast.makeText(mContext, R.string.msg_coming_soon, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!startServiceIfNecessary()) remote.playFromUri(uri, null); else {
            tempCommand = 0;
            tempUri = uri;
        }
    }

    public static void play(Song song) {
        if (Options.useStableService()) {
            PlaybackManager.get().play(song);
            return;
        }
        if (!startServiceIfNecessary()) remote.playFromMediaId(String.valueOf(song.getId()), null); else {
            tempCommand = 1;
            tempSong = song;
        }
    }

    public static void play(List<Song> songs, int startPos) {
        if (Options.useStableService()) {
            PlaybackManager.get().play(songs, startPos);
            return;
        }
        tempSongList = songs;
        tempListStartPos = startPos;
        if (!startServiceIfNecessary()) remote.sendCustomAction(PlaybackBase.ACTION_PLAY_FROM_LIST, null); else tempCommand = 2;
    }

    public static void playQueueItem(int pos) {
        if (Options.useStableService()) {
            PlaybackManager.get().playQueueItem(pos);
            return;
        }
        play(getQueue().get(pos));
        setCurrentSongPos(pos);
    }

    public static void resume() {
        if (Options.useStableService()) {
            PlaybackManager.get().resume();
            return;
        }
        remote.play();
    }

    public static void pause() {
        if (Options.useStableService()) {
            PlaybackManager.get().pause();
            return;
        }
        remote.pause();
    }

    public static void next() {
        if (Options.useStableService()) {
            PlaybackManager.get().playNext();
            return;
        }
        remote.skipToNext();
    }

    public static void prev() {
        if (Options.useStableService()) {
            PlaybackManager.get().playPrev(true);
            return;
        }
        remote.skipToPrevious();
    }

    public static void toggleRepeat() {
        if (Options.useStableService()) {
            PlaybackManager.get().setRepeat(!PlaybackManager.get().isLooping());
            return;
        }
        setRepeat(!mService.IMPL.isRepeating());
    }

    public static void setRepeat(boolean repeating) {
        if (Options.useStableService()) {
            PlaybackManager.get().setRepeat(repeating);
            return;
        }
        remote.sendCustomAction(PlaybackBase.ACTION_SET_REPEAT, null);
        tempRepeating = repeating;
    }

    public static boolean isRepeating() {
        if (Options.useStableService()) {
            return PlaybackManager.get().isLooping();
        }
        return mService.IMPL.isRepeating();
    }

    public static void seek(long time) {
        if (Options.useStableService()) {
            PlaybackManager.get().seekTo((int)time);
            return;
        }
        remote.seekTo(time);
    }

    public static void stop() {
        if (Options.useStableService()) {
            PlaybackManager.get().stop();
            return;
        }
        remote.stop();
        killService();
    }

    public static void killService() {
        mService.stopSelf();
    }

    ///////////////////////////////////////////////////////////////////////////
    // All of the queue things
    ///////////////////////////////////////////////////////////////////////////

    public static List<Song> getQueue() {
        if (Options.useStableService()) {
            return QueueManager.get().getCurrentQueueAsSong();
        }
        return mQueue;
    }

    public static void setQueue(List<Song> queue) {
        if (Options.useStableService()) {
            QueueManager.get().setQueueAsSongList(queue);
            return;
        }
        mQueue = queue;
    }

    public static void addToQueue(Song s) {
        if (Options.useStableService()) {
            QueueManager.get().addToQueue(s);
            return;
        }
        mQueue.add(s);
    }

    public static void setCurrentSongPos(int currentSongPos) {
        if (Options.useStableService()) {
            QueueManager.get().setCurrentSongPos(currentSongPos);
            return;
        }
        mCurrentSongPos = currentSongPos;
    }

    public static int getCurrentSongPos() {
        if (Options.useStableService()) {
            return QueueManager.get().getCurrentSongPos();
        }
        return mCurrentSongPos;
    }

    public static Song getCurrentSong() {
        if (Options.useStableService()) {
            return PlaybackManager.get().getCurrentSong();
        }
        if (!getQueue().isEmpty()) return getQueue().get(getCurrentSongPos());
        return null;
    }

    public static int getNextSongPos() {
        if (Options.useStableService()) {
            return QueueManager.get().updateNextSongPos();
        }

        int pos;
        pos = mCurrentSongPos + 1;
        if (pos > (mQueue.size() - 1)) {
            pos = 0;
        }
        setCurrentSongPos(pos);
        return pos;
    }

    public static int getPrevSongPos() {
        if (Options.useStableService()) {
            return QueueManager.get().updatePrevSongPos();
        }

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
        if (Options.useStableService()) {
            return PlaybackManager.get().getService().getSession().getSessionToken();
        }

        return mService.mSession.getSessionToken();
    }

    public static PlaybackStateCompat getState() {
        if (Options.useStableService()) {
            return PlaybackManager.get().getService().getStateObj();
        }

        return mService.mState;
    }

    public static MediaSessionCompat getSession() {
        if (Options.useStableService()) {
            return PlaybackManager.get().getService().getSession();
        }

        return mService.mSession;
    }

    public static boolean isActive() {
        if (Options.useStableService()) {
            return PlaybackManager.get().isActive();
        }

        return mService != null && mService.mSession.isActive();
    }

    public static boolean isPlaying() {
        if (Options.useStableService()) {
            return PlaybackManager.get().isPlaying();
        }

        return mService.IMPL.isPlaying();
    }

    public static int getCurrentPosInSong() {
        if (Options.useStableService()) {
            return PlaybackManager.get().getCurrentPosInSong();
        }

        return mService.IMPL.getCurrentPosInSong();
    }

    //////////////////////////////////////////////////////////////////////////
    // Listener
    ///////////////////////////////////////////////////////////////////////////

    static ArrayList<SongChangedListener> songListeners = new ArrayList<>();
    static ArrayList<StateChangedListener> stateListeners = new ArrayList<>();

    static {
        PlaybackManager.get().registerListener(new PlaybackManager.OnChangedListener() {
            @Override
            public void onSongChanged(Song song) {
                updateSongListeners(song);
            }

            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                updateStateListeners(state);
            }
        });
    }

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
        if (Options.useStableService()) {
            Toast.makeText(mContext, R.string.msg_coming_soon, Toast.LENGTH_SHORT).show();
            return;
        }

        Song s = Library.findSongByUri(uri);
        if (s == null) {
            //Builds the song
            s = new Song();
            Album a = new Album();
            s.setSongTitle(mContext.getString(R.string.title_uri));
            s.setSongArtist(mContext.getString(R.string.artist_uri));
            a.setContext(mContext);

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
