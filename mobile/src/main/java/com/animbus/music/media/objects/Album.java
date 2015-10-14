package com.animbus.music.media.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.animbus.music.shared.Constants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String albumArtPath;
    public boolean defaultArt = false;
    public boolean artLoaded = false;
    public Bitmap albumArt;
    public ArrayList<ArtRequest> artRequests = new ArrayList<>();

    public void setAlbumArtPath(String albumArtPath) {
        this.albumArtPath = "file://" + albumArtPath;
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
                    for (ArtRequest request  : artRequests) request.respond(Constants.defaultArt(getContext()));
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
                }
            });
            prepareArt();
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

