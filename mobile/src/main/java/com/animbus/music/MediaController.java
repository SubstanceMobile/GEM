package com.animbus.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.animbus.music.data.dataModels.SongInfoHolder;

import java.util.List;

public class MediaController implements MusicService.onStopListner {
    MusicService musicService;

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder musicBinder = (MusicService.MusicBinder) service;
            musicService = musicBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public MediaController(Context context) {
        musicService.setOnStopListner(this);
        musicService.setContext(context);
    }

    @Override
    public void onStop() {
        musicService.unbindService(musicConnection);
    }

    //This is where the song is set and the playback begins
    public void startPlayback(SongInfoHolder song) {
        musicService.playSong(song);
    }

    public void startPlayback(List<SongInfoHolder> data, int position){
        musicService.playSong(data, position);
    }

    //The Controls
    public void pausePlayback(){
        musicService.pause();
    }

    public void resumePlayback(){
        musicService.resume();
    }

    public void playNextSong(){
        musicService.playNext();
    }

    public void playPrevSong(){
        musicService.playPrev();
    }

    //Misc.
    public List<SongInfoHolder> getQueue(){
        return musicService.getQueue();
    }

    public void setQueue(List<SongInfoHolder> list){
        musicService.setQueue(list);
    }

    public void addToQueue(SongInfoHolder song) {
        musicService.addToQueue(song);
    }

    public void removeFromQueue(int position){
        musicService.removeFromQueue(position);
    }

    public void setRepeat(Boolean doRepeat) {
        musicService.setRepeat(doRepeat);
    }

}
