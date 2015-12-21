package com.animbus.music.util;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by Adrian on 12/10/2015.
 */
public class ColorUtil {

    @ColorInt
    public static int getDarkerColor(@ColorInt int color) {
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, (int) (red * 0.9), (int) (green * 0.9), (int) (blue * 0.9));
    }

    public static boolean isLightColor(@ColorInt int color) {
        return color == Color.WHITE || color != Color.BLACK && ((Color.red(color) * 0.2126f) + (Color.green(color) * 0.7152f) + (Color.blue(color) * 0.0722f)) > 160f;
    }
}
