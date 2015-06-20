package com.animbus.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import com.animbus.music.data.dataModels.SongInfoHolder;

import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    final IBinder musicBind = new MusicBinder();
    //Media player, Song List, and Current item_songlist from the array list
    MediaPlayer player;
    Context cxt;
    Integer repeatType;
    public static Integer REPEAT_TYPE_ALL = 0,REPEAT_TYPE_ONE = 1;
    List<SongInfoHolder> data;

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
        switch (repeatType) {
            case 0:
                playSong(getCurrentSong());
                break;
            case 1:
                playSong(getNextSong());
        }
    }

    public SongInfoHolder getCurrentSong() {
        //This will return the currently playing song. 0 because 0 = now playing.
        return data.get(0);
    }

    public void getPrevSong() {

    }

    public SongInfoHolder getNextSong() {
        //we use 1 because 0 is current, data.size is previous, and 1 is next
        return data.get(1);
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

    public void playSong(SongInfoHolder songInfo) {
        player.prepareAsync();
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
