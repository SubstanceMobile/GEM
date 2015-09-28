package com.animbus.music.media.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.animbus.music.R;
import com.animbus.music.SettingsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 7/5/2015.
 */
public class Album {
    List<Song> albumSongs = new ArrayList<>();

    String albumTitle;

    String albumArtistName;
    Artist albumArtist;

    Bitmap albumArt;

    long id;

    public boolean colorAnimated;
    public boolean animated;
    public boolean defaultArt = false;
    public int BackgroundColor;
    public int TitleTextColor;
    public int SubtitleTextColor;
    public int accentColor;
    public int accentIconColor;
    public int darkPrimary;
    public Context cxt;

    public Album(){}

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This manages the songs of the album
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setSongs(List<Song> albumSongs) {
        this.albumSongs = albumSongs;
    }

    public List<Song> getSongs() {
        return albumSongs;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This manages the info of the album in strings
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This handles the Album Art
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }

    public Bitmap getAlbumArt() {
        if (albumArt != null){
            return albumArt;
        } else {
            return getDefaultArt();
        }
    }

    public Bitmap getDefaultArt() {
        defaultArt = true;
        if (!SettingsManager.get().getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)){
            return ((BitmapDrawable) cxt.getResources().getDrawable(R.drawable.art_dark)).getBitmap();
        } else {
            return ((BitmapDrawable) cxt.getResources().getDrawable(R.drawable.art_light)).getBitmap();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This handles the album artist
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setAlbumArtistName(String albumArtistName) {
        this.albumArtistName = albumArtistName;
    }

    public String getAlbumArtistName() {
        return albumArtistName;
    }

    public void setArtist(Artist albumArtist) {
        this.albumArtist = albumArtist;
    }

    public Artist getArtist() {
        return albumArtist;
    }

    ///////////////////////////////////////////////////////////////////////////
    // ID
    ///////////////////////////////////////////////////////////////////////////

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Context
    ///////////////////////////////////////////////////////////////////////////

    public void setContext(Context cxt) {
        this.cxt = cxt;
    }

    public Context getContext() {
        return cxt;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Misc. Methods
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //None...

}

