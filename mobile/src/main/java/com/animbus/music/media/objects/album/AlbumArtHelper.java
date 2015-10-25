package com.animbus.music.media.objects.album;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.animbus.music.R;
import com.animbus.music.ui.theme.ThemeManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

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
            AlbumColorHelper.setDefaults(a);
            a.defaultArt = true;
            a.colorsLoaded = true;
            a.colorAnimated = true;
            return getPicasso().load(!ThemeManager.get().useLightTheme ? R.drawable.art_dark : R.drawable.art_light);
        }
    }

    public static boolean isPicassoSet() {
        return i.picasso != null;
    }

}
