package com.animbus.music.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.animbus.music.R;
import com.animbus.music.media.objects.Album;
import com.animbus.music.media.objects.Song;
import com.animbus.music.ui.nowPlaying.NowPlaying;

import static android.support.v4.app.NotificationCompat.CATEGORY_TRANSPORT;
import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;
import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

/**
 * Created by Adrian on 7/20/2015
 */
public class MediaNotification extends BroadcastReceiver {
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //All of the Variables
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int NOTIFICATION_ID = 412;

    public static final int REQ_CODE = 100;

    public static final String ACTION_PLAY = "music_PLAY";
    public static final String ACTION_PAUSE = "music_PAUSE";
    public static final String ACTION_NEXT = "music_NEXT";
    public static final String ACTION_PREV = "music_PREV";
    public static final String ACTION_STOP = "music_STOP";
    public static final String ACTION_QUEUE = "music_QUEUE";
    public static final String ACTION_EXIT_QUEUE = "music_EXIT_QUEUE";

    private final MediaService mService;
    String stringPrev;
    String stringPlay;
    String stringPause;
    String stringsNext;
    String stringQueue;
    private MediaSessionCompat.Token mSessionToken;
    private MediaControllerCompat mController;
    private MediaControllerCompat.TransportControls mTransportControls;
    private PlaybackStateCompat mPlaybackState;
    private NotificationManagerCompat mNotificationManager;
    private int mNotificationColor;

    private NotificationCompat.Builder mBuilder;
    private Notification mNotification;

    private boolean mDisplayinQueue = false;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //The constructor
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MediaNotification(MediaService service) {
        mService = service;
        mNotificationColor = mService.getResources().getColor(R.color.primaryGreyDark);
        mNotificationManager = NotificationManagerCompat.from(service);

        stringPrev = mService.getResources().getString(R.string.playback_prev);
        stringPlay = mService.getResources().getString(R.string.playback_play);
        stringPause = mService.getResources().getString(R.string.playback_pause);
        stringsNext = mService.getResources().getString(R.string.playback_next);
        stringQueue = mService.getResources().getString(R.string.playback_queue);

        PlaybackManager.get().registerListener(new PlaybackManager.OnChangedListener() {
            @Override
            public void onSongChanged(Song song) {
                update();
            }

            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                mPlaybackState = state;
                if (PlaybackManager.get().isActive()) update();
            }
        });
    }

    private void addPrevious() {
        mBuilder.addAction(R.drawable.ic_skip_previous_white_36dp, stringPrev, PendingIntent.getBroadcast(mService, REQ_CODE,
                new Intent(ACTION_PREV), PendingIntent.FLAG_CANCEL_CURRENT));
    }

    private void addPlayPause() {
        if (PlaybackManager.get().isPlaying()) {
            mBuilder.addAction(R.drawable.ic_pause_white_36dp, stringPause, PendingIntent.getBroadcast(mService, REQ_CODE,
                    new Intent(ACTION_PAUSE), PendingIntent.FLAG_CANCEL_CURRENT));
        } else {
            mBuilder.addAction(R.drawable.ic_play_arrow_white_36dp, stringPlay, PendingIntent.getBroadcast(mService, REQ_CODE,
                    new Intent(ACTION_PLAY), PendingIntent.FLAG_CANCEL_CURRENT));
        }
    }

    private void addNext() {
        mBuilder.addAction(R.drawable.ic_skip_next_white_36dp, stringsNext, PendingIntent.getBroadcast(mService, REQ_CODE,
                new Intent(ACTION_NEXT), PendingIntent.FLAG_CANCEL_CURRENT));
    }

    private void addQueue() {
        mBuilder.addAction(R.drawable.ic_queue_music_white_24dp, stringsNext, PendingIntent.getBroadcast(mService, REQ_CODE,
                new Intent(ACTION_QUEUE), PendingIntent.FLAG_CANCEL_CURRENT));
    }

    public void setUp() {
        Song song = PlaybackManager.get().getCurrentSong();

        PendingIntent stopServiceIntent = PendingIntent.getBroadcast(mService, REQ_CODE, new Intent(ACTION_STOP), PendingIntent.FLAG_CANCEL_CURRENT);

        mBuilder = new NotificationCompat.Builder(mService);
        if (!mDisplayinQueue) {
            mBuilder
                    .setContentTitle(song.songTitle)
                    .setContentText(song.songArtist)
                    .setSubText(song.getAlbum().getAlbumTitle())
                    .setColor(mNotificationColor)
                    .setStyle(
                            new NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2)
                                    .setMediaSession(mService.getSession().getSessionToken()).setShowCancelButton(true).setCancelButtonIntent(stopServiceIntent))
                    .setSmallIcon(R.mipmap.ic_notificstaion_srini)
                    .setCategory(CATEGORY_TRANSPORT)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .setDeleteIntent(stopServiceIntent)
                    .setContentIntent(PendingIntent.getActivity(mService, REQ_CODE,
                            new Intent(mService, NowPlaying.class), PendingIntent.FLAG_CANCEL_CURRENT))
                    .setShowWhen(false)
                    .setPriority(PRIORITY_MAX);

            song.getAlbum().requestArt(mService, new Album.ArtRequest() {
                @Override
                public void respond(Bitmap albumArt) {
                    mBuilder.setLargeIcon(albumArt);
                }
            });

            addPrevious();
            addPlayPause();
            addNext();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_PLAY);
            filter.addAction(ACTION_PAUSE);
            filter.addAction(ACTION_NEXT);
            filter.addAction(ACTION_PREV);
            filter.addAction(ACTION_STOP);
            filter.addAction(ACTION_QUEUE);
            filter.addAction(ACTION_EXIT_QUEUE);
            mService.registerReceiver(this, filter);
        } else {
            mService.sendBroadcast(new Intent(ACTION_EXIT_QUEUE));
        }

        mNotification = mBuilder.build();
    }

    public void update() {
        if (PlaybackManager.get().getCurrentSong() != null) {
            setUp();
            if (mPlaybackState.getState() != PlaybackStateCompat.STATE_STOPPED) {
                mNotificationManager.notify(NOTIFICATION_ID, getNotification());
            }
        }
    }

    public Notification getNotification() {
        return mNotification;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MediaControllerCompat.TransportControls tc = PlaybackManager.get().getService().getSession().getController().getTransportControls();
        if (intent.getAction().equals(ACTION_PLAY)) {
            tc.play();
        } else if (intent.getAction().equals(ACTION_PAUSE)) {
            tc.pause();
        } else if (intent.getAction().equals(ACTION_NEXT)) {
            tc.skipToNext();
        } else if (intent.getAction().equals(ACTION_PREV)) {
            tc.skipToPrevious();
        } else if (intent.getAction().equals(ACTION_STOP)) {
            tc.stop();
        } else if (intent.getAction().equals(ACTION_QUEUE)) {
            mDisplayinQueue = true;
            update();
            Toast.makeText(mService, "Enter Queue", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(ACTION_EXIT_QUEUE)) {
            Toast.makeText(mService, "Exit Queue", Toast.LENGTH_SHORT).show();
            mDisplayinQueue = false;
            update();
        }
    }
}
