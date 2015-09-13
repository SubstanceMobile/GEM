/*
package com.animbus.music.media.old;

import android.content.Context;

import com.animbus.music.media.MediaNotification;
import com.animbus.music.media.QueueManager;
import com.animbus.music.media.objects.Song;

import java.util.ArrayList;
import java.util.List;

public class MediaController  implements MusicService.UpdatePushListener{
    MusicService musicService;
    ArrayList<OnUpdateListener> listeners = new ArrayList<>();
    Context cxt;

    private static MediaController instance = new MediaController();
    public static MediaController getInstance() {
        return instance;
    }

    private MediaController(){

    }

    public MediaController setContext(Context context){
        musicService = new MusicService(context);
        cxt = context;
        musicService.setPushedListener(this);
        return this;
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
        void onUpdate(Song currentSong, Boolean isPaused, Boolean isRepeating, Boolean isShuffled, List<com.animbus.music.media.objects.Song> currentQueue);
    }

    public void addOnUpdateListener(OnUpdateListener onUpdateListener) {
        listeners.add(onUpdateListener);
    }

    public void requestUpdate(){
        for (OnUpdateListener listener : listeners) {
            listener.onUpdate(musicService.getCurrentSong(), musicService.getPaused(), musicService.getRepeating(), null, QueueManager.get().getCurrentQueueAsSong());
        }
    }

    @Override
    public void onPushed() {
        requestUpdate();
    }
}
*/
