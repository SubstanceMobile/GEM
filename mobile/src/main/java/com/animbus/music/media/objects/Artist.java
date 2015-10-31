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
