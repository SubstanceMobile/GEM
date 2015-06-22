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

import com.animbus.music.data.dataModels.SongInfoHolder;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    final IBinder musicBind = new MusicBinder();
    MediaPlayer player;
    Context cxt;
    Boolean doRepeat, isPaused;
    Integer MAX_RESTART_ON_PREV_CLICKED_DUR = /*Time in ms*/ 3000;
    List<SongInfoHolder> queue;
    onStopListner onStopListner;
    Integer currentPosition;

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
        stopPlayback();
        return false;
    }

    public void setContext(Context context) {
        cxt = context;
    }

    public void playSong(SongInfoHolder songInfo) {
        try {
            player.setDataSource(cxt, songInfo.getSongURI());
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.prepareAsync();
    }

    public void playSong(List<SongInfoHolder> list, Integer position) {
        SongInfoHolder songInfo = list.get(position);
        playSong(songInfo);
    }

    public void stopPlayback() {
        player.stop();
        player.release();
        onStopListner.onStop();
    }

    public void setOnStopListner(MusicService.onStopListner onStopListner) {
        this.onStopListner = onStopListner;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext();
    }

    public SongInfoHolder getCurrentSong() {
        //This will return the currently playing song. 0 because 0 = now playing.
        return queue.get(getCurrentSongPos());
    }

    public int getCurrentSongPos(){
        return currentPosition;
    }

    public SongInfoHolder getPrevSong() {
        SongInfoHolder song;
        if (getCurrentSongPos() == 0){
            song = queue.get(queue.size());
        } else {
            song = queue.get(getCurrentSongPos() - 1);
        }
        return song;
    }

    public SongInfoHolder getNextSong() {
        //we use 1 because 0 is current, queue.size is previous, and 1 is next
        return queue.get(getCurrentSongPos() + 1);
    }

    public void pause() {
        player.pause();
        isPaused =true;
    }

    public void resume() {
        if (isPaused) {
            player.start();
        }
    }

    public void playNext() {
        if (doRepeat) {
            if (getCurrentSong().isRepeating()) {
                playSong(getCurrentSong());
            } else {
                playSong(getNextSong());
            }
        } else {
            playSong(getNextSong());
        }
    }

    public void playPrev() {
        if (player.getCurrentPosition() <= MAX_RESTART_ON_PREV_CLICKED_DUR) {
            playSong(getCurrentSong());
        } else {
            playSong(getPrevSong());
        }
    }

    public void addToQueue(SongInfoHolder song) {
        queue.add(song);
    }

    public void removeFromQueue(int position) {
        queue.remove(position);
    }

    public List<SongInfoHolder> getQueue() {
        return queue;
    }

    public void setQueue(List<SongInfoHolder> queue) {
        this.queue = queue;
    }

    public void setRepeat(Boolean doRepeat) {
        this.doRepeat = doRepeat;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //TODO:Use Reasource for Text
        Toast.makeText(cxt, "An Error has Occurred, couldn't play music", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
    }

    interface onStopListner {
        void onStop();
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}