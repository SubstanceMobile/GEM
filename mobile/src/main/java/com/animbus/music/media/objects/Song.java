package com.animbus.music.media.objects;

import android.content.ContentUris;
import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;

import com.animbus.music.media.Library;
import com.animbus.music.util.GEMUtil;

import static android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER;

/**
 * Wrapper around a MediaMetadataCompat optimised for Song metadata
 */
public class Song extends MediaObject {
    public long ID, albumID;

    ///////////////////////////////////////////////////////////////////////////
    // Uri
    ///////////////////////////////////////////////////////////////////////////

    public Uri getSongURI() {
        return ContentUris.withAppendedId(EXTERNAL_CONTENT_URI, getId());
    }


    ///////////////////////////////////////////////////////////////////////////
    // Title
    ///////////////////////////////////////////////////////////////////////////

    public String getSongTitle() {
        return data.getString(METADATA_KEY_TITLE);
    }

    public Song setSongTitle(String songTitle) {
        putString(METADATA_KEY_TITLE, songTitle);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Artist
    ///////////////////////////////////////////////////////////////////////////

    public String getSongArtist() {
        return data.getString(METADATA_KEY_ARTIST);
    }

    public Song setSongArtist(String songArtist) {
        putString(METADATA_KEY_ARTIST, songArtist);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // IDs
    ///////////////////////////////////////////////////////////////////////////

    public long getId() {
        return ID;
    }

    public Song setId(long id) {
        this.ID = id;
        return this;
    }

    public long getAlbumID() {
        return albumID;
    }

    public Song setAlbumID(long albumID) {
        this.albumID = albumID;
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Song Duration
    ///////////////////////////////////////////////////////////////////////////

    public long getSongDuration() {
        return data.getLong(METADATA_KEY_DURATION);
    }

    public Song setSongDuration(long songDuration) {
        putLong(METADATA_KEY_DURATION, songDuration);
        return this;
    }

    public String getSongDurString() {
        return GEMUtil.stringForTime(getSongDuration());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Track Number
    ///////////////////////////////////////////////////////////////////////////

    public String getTrackNumberString() {
        return getTrackNumber() != 0 ? String.valueOf(getTrackNumber()) : "-";
    }

    public long getTrackNumber() {
        return data.getLong(METADATA_KEY_TRACK_NUMBER);
    }

    public Song setTrackNumber(long trackNumber) {
        putLong(METADATA_KEY_TRACK_NUMBER, trackNumber);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Album
    ///////////////////////////////////////////////////////////////////////////

    public Album getAlbum() {
        return Library.findAlbumById(getAlbumID());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Misc
    ///////////////////////////////////////////////////////////////////////////

    public QueueItem toQueueItem() {
        return new QueueItem(data.getDescription(), getId());
    }

    @Deprecated
    public static Song parse() {
        //Do nothing. This method will be removed soon
        return new Song();
    }
}
