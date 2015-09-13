package com.animbus.music.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.session.MediaController;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.view.KeyEvent;

import com.animbus.music.R;
import com.animbus.music.media.objects.Song;
import com.animbus.music.ui.mainScreen.MainScreen;

import static android.support.v7.app.NotificationCompat.CATEGORY_TRANSPORT;
import static android.support.v7.app.NotificationCompat.COLOR_DEFAULT;
import static android.support.v7.app.NotificationCompat.MediaStyle;
import static android.support.v7.app.NotificationCompat.PRIORITY_MAX;
import static android.support.v7.app.NotificationCompat.VISIBILITY_PUBLIC;

/**
 * Created by Adrian on 7/20/2015
 */
public class MediaNotification {
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //All of the Variables
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int NOTIFICATION_ID = 412;

    public static final int UI_REQ_CODE = 126;
    public static final int PAUSE_REQ_CODE = 127;
    public static final int PLAY_REQ_CODE = 128;
    public static final int PREV_REQ_CODE = 129;
    public static final int NEXT_REQ_CODE = 130;
    public static final int STOP_REQ_CODE = 131;

    private final MediaService mService;
    private MediaSessionCompat.Token mSessionToken;
    private MediaControllerCompat mController;
    private MediaControllerCompat.TransportControls mTransportControls;

    private PlaybackStateCompat mPlaybackState;
    private MediaMetadataCompat mMetadata;

    private NotificationManagerCompat mNotificationManager;

    private int mNotificationColor;

    private boolean mStarted = false;

    private NotificationCompat.Builder mBuilder;

    String stringPrev;
    String stringPlay;
    String stringPause;
    String stringsNext;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //The constructor
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MediaNotification(MediaService service) {
        mService = service;
        mNotificationColor = COLOR_DEFAULT;
        mNotificationManager = NotificationManagerCompat.from(service);

        stringPrev = mService.getResources().getString(R.string.playback_prev);
        stringPlay = mService.getResources().getString(R.string.playback_play);
        stringPause = mService.getResources().getString(R.string.playback_pause);
        stringsNext = mService.getResources().getString(R.string.playback_next);

        PlaybackManager.get().registerListener(new PlaybackManager.OnChangedListener() {
            @Override
            public void onSongChanged(Song song) {
                update();
            }

            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                mPlaybackState = state;
                update();
            }
        });
    }

    private void addPrevious(){
        mBuilder.addAction(R.drawable.ic_skip_previous_white_48dp, stringPrev, PendingIntent.getBroadcast(mService, PREV_REQ_CODE,
                new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS)), PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private void addPlayPause(){
        if (PlaybackManager.get().isPlaying()){
            mBuilder.addAction(R.drawable.ic_pause_white_48dp, stringPause, PendingIntent.getBroadcast(mService, PAUSE_REQ_CODE,
                    new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE)), PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            mBuilder.addAction(R.drawable.ic_play_arrow_white_48dp, stringPlay, PendingIntent.getBroadcast(mService, PLAY_REQ_CODE,
                    new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY)), PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    private void addNext(){
        mBuilder.addAction(R.drawable.ic_skip_next_white_48dp, stringsNext, PendingIntent.getBroadcast(mService, NEXT_REQ_CODE,
                new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT)), PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public void setUp(){
        Song song = PlaybackManager.get().getCurrentSong();

            mBuilder = new NotificationCompat.Builder(mService);
            mBuilder
                    .setContentTitle(song.songTitle)
                    .setContentText(song.songArtist)
                    .setColor(mNotificationColor)
                    .setStyle(new MediaStyle().setShowActionsInCompactView(0,1,2))
                    .setSmallIcon(R.mipmap.ic_notificstaion_srini)
                    .setLargeIcon(song.getAlbum().getAlbumArt())
                    .setCategory(CATEGORY_TRANSPORT)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .setDeleteIntent(PendingIntent.getBroadcast(mService, STOP_REQ_CODE,
                            new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_STOP)), PendingIntent.FLAG_UPDATE_CURRENT))
                    //TODO:Fix Intent
                    .setContentIntent(PendingIntent.getActivity(mService, UI_REQ_CODE,
                            new Intent(mService, MainScreen.class), PendingIntent.FLAG_UPDATE_CURRENT))
                    .setShowWhen(false)
                    .setPriority(PRIORITY_MAX);
            addPrevious();
            addPlayPause();
            addNext();
    }

    public void update() {
        if (PlaybackManager.get().getCurrentSong() != null) {
            setUp();
            if (mPlaybackState.getState() != PlaybackStateCompat.STATE_STOPPED) {
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        }
    }

    public Notification getNotification(){
        return mBuilder.build();
    }
}
