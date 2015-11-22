package com.animbus.music.media.experimental;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

/**
 * Created by Adrian on 11/21/2015.
 */
class MediaService extends Service {
    private static final String TAG = "MediaService";
    private static final String ACTION_START = "GEM_START_SERVICE";

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
        setUp();
        return START_STICKY;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Actual Code
    ///////////////////////////////////////////////////////////////////////////

    PlaybackStateCompat mState;
    MediaSessionCompat mSession;

    void setUp() {
        PlaybackRemote.init(this);
        mSession = new MediaSessionCompat(this, TAG);
        mSession.setCallback(new MediaCallback());
    }

    void setState(int state) {

    }

    void setAsForeground() {

    }

    class MediaCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            PlaybackRemote.resume();
        }

        @Override
        public void onPause() {
            PlaybackRemote.pause();
        }

        @Override
        public void onSkipToNext() {
            PlaybackRemote.next();
        }

        @Override
        public void onSkipToPrevious() {
            PlaybackRemote.prev();
        }

        @Override
        public void onSeekTo(long pos) {
            PlaybackRemote.seek(pos);
        }

        @Override
        public void onStop() {
            PlaybackRemote.stop();
            mSession.release();
            stopSelf();
        }
    }

}
