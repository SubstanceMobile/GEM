/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.afollestad.appthemeengine.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.ATEActivity;
import com.afollestad.appthemeengine.R;
import com.afollestad.appthemeengine.inflation.InflationInterceptor;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class ATEUtil {

//    @NonNull
//    public static String getIdName(@NonNull Context context, @IdRes int id) {
//        if (id == 0) return "(no id)";
//        try {
//            String name = context.getResources().getResourceName(id);
//            if (name == null || name.trim().isEmpty())
//                return "(no id)";
//            return name;
//        } catch (Throwable t) {
//            return "(no id)";
//        }
//    }

    public static int adjustAlpha(@ColorInt int color, @FloatRange(from = 0.0, to = 1.0) float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static int resolveColor(Context context, @AttrRes int attr) {
        return resolveColor(context, attr, 0);
    }

    public static int resolveColor(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, fallback);
        } finally {
            a.recycle();
        }
    }

    @ColorInt
    public static int shiftColor(@ColorInt int color, @FloatRange(from = 0.0f, to = 2.0f) float by) {
        if (by == 1f) return color;
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= by; // value component
        return Color.HSVToColor(hsv);
    }

    @ColorInt
    public static int darkenColor(@ColorInt int color) {
        return shiftColor(color, 0.9f);
    }

    public static boolean isColorLight(@ColorInt int color) {
        if (color == Color.BLACK) return false;
        else if (color == Color.WHITE || color == Color.TRANSPARENT) return true;
        final double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.4;
    }

    // optional convenience method, this can be called when we have information about the background color and want to consider it
    public static boolean isColorLight(@ColorInt int color, @ColorInt int bgColor) {
        if (Color.alpha(color) < 128) { // if the color is less than 50% visible rely on the background color
            return isColorLight(bgColor); // one could use some kind of color mixing here before passing the color
        }
        return isColorLight(color);
    }

    @ColorInt
    public static int invertColor(@ColorInt int color) {
        final int r = 255 - Color.red(color);
        final int g = 255 - Color.green(color);
        final int b = 255 - Color.blue(color);
        return Color.argb(Color.alpha(color), r, g, b);
    }

    @Nullable
    public static Toolbar getSupportActionBarView(@Nullable ActionBar ab) {
        if (ab == null) return null;
        try {
            Field field = ab.getClass().getDeclaredField("mDecorToolbar");
            field.setAccessible(true);
            ToolbarWidgetWrapper wrapper = (ToolbarWidgetWrapper) field.get(ab);
            field = ToolbarWidgetWrapper.class.getDeclaredField("mToolbar");
            field.setAccessible(true);
            return (Toolbar) field.get(wrapper);
        } catch (Throwable t) {
            Log.d("ATEUtil", "Unable to get Toolbar from " + ab.getClass().getName());
            return null;
        }
    }

    public static void setOverflowButtonColor(@NonNull Activity activity,
                                              @Nullable Toolbar toolbar,
                                              final @ColorInt int color) {
        if (toolbar != null && toolbar.getTag() != null && ATE.IGNORE_TAG.equals(toolbar.getTag()))
            return; // ignore tag was set, don't update the overflow
        final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final View target = toolbar != null ? toolbar :
                (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = target.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<>();
                target.findViewsWithText(outViews, overflowDescription,
                        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) return;
                final AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
                overflow.setImageDrawable(TintHelper.createTintedDrawable(overflow.getDrawable(), color));
                removeOnGlobalLayoutListener(target, this);
            }
        });
    }

    @SuppressWarnings("deprecation")
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    @SuppressWarnings("deprecation")
    public static void setBackgroundCompat(@NonNull View view, @Nullable Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            view.setBackground(drawable);
        else view.setBackgroundDrawable(drawable);
    }

    public static int stripAlpha(@ColorInt int color) {
        return Color.rgb(Color.red(color), Color.green(color), Color.blue(color));
    }

    public static boolean isInClassPath(@NonNull String clsName) {
        try {
            return inClassPath(clsName) != null;
        } catch (Throwable t) {
            return false;
        }
    }

    public static Class<?> inClassPath(@NonNull String clsName) {
        try {
            return Class.forName(clsName);
        } catch (Throwable t) {
            throw new IllegalStateException(String.format("%s is not in your class path! You must include the associated library.", clsName));
        }
    }

    /**
     * Taken from CollapsingToolbarLayout's CollapsingTextHelper class.
     */
    public static int blendColors(int color1, int color2, float ratio) {
        final float inverseRatio = 1f - ratio;
        float a = (Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio);
        float r = (Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio);
        float g = (Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio);
        float b = (Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    public static void setInflaterFactory(LayoutInflater li, Activity activity) {
        LayoutInflaterCompat.setFactory(li, new InflationInterceptor(
                activity instanceof ATEActivity ? (ATEActivity) activity : null,
                li,
                activity instanceof AppCompatActivity ? ((AppCompatActivity) activity).getDelegate() : null));
    }

    public interface LayoutCallback {
        void onLayout(View view);
    }

    public static void waitForLayout(final View view, final LayoutCallback callback) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeLayoutListener(view, this);
                if (callback != null)
                    callback.onLayout(view);
            }
        });
    }

    private static void removeLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        } else {
            //noinspection deprecation
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }

    private ATEUtil() {
    }
}