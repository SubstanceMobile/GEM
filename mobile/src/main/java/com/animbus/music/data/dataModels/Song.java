package com.animbus.music.data.dataModels;


import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;

import java.net.URI;

public class Song {
    public Integer songPosition;
    public String songTitle, songArtist, songGenre;
    public long songID, songDuration;
    Uri songURI;
    boolean repeating;

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

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public boolean isRepeating() {
        return repeating;
    }
}
