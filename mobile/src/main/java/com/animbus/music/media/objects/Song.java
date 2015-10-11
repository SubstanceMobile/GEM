package com.animbus.music.media.objects;


import android.content.ContentUris;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;

/**
 * Created by Adrian on 7/5/2015.
 */
public class Song {
    public Integer trackNumber;
    public String songTitle, songArtist, songGenre;
    public long songID, songDuration;
    Uri songURI;
    boolean repeating;
    Album songAlbum;
    long albumID;
    String songDurString = "";

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

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
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

    public String getSongDurString() {
        if (!songDurString.equals("")) {
            return songDurString;
        } else {
            songDurString = stringForTime(getSongDuration());
            return songDurString;
        }
    }

    private String stringForTime(long timeMs) {
        int totalSeconds = (int) timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return  String.format("%02d:%02d", minutes, seconds);
        }
    }

    public void setSongDurString(String songDurString) {
        this.songDurString = songDurString;
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

    public MediaMetadataCompat getMetaData(){
        final MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putText(MediaMetadataCompat.METADATA_KEY_TITLE, getSongTitle());
        builder.putText(MediaMetadataCompat.METADATA_KEY_ARTIST, getSongArtist());
        builder.putText(MediaMetadataCompat.METADATA_KEY_ALBUM, getAlbum().getAlbumTitle());
        getAlbum().requestArt(new Album.AlbumArtState() {
            @Override
            public void respond(Bitmap albumArt) {
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
            }
        });
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, getSongDuration());
        return builder.build();
    }
}
