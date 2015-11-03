package com.animbus.music.media.objects;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;

import com.animbus.music.R;
import com.animbus.music.SettingsManager;
import com.animbus.music.media.objects.Artist;
import com.animbus.music.media.objects.Song;
import com.animbus.music.ui.theme.ThemeManager;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Adrian on 7/5/2015.
 */
public class Album {
    public List<Song> albumSongs = new ArrayList<>();

    public String albumTitle;

    public String albumArtistName;
    public Artist albumArtist;

    public long id;

    public boolean animated;
    public Context cxt;

    public Album() {
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This manages the songs of the album
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setSongs(List<Song> albumSongs) {
        this.albumSongs = albumSongs;
    }

    public List<Song> getSongs() {
        return albumSongs;
    }

    public void addSong(Song s) {
        albumSongs.add(s);
        Collections.sort(albumSongs, new Comparator<Song>() {
            @Override
            public int compare(Song lhs, Song rhs) {
                return lhs.getTrackNumber().compareTo(rhs.getTrackNumber());
            }
        });
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

    public String albumArtPath;
    public boolean defaultArt = false;
    public boolean artLoaded = false;

    public void setAlbumArtPath(String albumArtPath) {
        if (albumArtPath != null) {
            defaultArt = false;
            colorAnimated = false;
        } else {
            defaultArt = true;
            colorAnimated = true;
        }
        this.albumArtPath = "file://" + albumArtPath;
    }

    public String getAlbumArtPath() {
        return albumArtPath;
    }

    public interface ArtRequest {
        void respond(Bitmap albumArt);
    }

    public void requestArt(final ArtRequest request) {
        Picasso.with(getContext()).load(getAlbumArtPath()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                request.respond(bitmap);
                Log.d("Album " + String.valueOf(getId()), "Art Location: " + getAlbumArtPath() + " Art from: " + from.name());
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                request.respond(((BitmapDrawable) errorDrawable).getBitmap());
                Log.d("Album " + String.valueOf(getId()), "Fetching Default Art");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    public void requestArt(final ImageView imageView) {
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(getContext()).load(getAlbumArtPath()).into(imageView);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Colors
    ///////////////////////////////////////////////////////////////////////////

    public boolean colorAnimated = false;
    public static final int FRAME_COLOR = 0, TITLE_COLOR = 1, SUBTITLE_COLOR = 2;
    public int[] mainColors;
    public int[] accentColors;

    public int getBackgroundColor() {
        return mainColors[FRAME_COLOR];
    }

    public int getTitleTextColor() {
        return mainColors[TITLE_COLOR];
    }

    public int getSubtitleTextColor() {
        return mainColors[SUBTITLE_COLOR];
    }

    public int getAccentColor() {
        return accentColors[FRAME_COLOR];
    }

    public int getAccentIconColor() {
    return accentColors[TITLE_COLOR];
    }

    public int getAccentSecondaryIconColor() {
        return accentColors[SUBTITLE_COLOR];
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

    //Nothing Yet

}

