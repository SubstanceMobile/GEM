package com.animbus.music.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.util.Log;

import com.animbus.music.data.dataModels.Song;

import java.io.IOException;
import java.util.List;

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
     * The volume we set the media player when GEM has audio focus.
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

    /**
     * The ID of the current song
     */
    private volatile long mCurrentMediaId;
    private Context context;
    private volatile int mCurrentPosition;

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

    public PlaybackManager(Context context, MusicService service) {
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

    //TODO:Rename this
    public int getCurrentStreamPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : mCurrentPosition;
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

                mState = PlaybackState.STATE_PLAYING;

                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(context, item.getSongURI());

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
     * A variaton of play that uses an item extracted from a list
     */
    public void play(List<Song> list, int songPos) {
        play(list.get(songPos));
    }

    /**
     * Pauses the playback
     */
    public void pause() {
        if (mState == PlaybackState.STATE_PLAYING) {
            // Pause media player and cancel the 'foreground service' state.
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentPosition = mMediaPlayer.getCurrentPosition();
            }
            // while paused, retain the MediaPlayer but give up audio focus
            relaxResources(false);
            giveUpAudioFocus();
        }
        mState = PlaybackState.STATE_PAUSED;
        if (mCallback != null) {
            mCallback.onPlaybackStatusChanged(mState);
        }
        unregisterAudioNoisyReceiver();
    }

    public void seekTo(int position) {
        Log.d(TAG, "seekTo called with " + position);
        if (mMediaPlayer == null) {
            // If we do not have a current media player, simply update the current position
            mCurrentPosition = position;
        } else {
            if (mMediaPlayer.isPlaying()) {
                if (getCurrentStreamPosition() > position) {
                    //If the old position is greater then the new one, then you are rewinding
                    setState(PlaybackState.STATE_REWINDING);
                } else {
                    //If the old one is less then the new one, then you are fast forwarding
                    setState(PlaybackState.STATE_FAST_FORWARDING);
                }
            }
            //Sets the position of the media player
            mMediaPlayer.seekTo(position);
            mCurrentPosition = position;
            if (mCallback != null) {
                mCallback.onPlaybackStatusChanged(mState);
            }
        }
    }


    /**
     * Try to get the system audio focus.
     */
    private void tryToGetAudioFocus() {
        Log.d(TAG, "tryToGetAudioFocus");
        if (mAudioFocus != AUDIO_FOCUSED) {
            int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocus = AUDIO_FOCUSED;
            }
        }
    }

    /**
     * Give up the audio focus.
     */
    private void giveUpAudioFocus() {
        Log.d(TAG, "giveUpAudioFocus");
        if (mAudioFocus == AUDIO_FOCUSED) {
            if (mAudioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
            }
        }
    }


    /**
     * Reconfigures MediaPlayer according to audio focus settings and
     * starts/restarts it. This method starts/restarts the MediaPlayer
     * respecting the current audio focus state. So if we have focus, it will
     * play normally; if we don't have focus, it will either leave the
     * MediaPlayer paused or set it to a low volume, depending on what is
     * allowed by the current focus settings. This method assumes mPlayer !=
     * null, so if you are calling it, you have to do so from a context where
     * you are sure this is the case.
     */
    private void configMediaPlayerState() {
        Log.d(TAG, "configMediaPlayerState. mAudioFocus=" + mAudioFocus);
        if (mAudioFocus == AUDIO_NO_FOCUS_NO_DUCK) {
            // If we don't have audio focus and can't duck, we have to pause,
            if (mState == PlaybackState.STATE_PLAYING) {
                pause();
            }
        } else {  // we have audio focus:
            if (mAudioFocus == AUDIO_NO_FOCUS_CAN_DUCK) {
                mMediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK); // we'll be relatively quiet
            } else {
                if (mMediaPlayer != null) {
                    mMediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL); // we can be loud again
                } // else do something for remote client.
            }
            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain) {
                if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                    Log.d(TAG, "configMediaPlayerState startMediaPlayer. seeking to " + mCurrentPosition);
                    if (mCurrentPosition == mMediaPlayer.getCurrentPosition()) {
                        mMediaPlayer.start();
                        mState = PlaybackState.STATE_PLAYING;
                    } else {
                        mMediaPlayer.seekTo(mCurrentPosition);
                        mState = PlaybackState.STATE_PLAYING;
                    }
                }
                mPlayOnFocusGain = false;
            }
        }
        if (mCallback != null) {
            mCallback.onPlaybackStatusChanged(mState);
        }
    }

    /**
     * Called by AudioManager on audio focus changes.
     * Implementation of {@link android.media.AudioManager.OnAudioFocusChangeListener}
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d(TAG, "onAudioFocusChange. focusChange=" + focusChange);
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // We have gained focus:
            mAudioFocus = AUDIO_FOCUSED;

        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            // We have lost focus. If we can duck (low playback volume), we can keep playing.
            // Otherwise, we need to pause the playback.
            boolean canDuck = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
            mAudioFocus = canDuck ? AUDIO_NO_FOCUS_CAN_DUCK : AUDIO_NO_FOCUS_NO_DUCK;

            // If we are playing, we need to reset media player by calling configMediaPlayerState
            // with mAudioFocus properly set.
            if (mState == PlaybackState.STATE_PLAYING && !canDuck) {
                // If we don't have audio focus and can't duck, we save the information that
                // we were playing, so that we can resume playback once we get the focus back.
                mPlayOnFocusGain = true;
            }
        } else {
            Log.e(TAG, "onAudioFocusChange: Ignoring unsupported focusChange: " + focusChange);
        }
        configMediaPlayerState();
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
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
