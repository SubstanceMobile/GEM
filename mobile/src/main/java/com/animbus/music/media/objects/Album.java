package com.animbus.music.media.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;

import com.animbus.music.shared.Constants;
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
    public int BackgroundColor;
    public int TitleTextColor;
    public int SubtitleTextColor;
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
    public Bitmap albumArt;
    public ArrayList<ArtRequest> artRequests = new ArrayList<>();

    public void setAlbumArtPath(String albumArtPath) {
        this.albumArtPath = "file://" + albumArtPath;
        loadColors();
        prepareArt();
    }

    public String getAlbumArtPath() {
        return albumArtPath;
    }

    public void prepareArt() {
        if (!artLoaded) {
            Picasso.with(getContext()).load(getAlbumArtPath()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    defaultArt = false;
                    artLoaded = true;
                    albumArt = bitmap;
                    for (ArtRequest artRequest : artRequests) artRequest.respond(bitmap);
                    artRequests.clear();
                    Log.d("Album " + String.valueOf(getId()), "Art Loaded from " + getAlbumArtPath());
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    defaultArt = true;
                    artLoaded = true;
                    albumArt = Constants.defaultArt(getContext());
                    for (ArtRequest request : artRequests)
                        request.respond(Constants.defaultArt(getContext()));
                    artRequests.clear();
                    Log.d("Album " + String.valueOf(getId()), "Fetching Default Art");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }

    public void addListener(ArtRequest request) {
        artRequests.add(request);
    }

    public interface ArtRequest {
        void respond(Bitmap albumArt);
    }

    public void requestArt(ArtRequest request) {
        if (artLoaded) request.respond(albumArt);
        else {
            addListener(request);
            prepareArt();
        }
    }

    public void requestArt(final ImageView imageView) {
        if (artLoaded) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(albumArt);
        } else {
            addListener(new ArtRequest() {
                @Override
                public void respond(Bitmap albumArt) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setImageBitmap(albumArt);
                    prepareArt();
                }
            });
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
            loadColors();
        }
    }

    private void loadColors() {
        if (!colorsLoaded) {
            //color extraction enabled
            if (!defaultArt) {
                //album art is compatible
                requestArt(new Album.ArtRequest() {
                    @Override
                    public void respond(Bitmap albumArt) {
                        Palette.from(albumArt).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch swatch = getMainSwatch(palette.getSwatches())[0];
                                Palette.Swatch accentSwatch = getMainSwatch(palette.getSwatches())[1];
                                BackgroundColor = swatch.getRgb();
                                TitleTextColor = swatch.getTitleTextColor();
                                SubtitleTextColor = swatch.getBodyTextColor();
                                accentColor = accentSwatch.getRgb();
                                accentIconColor = accentSwatch.getTitleTextColor();
                                accentSecondaryIconColor = accentSwatch.getBodyTextColor();

                                for (ColorsRequest request : colorRequests) request.respond();

                                colorsLoaded = true;
                            }
                        });
                    }
                });
            }
        }
    }

    private Palette.Swatch[] getMainSwatch(List<Palette.Swatch> swatches) {
        ArrayList<Palette.Swatch> sortedSwatches = new ArrayList<>(swatches);
        Collections.sort(sortedSwatches, new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch a, Palette.Swatch b) {
                return ((Integer) a.getPopulation()).compareTo(b.getPopulation());
            }
        });
        return new Palette.Swatch[]{sortedSwatches.get(sortedSwatches.size() - 1), sortedSwatches.get(0)};
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Misc. Methods
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Nothing Yet

}

