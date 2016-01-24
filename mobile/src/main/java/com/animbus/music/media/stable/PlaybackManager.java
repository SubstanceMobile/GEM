package com.animbus.music.media.stable;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.animbus.music.media.objects.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.media.AudioManager.OnAudioFocusChangeListener;
import static android.media.MediaPlayer.OnCompletionListener;
import static android.media.MediaPlayer.OnErrorListener;
import static android.media.MediaPlayer.OnPreparedListener;
import static android.media.MediaPlayer.OnSeekCompleteListener;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_FAST_FORWARDING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_NONE;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_REWINDING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_STOPPED;


/**
 * WARNING: This class is extremely experimental. This file was created on 7/20/2015 by Adrian.
 */
public class PlaybackManager implements OnAudioFocusChangeListener, OnPreparedListener, OnErrorListener, OnCompletionListener, OnSeekCompleteListener {
    /**
     * The volume we set the media player to when GEM loses audio focus, but is allowed to reduce the volume instead of stopping playback.
     */
    public static final float VOLUME_DUCK = 0.2f;
    /**
     * The volume we set the media player when GEM has audio focus.
     */
    public static final float VOLUME_NORMAL = 1.0f;
    /**
     * This is the value you would use for tis situation:
     * The user presses "Previous". if the current position in the song is greater then this value,
     * pressing "Previous" will restart the song, if not it will play the previous song
     */
    public static final int MAX_DURATION_FOR_REPEAT = 3000;
    private final static PlaybackManager instance = new PlaybackManager();
    /**
     * This clsss's log tag
     */
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
    public Song mSong;
    private MediaService mService;
    /**
     * Type of audio focus GEM currently has:
     */
    private int mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
    /**
     * Underlying Media Player
     */
    private MediaPlayer mMediaPlayer;
    /**
     * AudioManager used to change volume and manage audio focus
     */
    private AudioManager mAudioManager;
    private boolean mPlayOnFocusGain;
    /**
     * The ID of the current song
     */
    private volatile long mCurrentMediaId;
    public Context mContext;
    private volatile int mCurrentPosition;

    public ArrayList<OnChangedListener> listeners = new ArrayList<>();
    public MediaNotification mNotification;
    private MediaService ervice;

    ///////////////////////////////////////////////////////////////////////////
    // Ways to get the class...
    ///////////////////////////////////////////////////////////////////////////

    private PlaybackManager() {

    }

    public static PlaybackManager get() {
        return instance;
    }

    public static PlaybackManager from(MediaService service) {
        return PlaybackManager.get().setContext(service).setService(service);
    }

    public PlaybackManager setContext(Context context) {
        this.mContext = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return this;
    }

    public PlaybackManager setService(MediaService service) {
        mService = service;
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Controls
    ///////////////////////////////////////////////////////////////////////////

    /**
     * This is the play method that plays music from an item
     *
     * @param item this is for the player to recognise what song to play
     */
    public void play(Song item) {
        mPlayOnFocusGain = true;
        tryToGetAudioFocus();
        long mediaId = item.getSongID();
        boolean mediaHasChanged = !(mediaId == mCurrentMediaId);
        if (mediaHasChanged) {
            mCurrentPosition = 0;
            mCurrentMediaId = mediaId;
        }

        if (mService.getState() == STATE_PAUSED && !mediaHasChanged && mMediaPlayer != null) {
            configMediaPlayerState();
        } else {
            mService.setState(STATE_STOPPED);
            relaxResources(false); // release everything except MediaPlayer

            try {
                createMediaPlayerIfNeeded();

                mService.setState(STATE_PLAYING);

                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(mContext, item.getSongURI());

                // Starts preparing the media player in the background. When
                // it's done, it will call our OnPreparedListener (that is,
                // the onPrepared() method on this class, since we set the
                // listener to 'this'). Until the media player is prepared,
                // we *cannot* call start() on it!
                mMediaPlayer.prepareAsync();

                mSong = item;

                for (OnChangedListener l : listeners) {
                    l.onSongChanged(item);
                }

                mService.setAsForeground();
            } catch (IOException ex) {
                Log.e(TAG, "Exception playing song", ex);
            }
        }
    }


    /**
     * A variaton of play that uses an item extracted from a list
     */
    public void play(List<Song> list, int songPos) {
        ArrayList<Song> data = new ArrayList<>(list);
        play(data.get(songPos));
        QueueManager.get().setQueue(data);
        QueueManager.get().setCurrentSongPos(songPos);
    }

    public void playQueueItem(int pos) {
        play(QueueManager.get().getCurrentQueueAsSong().get(pos));
        QueueManager.get().setCurrentSongPos(pos);
    }

    /**
     * Plays the next song in the queue
     */
    public void playNext() {
        play(QueueManager.get().getCurrentQueueAsSong(), QueueManager.get().updateNextSongPos());
    }

    /**
     * Plays the previous song in the queue
     */
    public void playPrev(boolean processSongPos) {
        if (processSongPos) {
            if (mMediaPlayer.getCurrentPosition() > MAX_DURATION_FOR_REPEAT) {
                play(QueueManager.get().getCurrentQueueAsSong(), QueueManager.get().getCurrentSongPos());
                mCurrentPosition = 0;
            } else {
                play(QueueManager.get().getCurrentQueueAsSong(), QueueManager.get().updatePrevSongPos());
            }
        } else {
            play(QueueManager.get().getCurrentQueueAsSong(), QueueManager.get().updatePrevSongPos());
        }
    }

    /**
     * Pauses the playback
     */
    public void pause() {
        if (mService.getState() == STATE_PLAYING) {
            // Pause media player and cancel the 'foreground service' state.
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentPosition = mMediaPlayer.getCurrentPosition();
            }
            // while paused, retain the MediaPlayer but give up audio focus
            relaxResources(false);
            giveUpAudioFocus();
            mService.removeForeground(false);
        }
        mService.setState(STATE_PAUSED);
    }

    /**
     * Resumes playback if it is paused
     */
    public void resume() {
        if (mService.getState() == STATE_PAUSED && mMediaPlayer != null) {
            mMediaPlayer.start();
            mService.setState(STATE_PLAYING);
            tryToGetAudioFocus();
            mService.setAsForeground();
            Log.d(TAG, "Resuming");
        } else {
            Log.d(TAG, "Not paused or MediaPlayer is null. Player is null: " + (mMediaPlayer == null));
        }
    }

    /**
     * This is what is called to stop playback
     */
    public void stop() {
        mService.setState(STATE_STOPPED);
        mCurrentPosition = getCurrentPosInSong();
        // Give up Audio focus
        giveUpAudioFocus();
        // Relax all resources
        relaxResources(true);
        //Stop the service
        mService.removeForeground(true);
        mService.stopService();
    }

    public void seekTo(int position) {
        Log.d(TAG, "seekTo called with " + position);
        if (mMediaPlayer == null) {
            // If we do not have a current media player, simply update the current position
            mCurrentPosition = position;
        } else {
            if (getCurrentPosInSong() > position) {
                //If the old position is greater then the new one, then you are rewinding
                mService.setState(STATE_REWINDING);
            } else {
                //If the old one is less then the new one, then you are fast forwarding
                mService.setState(STATE_FAST_FORWARDING);
            }
            mCurrentPosition = position;
            mMediaPlayer.seekTo(position);
        }
    }

    public void setVolume(int mediaVolume) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mediaVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    public void setRepeat(boolean repeat) {
        mMediaPlayer.setLooping(repeat);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interfaces
    ///////////////////////////////////////////////////////////////////////////

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
            if (mService.getState() == STATE_PLAYING && !canDuck) {
                // If we don't have audio focus and can't duck, we save the information that
                // we were playing, so that we can resume playback once we get the focus back.
                //todo setting
                mPlayOnFocusGain = true;
            }
        } else {
            Log.e(TAG, "onAudioFocusChange: Ignoring unsupported focusChange: " + focusChange);
        }
        configMediaPlayerState();
    }

    /**
     * Called when MediaPlayer has completed a seek
     *
     * @see android.media.MediaPlayer.OnSeekCompleteListener
     */
    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "onSeekComplete from MediaPlayer:" + mp.getCurrentPosition());
        mCurrentPosition = mp.getCurrentPosition();
        if (mService.getState() == STATE_REWINDING || mService.getState() == STATE_FAST_FORWARDING) {
            mMediaPlayer.start();
            mService.setState(STATE_PLAYING);
        }
    }

    /**
     * Called when media player is done playing current song.
     *
     * @see android.media.MediaPlayer.OnCompletionListener
     */
    @Override
    public void onCompletion(MediaPlayer player) {
        Log.d(TAG, "onCompletion from MediaPlayer");
        if (!player.isLooping()) {
            // The media player finished playing the current song, so we go ahead and start the next.
            play(QueueManager.get().getCurrentQueueAsSong(), QueueManager.get().updateNextSongPos());
        }
    }

    /**
     * Called when media player is done preparing.
     *
     * @see android.media.MediaPlayer.OnPreparedListener
     */
    @Override
    public void onPrepared(MediaPlayer player) {
        Log.d(TAG, "onPrepared from MediaPlayer");
        // The media player is done preparing. That means we can start playing if we
        // have audio focus.
        configMediaPlayerState();
    }

    /**
     * Called when there's an error playing media. When this happens, the media
     * player goes to the Error state. We warn the user about the error and
     * reset the media player.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "Media player error: what=" + what + ", extra=" + extra);
        return true; // true indicates we handled the error
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
    ///////////////////////////////////////////////////////////////////////////
    // Change Listener
    ///////////////////////////////////////////////////////////////////////////

    public interface OnChangedListener {
        void onSongChanged(Song song);

        void onPlaybackStateChanged(PlaybackStateCompat state);
    }

    public void registerListener(OnChangedListener l) {
        listeners.add(l);
    }

    public void unregisterListener(OnChangedListener l) {
        listeners.remove(l);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Meat and potatoes of the code
    ///////////////////////////////////////////////////////////////////////////

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
            if (mService.getState() == STATE_PLAYING) {
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
                        mService.setState(STATE_PLAYING);
                    } else {
                        mMediaPlayer.seekTo(mCurrentPosition);
                    }
                }
                mPlayOnFocusGain = false;
            }
        }
    }

    /**
     * Makes sure the media player exists and has been reset. This will create
     * the media player if needed, or reset the existing media player if one
     * already exists.
     */
    private void createMediaPlayerIfNeeded() {
        Log.d(TAG, "createMediaPlayerIfNeeded. Needed: " + (mMediaPlayer == null));
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            // Make sure the media player will acquire a wake-lock while
            // playing. If we don't do that, the CPU might go to sleep while the
            // song is playing, causing playback to stop.
            mMediaPlayer.setWakeMode(mContext,
                    PowerManager.PARTIAL_WAKE_LOCK);

            // we want the media player to notify us when it's ready preparing,
            // and when it's done playing:
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
        } else {
            mMediaPlayer.reset();
        }
    }

    /**
     * Releases resources used by the service for playback. This includes the
     * "foreground service" status, the wake locks and possibly the MediaPlayer.
     *
     * @param releaseMediaPlayer Indicates whether the Media Player should also
     *                           be released or not
     */
    private void relaxResources(boolean releaseMediaPlayer) {
        Log.d(TAG, "RelaxResources. ReleaseMediaPlayer=" + releaseMediaPlayer);

        mService.removeForeground(false);

        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////

    /**
     * When called, this tells the app wheather the app is playing music
     */
    public boolean isPlaying() {
        return mPlayOnFocusGain || (mMediaPlayer != null && mMediaPlayer.isPlaying());
    }

    public int getCurrentPosInSong() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : mCurrentPosition;
    }

    public Song getCurrentSong() {
        return mSong;
    }

    public boolean isActive() {
        return mService != null && mService.getSession() != null && !(mService.getState() == STATE_STOPPED || mService.getState() == STATE_NONE) && mService.getSession().isActive();
    }

    public MediaService getService() {
        return mService;
    }

    public boolean isLooping() {
        return mMediaPlayer.isLooping();
    }

    public boolean isInitiated() {
        return mService != null;
    }


}