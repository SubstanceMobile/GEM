package com.animbus.music.data.dataModels;


public class SongInfoHolder {
    public Integer songDuration, songPosition;
    public String songTitle, songArtist, songGenre;
    public long songID;

    public Integer getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(Integer songDuration) {
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

}
