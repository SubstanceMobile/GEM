/*
 * Copyright (C) 2016 Substance Mobile
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
 */

package com.animbus.music.media.objects;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Adrian on 7/5/2015.
 */
public class Artist {
    String artistName;
    String artistBio;

    List<Album> artistAlbums;
    List<Song> artistSongs;

    Bitmap artistImage;

    public Artist(){}

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
