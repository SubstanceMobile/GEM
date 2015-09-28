package com.animbus.music.ui.albumDetails;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by Adrian on 9/20/2015.
 */
public class FabHelper {

    public static void setFabBackground(FloatingActionButton fab, int color){
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public static void setFabTintedIcon(FloatingActionButton fab, Drawable icon, int color){
        DrawableCompat.setTint(icon, color);
        fab.setImageDrawable(icon);
    }

    public static void setRippleColor(FloatingActionButton fab, int color){
        fab.setRippleColor(color);
    }

}
