package com.animbus.music.media;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.animbus.music.R;
import com.animbus.music.media.objects.Album;
import com.animbus.music.ui.theme.ThemeManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.Objects;

/**
 * Created by Adrian on 10/20/2015.
 */
public class AlbumArtHelper {
    private static final AlbumArtHelper i = new AlbumArtHelper();

    public static AlbumArtHelper get() {
        return i;
    }

    private AlbumArtHelper() {
    }

    public Picasso picasso;

    public static Picasso getPicasso() {
        return i.picasso;
    }

    public static void setPicasso(Picasso picasso) {
        i.picasso = picasso;
    }

    public static RequestCreator getPicasso(Album a) {
        if (!a.getAlbumArtPath().equals("default")) {
            return getPicasso().load(a.getAlbumArtPath()).error(!ThemeManager.get().useLightTheme ? R.drawable.art_dark : R.drawable.art_light);
        } else {
           return  getPicasso().load(!ThemeManager.get().useLightTheme ? R.drawable.art_dark : R.drawable.art_light);
        }
    }

    public static abstract class SimpleTarget implements Target {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            loadArt(bitmap, from);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            loadDefault();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

        public abstract void loadDefault();

        public abstract void loadArt(Bitmap art, Picasso.LoadedFrom from);
    }

    public static boolean isPicassoSet() {
        return i.picasso != null;
    }


}
