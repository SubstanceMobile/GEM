package com.animbus.music.media.objects;


import android.content.ContentUris;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;

import com.animbus.music.media.MediaNotification;

import java.util.Queue;

/**
 * Created by Adrian on 7/5/2015.
 */
public class Song {
    public Integer songPosition;
    public String songTitle, songArtist, songGenre;
    public long songID, songDuration;
    Uri songURI;
    boolean repeating;
    Album songAlbum;
    long albumID;

    public Song() {

    }

    public Uri getSongURI() {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songID);
    }

    public long getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(long songDuration) {
        this.songDuration = songDuration;
    }

    public Integer getSongPosition() {
        return songPosition;
    }

    public void setSongPosition(Integer songPosition) {
        this.songPosition = songPosition;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public long getSongID() {
        return songID;
    }

    public void setSongID(long songID) {
        this.songID = songID;
    }

    public String getSongGenre() {
        return songGenre;
    }

    public void setSongGenre(String songGenre) {
        this.songGenre = songGenre;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public void setAlbum(Album songAlbum) {
        this.songAlbum = songAlbum;
    }

    public Album getAlbum() {
        return songAlbum;
    }

    public QueueItem toQueueItem(){
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setTitle(getSongTitle())
                .setDescription(getSongArtist())
                .setIconBitmap(null)
                .setMediaId(String.valueOf(getSongID()))
                .build();
        return new QueueItem(description, 1);
    }

    public static Song parse(QueueItem queueItem){
        Song song = new Song();
        MediaDescriptionCompat data = queueItem.getDescription();
        song.setSongTitle(String.valueOf((data.getTitle())));
        song.setSongArtist(String.valueOf(data.getDescription()));
        song.setSongID(Long.valueOf(data.getMediaId()));
        return song;
    }

    //TODO:Proper impl of this
    public static Song getFromID(long id){
        Song i =  new Song();
        i.setSongID(id);
        return i;
    }

    public long getAlbumID() {
        return albumID;
    }

    public Album getSongAlbum() {
        return songAlbum;
    }

    public void setAlbumID(long albumID) {
        this.albumID = albumID;
    }

    public void setSongAlbum(Album songAlbum) {
        this.songAlbum = songAlbum;
    }
}
