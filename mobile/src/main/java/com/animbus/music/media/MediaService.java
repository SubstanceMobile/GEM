package com.animbus.music.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.animbus.music.R;
import com.animbus.music.media.objects.Album;
import com.animbus.music.media.objects.Song;
import com.animbus.music.ui.activity.nowPlaying.NowPlaying;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_MEDIA_BUTTON;
import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
import static android.media.AudioManager.STREAM_MUSIC;
import static android.support.v4.app.NotificationCompat.CATEGORY_TRANSPORT;
import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;
import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;
import static android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS;
import static android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_STOP;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_FAST_FORWARDING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_NONE;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_REWINDING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_SKIPPING_TO_NEXT;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_STOPPED;

/**
 * Created by Adrian on 11/21/2015.
 */
public class MediaService extends Service {
    private static final String TAG = "MediaService";
    private static final int FOREGROUND_ID = 654321654;
    static final String ACTION_START = "GEM_START_SERVICE";
    PlaybackStateCompat mState;
    MediaSessionCompat mSession;
    PlaybackBase IMPL;

    ///////////////////////////////////////////////////////////////////////////
    // Binding
    ///////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_START.equals(intent.getAction())) {
            setUp();
        } else if (ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            MediaButtonReceiver.handleIntent(mSession, intent);
        }
        return START_STICKY;
    }

    ///////////////////////////////////////////////////////////////////////////
    // A few classes used for media playback
    ///////////////////////////////////////////////////////////////////////////

    static abstract class PlaybackBase extends MediaSessionCompat.Callback {
        private static final String TAG = "PlaybackBase";
        public static final String ACTION_PLAY_FROM_LIST = "GEM_PLAY_FROM_LIST";
        public static final String ACTION_SET_REPEAT = "GEM_SET_REPEAT";

        private static final long PREV_RESTARTS_AFTER = 5000;

        abstract void init(MediaService service);

        abstract void play(Uri uri, boolean notifyPlaybackRemote);

        abstract void play(Song song);

        abstract void play(List<Song> songs, int startPos);

        abstract void resume();

        abstract void pause();

        abstract void next();

        abstract void doPrev();

        void prev() {
            Log.d(TAG, "prev() called. Calling doPrev() = " + (getCurrentPosInSong() < PREV_RESTARTS_AFTER));
            //Previous only plays the previous song up to 5 seconds into the song. Afterwards it just restarts the song.
            if (getCurrentPosInSong() < PREV_RESTARTS_AFTER) {
                doPrev();
            } else {
                restart();
            }
        }

        abstract void restart();

        abstract void stop();

        abstract void repeat(boolean repeating);

        abstract void seek(long time);

        abstract boolean isPlaying();

        abstract boolean isRepeating();

        abstract boolean isInitialized();

        abstract int getCurrentPosInSong();

        @Override
        public void onCustomAction(String action, Bundle extras) {
            if (ACTION_PLAY_FROM_LIST.equals(action)) {
                play(PlaybackRemote.tempSongList, PlaybackRemote.tempListStartPos);
            } else if (ACTION_SET_REPEAT.equals(action)) {
                repeat(PlaybackRemote.tempRepeating);
            }
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            play(uri, true);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            play(Library.findSongById(Long.valueOf(mediaId)));
        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            //TODO Add this
        }

        @Override
        public void onPlay() {
            resume();
        }

        @Override
        public void onPause() {
            pause();
        }

        @Override
        public void onSkipToNext() {
            next();
        }

        @Override
        public void onSkipToPrevious() {
            prev();
        }

        @Override
        public void onSeekTo(long pos) {
            seek(pos);
        }

        @Override
        public void onStop() {
            stop();
        }

    }

    static class LocalPlayback extends PlaybackBase implements AudioManager.OnAudioFocusChangeListener,
            MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
        private static final String TAG = "LocalPlayback";
        IntentFilter mNoisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
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

        ///////////////////////////////////////////////////////////////////////////
        // Audio Becoming Noisy filter
        ///////////////////////////////////////////////////////////////////////////
        //Self explanatory. Indicates that GEM has audio focus in the system.
        private boolean mHasAudioFocus = false;
        BroadcastReceiver mAudioNoisyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                    Log.d(TAG, "AudioNoisy received. Pausing");
                    if (isPlaying()) pause();
                }
            }
        };

        ///////////////////////////////////////////////////////////////////////////
        // Configure this with service
        ///////////////////////////////////////////////////////////////////////////

        @Override
        public void init(MediaService service) {
            Log.d(TAG, "init() called");
            mService = service;
            mAudioManager = (AudioManager) service.getSystemService(Context.AUDIO_SERVICE);
        }

        ///////////////////////////////////////////////////////////////////////////
        // Controls
        ///////////////////////////////////////////////////////////////////////////

        @Override
        public void play(Uri uri, boolean notifyPlaybackRemote) {
            Log.d(TAG, "play(Uri) called. Uri = " + uri.toString());
            requestAudioFocus();
            mWasPlaying = true;
            mService.registerReceiver(mAudioNoisyReceiver, mNoisyFilter);
            Log.d(TAG, "play(Uri): Noisy receiver registered");
            if (uri != mCurrentUri) {
                Log.d(TAG, "play(Uri): Uri is different. Playing new song");
                //Song is different
                mCurrentUri = uri;
                try {
                    createMediaPlayerIfNeeded();
                    mMediaPlayer.setDataSource(mService, uri);
                    mMediaPlayer.setAudioStreamType(STREAM_MUSIC);
                    mService.setState(STATE_PLAYING);
                    prepareAndNotifyService();
                } catch (Exception e) {
                    Log.e(TAG, "play(Uri): Error", e);
                }

                //Basically gives a song filled with dummy content
                if (notifyPlaybackRemote) PlaybackRemote.updateSongListeners(uri);
            } else {
                Log.d(TAG, "play(Uri): Uri is the same. Restarting this song");
                //Same song, restart playback
                seek(0);

                //Prepares the media player and it will start it eventually
                prepareAndNotifyService();
            }
        }

        @Override
        public void play(Song song) {
            Log.d(TAG, "play(Song) called. Song = " + song.getSongID());
            play(song.getSongURI(), false);
            PlaybackRemote.updateSongListeners(song);
        }

        @Override
        public void play(List<Song> songs, int startPos) {
            ArrayList<Song> data = new ArrayList<>(songs);
            Log.d(TAG, "play(List, int) called. Start Position = " + startPos);
            play(data.get(startPos));
            PlaybackRemote.setQueue(data);
            PlaybackRemote.setCurrentSongPos(startPos);
        }

        @Override
        public void resume() {
            Log.d(TAG, "resume() called");
            requestAudioFocus();
            mService.setState(STATE_PLAYING);

            Log.d(TAG, "resume(): Starting Media Player");
            mMediaPlayer.start();

            //If interrupted because of audio focus, when foc]us returns then media will resume playing
            mWasPlaying = true;

            Log.d(TAG, "resume(): Registering Receiver");
            mService.registerReceiver(mAudioNoisyReceiver, mNoisyFilter);
        }

        @Override
        public void pause() {
            Log.d(TAG, "pause() called. Calling pause(boolean)");
            pause(true);
        }

        private void pause(boolean overrideWasPlaying) {
            Log.d(TAG, "pause(boolean) called. overrideWasPlaying = " + overrideWasPlaying);
            giveUpAudioFocus();
            mService.setState(STATE_PAUSED);

            Log.d(TAG, "pause(boolean): Pausing Media Player");
            mMediaPlayer.pause();

            //If statement because if it is paused because of losing focus we still want to start playing again
            if (overrideWasPlaying) {
                //If audio focus got taken away and returned, the media won't start playing
                mWasPlaying = false;
            }

            Log.d(TAG, "pause(boolean): Unregistering Receiver");
            mService.unregisterReceiver(mAudioNoisyReceiver);

            Log.d(TAG, "pause(boolean): Stopping the service from being foreground");
            mService.stopForeground(false);
        }

        @Override
        public void next() {
            mService.setState(STATE_SKIPPING_TO_NEXT);

            Log.d(TAG, "next() called. Calling play(queue, nextSongPos)");
            play(PlaybackRemote.getQueue(), PlaybackRemote.getNextSongPos());
        }

        @Override
        public void doPrev() {
            mService.setState(STATE_SKIPPING_TO_PREVIOUS);

            Log.d(TAG, "doPrev() called. Calling play(queue, prevSongPos)");
            play(PlaybackRemote.getQueue(), PlaybackRemote.getPrevSongPos());
        }

        @Override
        void restart() {
            Log.d(TAG, "restart() called");
            Log.d(TAG, "restart(): Stopping Media Player");
            mMediaPlayer.stop();
            seek(0);
            prepareAndNotifyService();

            Log.d(TAG, "restart(): Done");
        }

        @Override
        public void stop() {
            Log.d(TAG, "stop() called");
            giveUpAudioFocus();
            mService.setState(STATE_STOPPED);
            mService.stopForeground(true);

            Log.d(TAG, "stop(): Unregistering Receiver");
            mService.unregisterReceiver(mAudioNoisyReceiver);

            Log.d(TAG, "stop(): Stopping player");
            mMediaPlayer.stop();

            Log.d(TAG, "stop(): Resetting player");
            mMediaPlayer.reset();

            Log.d(TAG, "stop(): Releasing Player");
            mMediaPlayer.release();

            Log.d(TAG, "stop(): Making Player null");
            mMediaPlayer = null;

            Log.d(TAG, "stop(): Killing Service");
            mService.stopSelf();
        }

        @Override
        public void repeat(boolean repeating) {
            Log.d(TAG, "repeat(boolean) called. repeating = " + repeating);
            mMediaPlayer.setLooping(repeating);
        }

        @Override
        public void seek(long time) {
            Log.d(TAG, "seek(long) called. time = " + time);
            mService.setState(getCurrentPosInSong() != time ? (getCurrentPosInSong() < time ? STATE_FAST_FORWARDING : STATE_REWINDING) : STATE_BUFFERING);

            Log.d(TAG, "seek(long): seeking MediaPlayer");
            mMediaPlayer.seekTo((int) time);
        }

        ///////////////////////////////////////////////////////////////////////////
        // Misc.
        ///////////////////////////////////////////////////////////////////////////

        private void createMediaPlayerIfNeeded() {
            Log.d(TAG, "createMediaPlayerIfNeeded() called. Needed = " + (mMediaPlayer == null));
            if (mMediaPlayer == null) {
                Log.d(TAG, "createMediaPlayerIfNeeded(): Creating Media Player");
                mMediaPlayer = new MediaPlayer();

                Log.d(TAG, "createMediaPlayerIfNeeded(): Configuring wakelock");
                mMediaPlayer.setWakeMode(mService, PowerManager.PARTIAL_WAKE_LOCK);

                Log.d(TAG, "createMediaPlayerIfNeeded(): Setting listeners");
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.setOnPreparedListener(this);
            } else {
                //Reset it
                Log.d(TAG, "createMediaPlayerIfNeeded(): Resetting Media Player");
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

        @Override
        int getCurrentPosInSong() {
            return mMediaPlayer.getCurrentPosition();
        }

        void prepareAndNotifyService() {
            Log.d(TAG, "prepareAndNotifyService() called");
            mService.setState(STATE_BUFFERING);
            mMediaPlayer.prepareAsync();
        }

        ///////////////////////////////////////////////////////////////////////////
        // Audiofocus
        ///////////////////////////////////////////////////////////////////////////

        private void requestAudioFocus() {
            if (!mHasAudioFocus) {
                Log.d(TAG, "requestAudioFocus() called. Requesting");
                mHasAudioFocus = (mAudioManager.requestAudioFocus(this, STREAM_MUSIC, AUDIOFOCUS_GAIN) == AUDIOFOCUS_REQUEST_GRANTED);
            } else {
                Log.d(TAG, "requestAudioFocus() called. Already has audio focus");
            }
        }

        private void giveUpAudioFocus() {
            if (mHasAudioFocus) {
                Log.d(TAG, "giveUpAudioFocus() called. Giving Up");
                mHasAudioFocus = !(mAudioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
            } else {
                Log.d(TAG, "giveUpAudioFocus() called. Doesn't have audio focus anyways");
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
            Log.d(TAG, "duck() called");
            mMediaPlayer.setVolume(0.2f, 0.2f);
        }

        public void unDuck() {
            Log.d(TAG, "unDuck() called");
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
            Log.d(TAG, "onPrepared() called");

            //Media player is prepared so we should start it
            Log.d(TAG, "onPrepared(): Starting Media Player");
            mp.start();

            //Make the service foreground
            mService.setAsForeground();
        }
    }

    public static class MediaNotification {
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //All of the Variables
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        public static final int NOTIFICATION_ID = 1151415975;
        public static final int REQ_CODE = 884987321;

        static final String ACTION_PLAY = "GEM_NOTIFICATION_PLAY";
        static final String ACTION_PAUSE = "GEM_NOTIFICATION_PAUSE";
        static final String ACTION_NEXT = "GEM_NOTIFICATION_NEXT";
        static final String ACTION_PREV = "GEM_NOTIFICATION_PREV";
        static final String ACTION_STOP = "GEM_NOTIFICATION_STOP";

        static volatile MediaService mService;
        static volatile NotificationCompat.Builder mBuilder;

        static final BroadcastReceiver mCommandsReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getAction();
                switch (s) {
                    case ACTION_PLAY:
                        mService.IMPL.resume();
                        break;
                    case ACTION_PAUSE:
                        mService.IMPL.pause();
                        break;
                    case ACTION_NEXT:
                        mService.IMPL.next();
                        break;
                    case ACTION_PREV:
                        mService.IMPL.prev();
                        break;
                    case ACTION_STOP:
                        mService.IMPL.stop();
                        mService.unregisterReceiver(this);
                        break;
                }
            }
        };

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //The constructor
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        static void init(MediaService service) {
            mService = service;
            PlaybackRemote.registerStateListener(new PlaybackRemote.StateChangedListener() {
                @Override
                public void onStateChanged(PlaybackStateCompat newState) {
                    update();
                }
            });
        }

        static void setUp() {
            Song song = PlaybackRemote.getQueue().get(PlaybackRemote.getCurrentSongPos());
            PendingIntent stopServiceIntent = PendingIntent.getBroadcast(mService, REQ_CODE, new Intent(ACTION_STOP), PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder = new NotificationCompat.Builder(mService);

            mBuilder
                    .setContentTitle(song.songTitle)
                    .setContentText(song.songArtist)
                    .setSubText(song.getAlbum().getAlbumTitle())
                    .setStyle(
                            new NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2)
                                    .setMediaSession(mService.mSession.getSessionToken()).setShowCancelButton(true).setCancelButtonIntent(stopServiceIntent))
                    .setSmallIcon(R.mipmap.ic_notificstaion_srini)
                    .setCategory(CATEGORY_TRANSPORT)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .setDeleteIntent(stopServiceIntent)
                    .setContentIntent(PendingIntent.getActivity(mService, REQ_CODE,
                            new Intent(mService, NowPlaying.class), PendingIntent.FLAG_CANCEL_CURRENT))
                    .setShowWhen(false)
                    .setPriority(PRIORITY_MAX);

            song.getAlbum().requestArt(new Album.ArtRequest() {
                @Override
                public void respond(Bitmap albumArt) {
                    mBuilder.setLargeIcon(albumArt);
                }
            });

            //The Actions

            mBuilder.addAction(R.drawable.ic_skip_previous_white_36dp, mService.getString(R.string.playback_prev), PendingIntent.getBroadcast(mService, REQ_CODE,
                    new Intent(ACTION_PREV), PendingIntent.FLAG_CANCEL_CURRENT));

            if (mService.IMPL.isPlaying()) {
                mBuilder.addAction(R.drawable.ic_pause_white_36dp, mService.getString(R.string.playback_pause), PendingIntent.getBroadcast(mService, REQ_CODE,
                        new Intent(ACTION_PAUSE), PendingIntent.FLAG_CANCEL_CURRENT));
            } else {
                mBuilder.addAction(R.drawable.ic_play_arrow_white_36dp, mService.getString(R.string.playback_play), PendingIntent.getBroadcast(mService, REQ_CODE,
                        new Intent(ACTION_PLAY), PendingIntent.FLAG_CANCEL_CURRENT));
            }

            mBuilder.addAction(R.drawable.ic_skip_next_white_36dp, mService.getString(R.string.playback_next), PendingIntent.getBroadcast(mService, REQ_CODE,
                    new Intent(ACTION_NEXT), PendingIntent.FLAG_CANCEL_CURRENT));

            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_PLAY);
            filter.addAction(ACTION_PAUSE);
            filter.addAction(ACTION_NEXT);
            filter.addAction(ACTION_PREV);
            filter.addAction(ACTION_STOP);
            mService.registerReceiver(mCommandsReciever, filter);
        }

        static void update() {
            setUp();
            if (mService.mSession.isActive())
                NotificationManagerCompat.from(mService).notify(NOTIFICATION_ID, getNotification());
        }

        static Notification getNotification() {
            return mBuilder.build();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // The code powering all of the media playback
    ///////////////////////////////////////////////////////////////////////////

    void inject(PlaybackBase impl) {
        IMPL = impl;
    }

    void setUp() {
        PlaybackRemote.init(this);
        IMPL.init(this);
        MediaNotification.init(this);

        mSession = new MediaSessionCompat(this, TAG);
        mSession.setCallback(IMPL);
        mSession.setPlaybackToLocal(AudioManager.STREAM_MUSIC);
        mSession.setFlags(FLAG_HANDLES_MEDIA_BUTTONS | FLAG_HANDLES_TRANSPORT_CONTROLS);
        setState(STATE_NONE);
        PlaybackRemote.registerSongListener(new PlaybackRemote.SongChangedListener() {
            @Override
            public void onSongChanged(Song newSong) {
                mSession.setMetadata(newSong.getMetaData(MediaService.this));
            }
        });
    }

    void setState(int state) {
        Log.d(TAG, "setState(int) called. Building new state");
        //This sets up the state
        PlaybackStateCompat.Builder mBuilder = new PlaybackStateCompat.Builder(mState);
        mBuilder.setActions(
                ACTION_PLAY | ACTION_PAUSE | ACTION_SKIP_TO_NEXT | ACTION_SKIP_TO_PREVIOUS | ACTION_STOP |
                ACTION_SEEK_TO | ACTION_PLAY_FROM_MEDIA_ID);
        mBuilder.setState(state, IMPL.getCurrentPosInSong(), 1.0f);
        mState = mBuilder.build();

        //Updates things with the new state
        Log.d(TAG, "setState(int): Updating Session with new state");
        mSession.setPlaybackState(mState);
        PlaybackRemote.updateStateListeners(mState);

        //This tells the session weather or not it should be considered active
        boolean isActive = state != STATE_NONE && state != STATE_STOPPED && IMPL.isInitialized();
        Log.d(TAG, "setState(int): Setting the session's active state to " + isActive);
        mSession.setActive(isActive);
    }

    void setAsForeground() {
        Log.d(TAG, "setAsForeground() called");
        startForeground(FOREGROUND_ID, MediaNotification.getNotification());
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() called");
        mSession.release();
    }

}
