/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.animbus.music.media.objects;

import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;

import com.animbus.music.media.Library;
import com.animbus.music.util.GEMUtil;

import static android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER;

/**
 * Wrapper around a MediaMetadataCompat optimised for Song metadata
 */
public class Song extends MediaObject {
    public long ID, albumID;

    ///////////////////////////////////////////////////////////////////////////
    // Uri
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected Uri getBaseUri() {
        return EXTERNAL_CONTENT_URI;
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
        long track = data.getLong(METADATA_KEY_TRACK_NUMBER);
        return track != 0 ? String.valueOf(track) : "-";
    }

    public Song setTrackNumber(long trackNumber) {
        putLong(METADATA_KEY_TRACK_NUMBER, trackNumber);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Album
    ///////////////////////////////////////////////////////////////////////////

    public long getAlbumID() {
        return albumID;
    }

    public Song setAlbumID(long albumID) {
        this.albumID = albumID;
        return this;
    }

    public Album getAlbum() {
        return Library.findAlbumById(getAlbumID());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Misc
    ///////////////////////////////////////////////////////////////////////////

    public QueueItem toQueueItem() {
        return new QueueItem(data.getDescription(), getID());
    }

    @Deprecated
    public static Song parse() {
        //Do nothing. This method will be removed soon
        return new Song();
    }


}
