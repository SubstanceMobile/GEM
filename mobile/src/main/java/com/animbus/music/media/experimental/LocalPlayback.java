package com.animbus.music.media.experimental;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.animbus.music.R;
import com.animbus.music.media.objects.Song;
import com.animbus.music.media.stable.QueueManager;

import java.util.ArrayList;
import java.util.List;

import static android.media.AudioManager.*;
import static android.support.v4.media.session.PlaybackStateCompat.*;

/**
 * An instance of {@link PlaybackBase} that plays media locally on the device.
 */
class LocalPlayback implements PlaybackBase, AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = "LocalPlayback";

    //Actual Media Player
    private MediaPlayer mMediaPlayer;

    //AudioManager for audiofocus
    private AudioManager mAudioManager;

    //The service that this has been injected into
    private MediaService mService;

    //The uri of the currently playing song
    private Uri mCurrentUri;

    //For when audiofocus is lost and playback was paused.
    //Gives a way to be able to tell that playback was paused and thus the playback won't start on an audiofocus gain
    private boolean mWasPlaying = true;

    //Self explanatory. Indicates that GEM has audio focus in the system.
    private boolean mHasAudioFocus = false;

    ///////////////////////////////////////////////////////////////////////////
    // Audio Becoming Noisy filter
    ///////////////////////////////////////////////////////////////////////////

    BroadcastReceiver mAudioNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                if (isPlaying()) pause();
            }
        }
    };

    IntentFilter mNoisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    ///////////////////////////////////////////////////////////////////////////
    // Configure this with service
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void init(MediaService service) {
        mService = service;
        mAudioManager = (AudioManager) service.getSystemService(Context.AUDIO_SERVICE);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Controls
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void play(Uri uri) {
        requestAudioFocus();
        mWasPlaying = true;
        mService.registerReceiver(mAudioNoisyReceiver, mNoisyFilter);
        if (uri != mCurrentUri) {
            //Song is different
            mCurrentUri = uri;

            try {
                createMediaPlayerIfNeeded();
                mMediaPlayer.setDataSource(mService, uri);
                mMediaPlayer.setAudioStreamType(STREAM_MUSIC);
                mService.setState(STATE_PLAYING);

                mMediaPlayer.prepareAsync();
            } catch (Exception e) {
                Log.e(TAG, "Exception when calling play(Uri)", e);
            }
        } else {
            //Same song, restart playback
            mMediaPlayer.seekTo(0);

            //Prepares the media player and it will start it eventually
            mMediaPlayer.prepareAsync();
        }
    }

    @Override
    public void play(Song song) {
        play(song.getSongURI());
    }

    @Override
    public void play(List<Song> songs, int startPos) {
        ArrayList<Song> data = new ArrayList<>(songs);
        play(data.get(startPos));
        QueueManager.get().setQueue(data);
        QueueManager.get().setCurrentSongPos(startPos);
    }

    @Override
    public void resume() {
        requestAudioFocus();

        mMediaPlayer.start();

        //If interrupted because of audio focus, when focus returns then media will resume playing
        mWasPlaying = true;

        mService.registerReceiver(mAudioNoisyReceiver, mNoisyFilter);

    }

    @Override
    public void pause() {
        pause(true);
    }

    private void pause(boolean overrideWasPlaying) {
        giveUpAudioFocus();

        mMediaPlayer.pause();

        //If statement because if it is paused because of losing focus we still want to start playing again
        if (overrideWasPlaying) {
            //If audio focus got taken away and returned, the media won't start playing
            mWasPlaying = false;
        }

        mService.unregisterReceiver(mAudioNoisyReceiver);
    }

    @Override
    public void next() {
        play(QueueManager.get().getCurrentQueueAsSong(), QueueManager.get().updateNextSongPos());
    }

    @Override
    public void prev() {
        play(QueueManager.get().getCurrentQueueAsSong(), QueueManager.get().updatePrevSongPos());
    }

    @Override
    public void stop() {
        giveUpAudioFocus();
        mService.setState(STATE_STOPPED);
        mService.unregisterReceiver(mAudioNoisyReceiver);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public void repeat(boolean repeating) {
        mMediaPlayer.setLooping(repeating);
    }

    @Override
    public void seek(long time) {
        mMediaPlayer.seekTo((int) time);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Misc.
    ///////////////////////////////////////////////////////////////////////////

    private void createMediaPlayerIfNeeded() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            mMediaPlayer.setWakeMode(mService, PowerManager.PARTIAL_WAKE_LOCK);

            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnPreparedListener(this);
        } else {
            //Reset it
            mMediaPlayer.reset();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Variables for service and remote
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public boolean isRepeating() {
        return mMediaPlayer.isLooping();
    }

    @Override
    public boolean isInitialized() {
        return mService != null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Audiofocus
    ///////////////////////////////////////////////////////////////////////////

    private void requestAudioFocus() {
        if (!mHasAudioFocus) {
            mHasAudioFocus = (mAudioManager.requestAudioFocus(this, STREAM_MUSIC, AUDIOFOCUS_GAIN) == AUDIOFOCUS_REQUEST_GRANTED);
        }
    }

    private void giveUpAudioFocus() {
        if (mHasAudioFocus) {
            mHasAudioFocus = !(mAudioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        mHasAudioFocus = focusChange == AUDIOFOCUS_GAIN;
        switch (focusChange) {
            case AUDIOFOCUS_GAIN:
                if (!isPlaying() && mWasPlaying) {
                    resume();
                }
                unDuck();
                break;
            case AUDIOFOCUS_LOSS:
                pause(false);
                break;
            case AUDIOFOCUS_LOSS_TRANSIENT:
                pause(false);
                break;
            case AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                duck();
                break;
        }
    }

    public void duck() {
        mMediaPlayer.setVolume(0.2f, 0.2f);
    }

    public void unDuck() {
        mMediaPlayer.setVolume(1.0f, 1.0f);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listeners for various events
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Will attempt to play the next song
        next();
        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Toast.makeText(mService, R.string.playback_error_unsupported, Toast.LENGTH_SHORT).show();
                return true;
            case MediaPlayer.MEDIA_ERROR_IO:
                Toast.makeText(mService, R.string.playback_error_IO, Toast.LENGTH_SHORT).show();
                return true;
            default:
                Toast.makeText(mService, R.string.playback_error_generic, Toast.LENGTH_SHORT).show();
                return true;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Media player is prepared so we should start it
        mp.start();

        //Make the service foreground
        mService.setAsForeground();
    }
}
