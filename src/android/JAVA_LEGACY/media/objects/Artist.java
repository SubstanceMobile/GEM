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

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.List;

/**
 * Created by Adrian on 7/5/2015.
 */
public class Artist extends MediaObject{
    String artistName;
    String artistBio;

    List<Album> artistAlbums;
    List<Song> artistSongs;

    Bitmap artistImage;

    public Artist(){}

    @Override
    protected Uri getBaseUri() {
        return MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This manages the strings
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setName(String artistName) {
        this.artistName = artistName;
    }

    public void setArtistBio(String artistBio) {
        this.artistBio = artistBio;
    }

    public String getName() {
        return artistName;
    }

    public String getBio() {
        return artistBio;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This manages the image
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setArtistImage(Bitmap artistImage) {
        this.artistImage = artistImage;
    }

    public Bitmap getArtistImage() {
        return artistImage;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This manages all of the lists
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setArtistAlbums(List<Album> artistAlbums) {
        this.artistAlbums = artistAlbums;
    }

    public List<Album> getArtistAlbums() {
        return artistAlbums;
    }

    public void setArtistSongs(List<Song> artistSongs) {
        this.artistSongs = artistSongs;
    }

    public List<Song> getArtistSongs() {
        return artistSongs;
    }
}
