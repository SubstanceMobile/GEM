package com.animbus.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.animbus.music.data.dataModels.Song;

import java.util.List;

public class MediaController {
    MusicService musicService;
    ServiceConnection musicConnection;

    public MediaController(Context context) {
        musicService = new MusicService(context);
        /*context.startService();*/
    }

    //This is where the song is set and the playback begins
    public void startPlayback(Song song) {
        musicService.playSong(song);
    }

    public void startPlayback(List<Song> data, int position) {
        musicService.playSong(data, position);
    }

    //The Controls
    public void pausePlayback() {
        musicService.pause();
    }

    public void resumePlayback() {
        musicService.resume();
    }

    public void playNextSong() {
        musicService.playNext();
    }

    public void playPrevSong() {
        musicService.playPrev();
    }

    //Misc.
    public List<Song> getQueue() {
        return musicService.getQueue();
    }

    public void setQueue(List<Song> list) {
        musicService.setQueue(list);
    }

    public void addToQueue(Song song) {
        musicService.addToQueue(song);
    }

    public void removeFromQueue(int position) {
        musicService.removeFromQueue(position);
    }

    public void setRepeat(Boolean doRepeat) {
        musicService.setRepeat(doRepeat);
    }

}
