package com.animbus.music.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.text.TextUtils;
import android.util.Log;

import com.animbus.music.data.dataModels.Song;

import java.io.IOException;

import static android.media.AudioManager.OnAudioFocusChangeListener;
import static android.media.MediaPlayer.OnCompletionListener;
import static android.media.MediaPlayer.OnErrorListener;
import static android.media.MediaPlayer.OnPreparedListener;

/**
 * WARNING: This class is extremely experimental.
 */
public class PlaybackManager implements OnAudioFocusChangeListener, OnPreparedListener, OnErrorListener, OnCompletionListener {
    /**
     * The volume we set the media player to when GEM loses audio focus, but is allowed to reduce the volume instead of stopping playback.
     */
    public static final float VOLUME_DUCK = 0.2f;
    /**
     * The volume we set the media player when we have audio focus.
     */
    public static final float VOLUME_NORMAL = 1.0f;
    //This clsss's log tag
    private static final String TAG = "Playback Manager:";
    /**
     * GEM doesn't have audio focus, and can't duck (play at a low volume)
     */
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    /**
     * GEM doesn't have focus, but can duck (play at a low volume)
     */
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    /**
     * GEM has full audio focus
     */
    private static final int AUDIO_FOCUSED = 2;
    private final MusicService mService;
    // Type of audio focus GEM currently has:
    private int mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private int mState;
    private boolean mPlayOnFocusGain;
    private Callback mCallback;
    private volatile boolean mAudioNoisyReceiverRegistered;
    /**The ID of the current song*/
    private volatile long mCurrentMediaId;
    private Context context;

    /**
     * The intent filter to notify that the audio will be re-routed through the speakers
     */
    private IntentFilter mAudioNoisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private BroadcastReceiver mAudioNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                Log.d(TAG, "Headphones disconnected.");
                if (isPlaying()) {
                    Intent i = new Intent(context, MusicService.class);
                    i.setAction(MusicService.ACTION_CMD);
                    i.putExtra(MusicService.CMD_NAME, MusicService.CMD_PAUSE);
                    mService.startService(i);
                }
            }
        }
    };

    public PlaybackManager(Context context,MusicService service) {
        this.mService = service;
    }


    /**
     * This is what is called to stop playback
     */
    public void stop(boolean notifyListeners) {
        mState = PlaybackState.STATE_STOPPED;
        if (notifyListeners && mCallback != null) {
            mCallback.onPlaybackStatusChanged(mState);
        }
        mCurrentPosition = getCurrentStreamPosition();
        // Give up Audio focus
        giveUpAudioFocus();
        unregisterAudioNoisyReceiver();
        // Relax all resources
        relaxResources(true);
    }

    /**
     * Get the current state
     */
    public int getState() {
        return mState;
    }

    /**
     * Sets the current state of the player
     */
    public void setState(int state) {
        this.mState = state;
    }

    /**
     * When called, this tells the app wheather the app is playing music
     */
    public boolean isPlaying() {
        return mPlayOnFocusGain || (mMediaPlayer != null && mMediaPlayer.isPlaying());
    }

    /**
     * This is the play method that plays music from an item
     *
     * @param item this is for the player to recognise what song to play
     */
    public void play(Song item) {
        mPlayOnFocusGain = true;
        tryToGetAudioFocus();
        registerAudioNoisyReceiver();
        long mediaId = item.getSongID();
        boolean mediaHasChanged = !(mediaId == mCurrentMediaId);
        if (mediaHasChanged) {
            mCurrentPosition = 0;
            mCurrentMediaId = mediaId;
        }

        if (mState == PlaybackState.STATE_PAUSED && !mediaHasChanged && mMediaPlayer != null) {
            configMediaPlayerState();
        } else {
            mState = PlaybackState.STATE_STOPPED;
            relaxResources(false); // release everything except MediaPlayer

            try {
                createMediaPlayerIfNeeded();

                mState = PlaybackState.STATE_BUFFERING;

                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource();

                // Starts preparing the media player in the background. When
                // it's done, it will call our OnPreparedListener (that is,
                // the onPrepared() method on this class, since we set the
                // listener to 'this'). Until the media player is prepared,
                // we *cannot* call start() on it!
                mMediaPlayer.prepareAsync();

                if (mCallback != null) {
                    mCallback.onPlaybackStatusChanged(mState);
                }

            } catch (IOException ex) {
                Log.e(TAG, "Exception playing song", ex);
                if (mCallback != null) {
                    mCallback.onError(ex.getMessage());
                }
            }
        }
    }


    /**
     * The callback that will tell everything listening that the media was changed
     */
    interface Callback {
        /**
         * On current music completed.
         */
        void onCompletion();

        /**
         * on Playback status changed
         * Implementations can use this callback to update
         * playback state on the media sessions.
         */
        void onPlaybackStatusChanged(int state);

        /**
         * @param error to be added to the PlaybackState
         */
        void onError(String error);

    }
}
