package com.animbus.music.media;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.animbus.music.R;
import com.animbus.music.media.objects.Album;
import com.animbus.music.ui.theme.ThemeManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.RequestHandler;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
            a.defaultArt = false;
            a.colorsLoaded = false;
            a.colorAnimated = false;

            return getPicasso().load(a.getAlbumArtPath()).error(!ThemeManager.get().useLightTheme ? R.drawable.art_dark : R.drawable.art_light);
        } else {
            a.backgroundColor = a.getContext().getResources().getColor(
                    ThemeManager.get().useLightTheme ?
                            R.color.primaryGreyLight : R.color.primaryGreyDark);
            a.titleTextColor = a.getContext().getResources().getColor(
                    ThemeManager.get().useLightTheme ?
                            R.color.primary_text_default_material_light :
                            R.color.primary_text_default_material_dark
            );
            a.subtitleTextColor = a.getContext().getResources().getColor(
                    ThemeManager.get().useLightTheme ?
                            R.color.secondary_text_default_material_light :
                            R.color.secondary_text_default_material_dark
            );
            a.accentColor = Color.WHITE;
            a.accentIconColor = Color.BLACK;
            a.accentSecondaryIconColor = Color.GRAY;

            a.defaultArt = true;
            a.colorsLoaded = true;
            a.colorAnimated = true;
            return getPicasso().load(!ThemeManager.get().useLightTheme ? R.drawable.art_dark : R.drawable.art_light);
        }
    }

    public static void loadColors(final Album a) {

    }

    public static abstract class ColorTarget implements Target {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            loadColors(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

        public abstract void loadColors(Bitmap art);
    }

    public static boolean isPicassoSet() {
        return i.picasso != null;
    }

}
