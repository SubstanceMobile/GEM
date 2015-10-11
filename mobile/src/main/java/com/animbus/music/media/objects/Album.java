package com.animbus.music.media.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.animbus.music.R;
import com.animbus.music.ui.theme.ThemeManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 7/5/2015.
 */
public class Album {
    public List<Song> albumSongs = new ArrayList<>();

    public String albumTitle;

    public String albumArtistName;
    public Artist albumArtist;

    public Bitmap albumArt;
    public String albumArtPath;
    public boolean artLoaded = false;
    public boolean defaultArt = false;

    public long id;

    public boolean colorAnimated;
    public boolean animated;
    public int BackgroundColor;
    public int TitleTextColor;
    public int SubtitleTextColor;
    public int accentColor;
    public int accentIconColor;
    public int darkPrimary;
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
    ////////////////////////////1                                                           //////////////////////////////////////////////////////////////////////////////////

    private void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
        for (AlbumArtState listener : artStateListeners) listener.respond(albumArt);
        defaultArt = false;
        artLoaded = true;
    }

    private Bitmap getAlbumArt() {
        return albumArt;
    }

    private void setDefaultArt() {
        Bitmap art = !ThemeManager.get().useLightTheme ? ((BitmapDrawable) cxt.getResources().getDrawable(R.drawable.art_dark)).getBitmap() : ((BitmapDrawable) cxt.getResources().getDrawable(R.drawable.art_light)).getBitmap();
        setAlbumArt(art);
        defaultArt = true;
    }

    public void setAlbumArtPath(String albumArtPath) {
        this.albumArtPath = albumArtPath;
    }

    public void buildArt() {
        try {
            Picasso.with(getContext()).load(new File(getAlbumArtPath())).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    setAlbumArt(bitmap);
                    artLoaded = true;
                    Log.d("Album " + String.valueOf(getId()), "Art Built");
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } catch (NullPointerException e) {
            setDefaultArt();
            Log.d("Album " + String.valueOf(getId()), "No Album Art");
        }
    }

    public String getAlbumArtPath() {
        return albumArtPath;
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


    ///////////////////////////////////////////////////////////////////////////
    // Album Art Changed Listener
    ///////////////////////////////////////////////////////////////////////////

    ArrayList<AlbumArtState> artStateListeners = new ArrayList<>();

    private void addStateListener(AlbumArtState listener) {
        this.artStateListeners.add(listener);
    }

    public interface AlbumArtState {
        void respond(Bitmap albumArt);
    }

    public void requestArt(AlbumArtState stateListener) {
        if (artLoaded) stateListener.respond(getAlbumArt());
        else {
            addStateListener(stateListener);
        }
    }

    public void requestArt(final ImageView imageView) {
        requestArt(new AlbumArtState() {
            @Override
            public void respond(Bitmap albumArt) {
                imageView.setImageBitmap(albumArt);
            }
        });
    }

}

