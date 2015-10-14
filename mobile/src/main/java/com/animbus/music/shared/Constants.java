package com.animbus.music.shared;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.animbus.music.R;
import com.animbus.music.ui.theme.ThemeManager;

/**
 * Created by Adrian on 7/21/2015.
 */
public class Constants {
    private final static Constants i = new Constants();

    private Constants() {
    }

    public Bitmap defaultArt;

    public static Bitmap defaultArt(Context c) {
        if (i.defaultArt != null) return i.defaultArt;
        else {
            Bitmap b = ((BitmapDrawable) c.getResources().getDrawable(!ThemeManager.get().useLightTheme ? R.drawable.art_dark : R.drawable.art_light)).getBitmap();
            i.defaultArt = b;
            return b;
        }
    }
}
