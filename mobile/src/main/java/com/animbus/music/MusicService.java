package com.animbus.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.animbus.music.data.objects.Song;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    final IBinder musicBind = new MusicBinder();
    MediaPlayer player;
    Context cxt;
    Boolean doRepeat = false, isPaused;
    Integer MAX_RESTART_ON_PREV_CLICKED_DUR = /*Time in ms*/ 3000;
    List<Song> queue;
    Integer currentPosition;
    UpdatePushListener pushedListener;

    public MusicService() {
        player = new MediaPlayer();
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

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

    public void playSong(Song songInfo) {
        player.stop();
        player.reset();
        Boolean errorHappened;
        try {
            player.setDataSource(cxt, songInfo.getSongURI());
            errorHappened = false;
        } catch (IOException e) {
            e.printStackTrace();
            errorHappened = true;
            Toast.makeText(cxt, "Error" + " at " + songInfo.getSongURI(), Toast.LENGTH_SHORT).show();
        }
        if (!errorHappened) {
            player.prepareAsync();
        }
        isPaused = false;
        pushUpdatedInfo();
    }

    public void playSong(List<Song> list, Integer position) {
        player.stop();
        player.reset();
        Song songInfo = list.get(position);
        Boolean error;
        try {
            player.setDataSource(cxt, songInfo.getSongURI());
            error = false;
        } catch (IOException e) {
            e.printStackTrace();
            error = true;
            Toast.makeText(cxt, "Error" + " at " + songInfo.getSongURI(), Toast.LENGTH_SHORT).show();
        }
        if (!error) {
            player.prepareAsync();
        }
        setCurrentSongPos(position);
        isPaused = false;
        pushUpdatedInfo();
    }

    public void stopPlayback() {
        player.stop();
        player.release();
        pushUpdatedInfo();
        stopSelf();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext(false);
        pushUpdatedInfo();
    }

    public boolean getPlaying() {
        return player.isPlaying();
    }

    public Song getCurrentSong() {
        //This will return the currently playing song. 0 because 0 = now playing.
        return queue.get(getCurrentSongPos());
    }

    public int getCurrentSongPos() {
        return currentPosition;
    }

    public void setCurrentSongPos(int pos) {
        currentPosition = pos;
        pushUpdatedInfo();
    }

    public void setCurrentSongPos(Song song) {
        if (queue != null) {
            queue.indexOf(song);
        } else {
            Log.println(Log.ERROR, "TAG_NO_QUEUE", "No Queue");
        }
        pushUpdatedInfo();
    }

    public Song getPrevSong() {
        Song song;
        if (getCurrentSongPos() == 0) {
            song = queue.get(queue.size() - 1);
            setCurrentSongPos(queue.size() - 1);
        } else {
            song = queue.get(getCurrentSongPos() - 1);
            setCurrentSongPos(getCurrentSongPos() - 1);
        }
        pushUpdatedInfo();
        return song;
    }

    public Song getNextSong() {
        Song song;
        if (getCurrentSongPos() == queue.size() - 1) {
            song = queue.get(0);
            setCurrentSongPos(0);
        } else {
            song = queue.get(getCurrentSongPos() + 1);
            setCurrentSongPos(getCurrentSongPos() + 1);
        }
        pushUpdatedInfo();
        return song;
    }

    public void pause() {
        player.pause();
        isPaused = true;
        pushUpdatedInfo();
    }

    public void resume() {
        if (isPaused) {
            player.start();
            isPaused = false;
        }
        pushUpdatedInfo();
    }

    public void togglePlayback() {
        if (player.isPlaying()) {
            pause();
        } else {
            resume();
        }
    }

    public boolean getPaused() {
        return isPaused;
    }

    //TPDO:Remove this
    public boolean getShowQuickToolbar() {
        Boolean show;
        if (player.isPlaying()) {
            show = true;
        } else if (isPaused) {
            show = true;
        } else {
            show = false;
        }
        return show;
    }

    public void playNext(Boolean ignoreRepeat) {
        if (!ignoreRepeat) {
            if (doRepeat) {
                playSong(getCurrentSong());
            } else {
                playSong(getNextSong());
            }
        } else {
            playSong(getNextSong());
        }
    }

    public void playPrev() {
        if (player.getCurrentPosition() >= MAX_RESTART_ON_PREV_CLICKED_DUR) {
            playSong(getCurrentSong());
        } else {
            playSong(getPrevSong());
        }
    }

    public void addToQueue(Song song) {
        queue.add(song);
        pushUpdatedInfo();
    }

    public void removeFromQueue(int position) {
        queue.remove(position);
        pushUpdatedInfo();
    }

    public List<Song> getQueue() {
        return queue;
    }

    public void setQueue(List<Song> queue) {
        this.queue = queue;
        pushUpdatedInfo();
    }

    public void setRepeat(Boolean doRepeat) {
        pushUpdatedInfo();
        this.doRepeat = doRepeat;
    }

    public boolean getRepeating() {
        return doRepeat;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //TODO:Use Reasource for Text
        Toast.makeText(cxt, "An Error has Occurred, couldn't play music", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        pushUpdatedInfo();
        player.start();
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void pushUpdatedInfo() {
        if (pushedListener != null) {
            pushedListener.onPushed();
        }
    }

    public interface UpdatePushListener {
        void onPushed();
    }

    public void setPushedListener(UpdatePushListener pushedListener) {
        this.pushedListener = pushedListener;
    }
}