package com.animbus.music;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.animbus.music.activities.NowPlaying;
import com.animbus.music.data.objects.Song;

import java.util.List;

public class MediaController  implements MusicService.UpdatePushListener{
    MusicService musicService;
    OnUpdateListener onUpdateListener;
    Context cxt;

    private static MediaController instance = new MediaController();
    public static MediaController getInstance() {
        return instance;
    }

    private MediaController(){

    }

    public void setContext(Context context){
        musicService = new MusicService(context);
        cxt = context;
        musicService.setPushedListener(this);
    }

    //This is where the song is set and the playback begins
    public void startPlayback(Song song) {
        musicService.setCurrentSongPos(song);
        musicService.playSong(song);
    }

    public void startPlayback(List<Song> data, int position) {
        musicService.setCurrentSongPos(position);
        musicService.playSong(data, position);
    }

    //The Controls
    public void pausePlayback() {
        musicService.pause();
    }

    public void resumePlayback() {
        musicService.resume();
    }

    public void togglePlayback(){
        musicService.togglePlayback();
    }

    public void playNextSong() {
        musicService.playNext(true);
    }

    public void playPrevSong() {
        musicService.playPrev();
    }

    //Misc.
    public void setQueue(List<Song> list) {
        musicService.setQueue(list);
    }

    public List<Song> getQueue() {
        return musicService.getQueue();
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

    public boolean getShowToolbar(){
        return musicService.getShowQuickToolbar();
    }

    public boolean getPlaying(){
        return musicService.getPlaying();
    }

    //Change Listener
    public interface OnUpdateListener {
        void onUpdate(Song currentSong, Boolean isPaused, Boolean isRepeating, Boolean isShuffled, List<Song> currentQueue);
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public void requestUpdate(){
        if (onUpdateListener != null){
            //TODO:Add shuffle
            onUpdateListener.onUpdate(musicService.getCurrentSong(), musicService.getPaused(), musicService.getRepeating(), null, getQueue());
        }
    }

    @Override
    public void onPushed() {
        requestUpdate();
    }
}
