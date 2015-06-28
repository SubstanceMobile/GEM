package com.animbus.music.media;

//Created by Adrian on 6/24/2015.

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.util.Log;

import com.animbus.music.NowPlayingClassic;
import com.animbus.music.R;
import com.animbus.music.activities.NowPlaying;
import com.animbus.music.data.SettingsManager;

import java.util.Collections;
import java.util.List;

/**
 * This class replaces the other one for multiple reasons:
 * <ul>Use of proper APIs </ul>
 * <ul>No more custom code </ul>
 * <ul>*MAY* remove the need for the media controller class </ul>
 * <ul>Notification </ul>
 * <ul>Proper music behavior </ul>
 * <ul>And more improvements </ul>
 */
public class MusicService extends Service implements PlaybackManager.Callback {
    /**
     * The action of the incoming Intent indicating that it contains a command
     * to be executed (see {@link #onStartCommand})
     */
    public static final String ACTION_CMD = "com.example.android.mediabrowserservice.ACTION_CMD";
    /**
     * The key in the extras of the incoming Intent indicating the command that
     * should be executed (see {@link #onStartCommand})
     */
    public static final String CMD_NAME = "CMD_NAME";
    /**
     * A value of a CMD_NAME key in the extras of the incoming Intent that
     * indicates that the music playback should be paused (see {@link #onStartCommand})
     */
    public static final String CMD_PAUSE = "CMD_PAUSE";
    /**
     * The log tag
     */
    private static final String TAG = "Music Service:";
    /**
     * Delay stopSelf by using a handler.
     */
    private static final int STOP_DELAY = 30000;

    /**
     * The media session (see {@link android.media.session.MediaSession})
     */
    private MediaSession mSession;
    /**
     * The queue (see {@link com.animbus.music.data.dataModels.Song})
     */
    private List<MediaSession.QueueItem> mPlayingQueue;
    /**
     * This is the position of the playing song in the queue
     */
    private int mCurrentIndexOnQueue;
    /**
     * Controls the media notification
     */
    private MediaNotificationManager mMediaNotificationManager;
    // Indicates whether the service was started.
    private boolean mServiceStarted;
    private DelayedStopHandler mDelayedStopHandler = new DelayedStopHandler(this);
    private PlaybackManager mPlayback;

    private boolean doRepeat;
    private boolean doShuffle;

    /**
     * (non-Javadoc)
     *
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        mPlayingQueue = Collections.emptyList();

        // Start a new MediaSession
        mSession = new MediaSession(this, "MusicService");
        setSessionToken(mSession.getSessionToken());
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mPlayback = new PlaybackManager(this, this);
        mPlayback.setState(PlaybackState.STATE_NONE);
        mPlayback.setCallback(this);

        Context context = getApplicationContext();
        Intent intent;
        if (new SettingsManager(context).getBooleanSetting(SettingsManager.KEY_USE_NEW_NOW_PLAYING, false)) {
            intent = new Intent(context, NowPlaying.class);
        } else {
            intent = new Intent(context, NowPlayingClassic.class);
        }
        PendingIntent pi = PendingIntent.getActivity(context, 99 /*request code*/,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mSession.setSessionActivity(pi);

        updatePlaybackState(null);

        mMediaNotificationManager = new MediaNotificationManager(this);
    }

    /**
     * (non-Javadoc)
     *
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        if (startIntent != null) {
            String action = startIntent.getAction();
            String command = startIntent.getStringExtra(CMD_NAME);
            if (ACTION_CMD.equals(action)) {
                if (CMD_PAUSE.equals(command)) {
                    if (mPlayback != null && mPlayback.isPlaying()) {
                        handlePauseRequest();
                    }
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        //Will release resources
        handleStopRequest(null);
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        //Release media session and clear up resources
        mSession.release();
    }

    private final class MediaSessionCallback extends MediaSession.Callback {

        @Override
        public void onPlay() {
            Log.d(TAG, "onPlay");

            if (mPlayingQueue != null && !mPlayingQueue.isEmpty()) {
                mSession.setQueue(mPlayingQueue);
                mSession.setQueueTitle(getString(R.string.title_queue));
            } else {
                Log.e(TAG, "The queue is empty");
            }

        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onSkipToNext() {
            if (doRepeat) {
                handlePlayRequest(getCurrentSong());
            } else {
                handlePlayRequest(getNextSong());
            }
        }

        @Override
        public void onSkipToPrevious() {

        }

        @Override
        public void onSkipToQueueItem(long id) {

        }

        @Override
        public void onSeekTo(long pos) {

        }

        public void handlePlayRequest() {}

        public void handlePlayRequest(List<MediaSession.QueueItem> list, int pos) {}

        public void handlePlayRequest(MediaSession.QueueItem song)
    }
}
