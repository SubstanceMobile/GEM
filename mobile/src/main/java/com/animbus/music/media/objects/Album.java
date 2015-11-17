package com.animbus.music.media.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import com.animbus.music.R;
import com.animbus.music.ui.activity.theme.ThemeManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

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
        Glide.with(getContext().getApplicationContext()).load(getAlbumArtPath())
                .asBitmap()
                .placeholder(!ThemeManager.get().useLightTheme ? R.drawable.art_dark : R.drawable.art_light)
                .animate(android.R.anim.fade_in)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        request.respond(resource);
                    }
                });
    }

    public void requestArt(ImageView imageView) {
        Glide.with(imageView.getContext()).load(getAlbumArtPath())
                .placeholder(!ThemeManager.get().useLightTheme ? R.drawable.art_dark : R.drawable.art_light)
                .animate(android.R.anim.fade_in)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .into(imageView);
    }

    public void requestArt(ImageView imageView, RequestListener<String, GlideDrawable> listener) {
        Glide.with(imageView.getContext()).load(getAlbumArtPath())
                .placeholder(!ThemeManager.get().useLightTheme ? R.drawable.art_dark : R.drawable.art_light)
                .animate(android.R.anim.fade_in)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .listener(listener)
                .into(imageView);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Colors
    ///////////////////////////////////////////////////////////////////////////

    public boolean colorAnimated = false;
    public static final int FRAME_COLOR = 0, TITLE_COLOR = 1, SUBTITLE_COLOR = 2;
    public int[] mainColors;
    public int[] accentColors;
    public boolean colorsLoaded = false;

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
        mainColors = new int[] {
                cxt.getResources().getColor(!ThemeManager.get().useLightTheme ? R.color.primaryGreyDark : R.color.primaryLight),
                cxt.getResources().getColor(!ThemeManager.get().useLightTheme ? R.color.primary_text_default_material_dark : R.color.primary_text_default_material_light),
                cxt.getResources().getColor(!ThemeManager.get().useLightTheme ? R.color.secondary_text_default_material_dark : R.color.secondary_text_default_material_light)
        };
        accentColors = new int[] {
                Color.BLACK, Color.WHITE, Color.GRAY
        };
    }

    public Context getContext() {
        return cxt;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Misc. Methods
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Nothing Yet

}

