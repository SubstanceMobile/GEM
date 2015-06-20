package com.animbus.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    final IBinder musicBind = new MusicBinder();
    //Media player, Song List, and Current item_songlist from the array list
    MediaPlayer player;
    Context cxt;
    Integer repeatType;

    public MusicService(Context context) {
        player = new MediaPlayer();
        player.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        cxt = context;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        if (getRepeatType() != 2) {
            if (getNextSong() != Long.parseLong(null)) {
                playSong(getNextSong());
            } else {
                repeat(getRepeatType());
            }
        } else {
            playSong(getCurrentSong());
        }
    }

    public long getCurrentSong() {
        return Long.parseLong(null);
    }

    public void getPrevSong() {

    }

    public long getNextSong() {
        return Long.parseLong(null);
    }

    public void getNowPlayingList() {

    }

    public void setSong() {

    }

    public void repeat(Integer howToRepeat) {
        switch (howToRepeat) {
            case 0:
                player.stop();
                player.release();
                break;
            case 1:

        }
    }

    public Integer getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(Integer repeatType) {
        this.repeatType = repeatType;
    }

    public void playSong(long song) {
        player.prepareAsync();
        player.start();
    }

    public void playSong(List list, Integer position) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //TODO:Use Reasource for Text
        Toast.makeText(cxt, "An Error has Occurred, couldn't play music", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
