package com.animbus.music.media.objects;

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
import com.animbus.music.media.AlbumArtHelper;
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

    public boolean colorAnimated = false;
    public boolean animated;
    public int backgroundColor;
    public int titleTextColor;
    public int subtitleTextColor;
    public int accentColor;
    public int accentIconColor;
    public int accentSecondaryIconColor;
    boolean colorsLoaded;
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
            builder.memoryCache(new LruCache(1024 * 1024 * am.getMemoryClass() / 5));
            builder.loggingEnabled(true);
            AlbumArtHelper.setPicasso(builder.build());
        }

        AlbumArtHelper.getPicasso(this).into(new AlbumArtHelper.SimpleTarget() {

            @Override
            public void loadDefault() {
                defaultArt = true;
                artLoaded = true;

                backgroundColor = getContext().getResources().getColor(ThemeManager.get().useLightTheme ? R.color.primaryGreyLight : R.color.primaryGreyDark);
                titleTextColor = getContext().getResources().getColor(ThemeManager.get().useLightTheme ? R.color.primary_text_default_material_light : R.color.primary_text_default_material_dark);
                subtitleTextColor = getContext().getResources().getColor(ThemeManager.get().useLightTheme ? R.color.secondary_text_default_material_light : R.color.secondary_text_default_material_dark);
                accentColor = Color.WHITE;
                accentIconColor = Color.BLACK;
                accentSecondaryIconColor = Color.GRAY;

                for (ColorsRequest request : colorRequests) request.respond();
                colorRequests.clear();

                colorsLoaded = true;
            }

            @Override
            public void loadArt(Bitmap art, Picasso.LoadedFrom from) {
                defaultArt = false;
                artLoaded = true;

                if (!colorsLoaded) {
                    Palette.from(art).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            //Gets main swatches
                            ArrayList<Palette.Swatch> sortedSwatches = new ArrayList<>(palette.getSwatches());
                            Collections.sort(sortedSwatches, new Comparator<Palette.Swatch>() {
                                @Override
                                public int compare(Palette.Swatch a, Palette.Swatch b) {
                                    return ((Integer) a.getPopulation()).compareTo(b.getPopulation());
                                }
                            });
                            Palette.Swatch[] swatches =
                                    new Palette.Swatch[]{sortedSwatches.get(sortedSwatches.size() - 1), sortedSwatches.get(0)};

                            //Color management
                            Palette.Swatch swatch = swatches[0];
                            Palette.Swatch accentSwatch = swatches[1];
                            backgroundColor = swatch.getRgb();
                            titleTextColor = swatch.getTitleTextColor();
                            subtitleTextColor = swatch.getBodyTextColor();
                            accentColor = accentSwatch.getRgb();
                            accentIconColor = accentSwatch.getTitleTextColor();
                            accentSecondaryIconColor = accentSwatch.getBodyTextColor();

                            for (ColorsRequest request : colorRequests) request.respond();
                            colorRequests.clear();

                            colorsLoaded = true;
                        }
                    });
                }
            }
        });

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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Colors
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ArrayList<ColorsRequest> colorRequests = new ArrayList<>();

    public interface ColorsRequest {
        void respond();

    }

    public void requestColors(ColorsRequest request) {
        if (colorsLoaded) request.respond();
        else {
            colorRequests.add(request);
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

