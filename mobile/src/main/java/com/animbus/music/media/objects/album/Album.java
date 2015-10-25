package com.animbus.music.media.objects.album;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
            this.albumArtPath = "file://" + albumArtPath;
        } else {
            this.albumArtPath = "default";
        }

        if (!AlbumArtHelper.isPicassoSet()) {
            Picasso.Builder builder = new Picasso.Builder(getContext());
            ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
            builder.memoryCache(new LruCache(1024 * 1024 * am.getMemoryClass() / 7));
            builder.loggingEnabled(false);

            AlbumArtHelper.setPicasso(builder.build());
        }
    }

    public String getAlbumArtPath() {
        return albumArtPath;
    }

    public interface ArtRequest {
        void respond(Bitmap albumArt);
    }

    public void requestArt(final ArtRequest request) {
        AlbumArtHelper.getPicasso(this).into(new Target() {
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
        AlbumArtHelper.getPicasso(this).into(imageView);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Colors
    ///////////////////////////////////////////////////////////////////////////

    public boolean colorAnimated = false;

    private Palette.Swatch[] swatches = null;

    public void prepareColors() {
        Log.d("Album ID:" + getId(), "Preparing Colors");
        requestArt(new ArtRequest() {
            @Override
            public void respond(Bitmap albumArt) {
                Log.d("Album ID:" + getId(), "Fetched Art for Color Extraction");
                Palette.from(albumArt).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        if (!defaultArt && SettingsManager.get().getBooleanSetting(SettingsManager.KEY_USE_PALETTE_IN_GRID, true)) {
                            //Gets main swatches
                            ArrayList<Palette.Swatch> sortedSwatches = new ArrayList<>(palette.getSwatches());
                            Collections.sort(sortedSwatches, new Comparator<Palette.Swatch>() {
                                @Override
                                public int compare(Palette.Swatch a, Palette.Swatch b) {
                                    return ((Integer) a.getPopulation()).compareTo(b.getPopulation());
                                }
                            });
                            swatches = new Palette.Swatch[]{sortedSwatches.get(sortedSwatches.size() - 1), sortedSwatches.get(0)};
                            Log.d("Album ID:" + getId(), "Prepared Colors");
                        } else {
                            Log.d("Album ID:" + getId(), "Extraction Disabled");
                        }
                    }
                });
            }
        });
    }

    public int getBackgroundColor() {
        try {
            return swatches[0].getRgb();
        } catch (NullPointerException e) {
            return getContext().getResources().getColor(
                    ThemeManager.get().useLightTheme ?
                            R.color.primaryGreyLight :
                            R.color.primaryGreyDark
            );
        }
    }

    public int getTitleTextColor() {
        try {
            return swatches[0].getTitleTextColor();
        } catch (NullPointerException e) {
            return getContext().getResources().getColor(
                    ThemeManager.get().useLightTheme ?
                            R.color.primary_text_default_material_light :
                            R.color.primary_text_default_material_dark
            );
        }
    }

    public int getSubtitleTextColor() {
        try {
            return swatches[0].getBodyTextColor();
        } catch (NullPointerException e) {
            return getContext().getResources().getColor(
                    ThemeManager.get().useLightTheme ?
                            R.color.secondary_text_default_material_light :
                            R.color.secondary_text_default_material_dark
            );
        }
    }

    public int getAccentColor() {
        try {
            return swatches[1].getRgb();
        } catch (NullPointerException e) {
            return Color.WHITE;
        }
    }

    public int getAccentIconColor() {
        try {
            return swatches[1].getTitleTextColor();
        } catch (NullPointerException e) {
            return Color.BLACK;
        }
    }

    public int getAccentSecondaryIconColor() {
        try {
            return swatches[1].getBodyTextColor();
        } catch (NullPointerException e) {
            return Color.GRAY;
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

    //Nothing Yet

}

