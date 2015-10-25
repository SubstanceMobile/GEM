package com.animbus.music.media.objects.album;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.widget.TextView;

import com.animbus.music.R;
import com.animbus.music.SettingsManager;
import com.animbus.music.data.adapter.AlbumGridAdapter;
import com.animbus.music.ui.theme.ThemeManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * Helper Class to manage colors from an album
 */
public class AlbumColorHelper {

    private AlbumColorHelper() {
    }

    private static boolean isEnabled() {
        //Color extraction is broken
        //TODO Fix this
        /*return SettingsManager.get().getBooleanSetting(SettingsManager.KEY_USE_PALETTE_IN_GRID, true);*/
        return false;
    }

    private static boolean doLoadFromSetting(Album a) {
        return SettingsManager.get().getBooleanSetting(SettingsManager.KEY_ALBUM_COLOR_AT_ + a.getId(), false);
    }

    /**
     * Sets the default colors into the album
     *
     * @param a album onto which the default colors will be set
     */
    public static void setDefaults(Album a) {
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
        Log.d("Album ID: " + a.getId(), "Using default colors");
    }

    /**
     * Uses {@link Palette} to generate colors for this album after making checks for settings and default art.
     *
     * @param a        album onto which colors will be loaded
     * @param listener the callback that triggers when the colors are generated.
     */
    public static void loadColors(final Album a, final ColorRequest listener) {
        if (!isEnabled() || a.defaultArt) {
            //Color extraction is disabled or the art is default
            setDefaults(a);
            if (listener != null) listener.onLoad();
        } else if (doLoadFromSetting(a)) {
            //Load colors from setting
            loadFromSetting(a, listener);
        } else if (!a.colorsLoaded) {
            //Extract colors
            AlbumArtHelper.getPicasso(a).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
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

                            try {
                                //Load colors
                                a.backgroundColor = swatches[0].getRgb();
                                a.titleTextColor = swatches[0].getTitleTextColor();
                                a.subtitleTextColor = swatches[0].getBodyTextColor();
                            } catch (NullPointerException e) {
                                //Loads default colors
                                a.backgroundColor = a.getContext().getResources().getColor(
                                        ThemeManager.get().useLightTheme ?
                                                R.color.primaryGreyLight :
                                                R.color.primaryGreyDark
                                );
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
                            }

                            try {
                                //Load colors
                                a.accentColor = swatches[1].getRgb();
                                a.accentIconColor = swatches[1].getTitleTextColor();
                                a.accentSecondaryIconColor = swatches[1].getBodyTextColor();
                            } catch (NullPointerException e) {
                                //Loads default colors
                                a.accentColor = Color.WHITE;
                                a.accentIconColor = Color.BLACK;
                                a.accentSecondaryIconColor = Color.GRAY;
                            }
                            Log.d("Album ID: " + a.getId(), "Colors Generated");
                            if (listener != null) listener.onLoad();
                        }
                    });
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            Log.d("Album ID: " + a.getId(), "Colors are already loaded");
            if (listener != null) listener.onLoad();
        }
    }

    /**
     * Uses {@link Palette} to generate colors for this album after making checks for settings and default art.
     *
     * @param a album onto which colors will be loaded
     */
    public static void loadColors(Album a) {
        loadColors(a, null);
    }

    /**
     * <b>Warning:</b> unfinished code. May misbehave
     * <p/>
     * Loads album colors from a settings that will be stored at {@link SettingsManager} using {@link com.animbus.music.ui.settings.AlbumColor}
     *
     * @param a The album onto which the colors will be set
     */
    private static void loadFromSetting(Album a, ColorRequest listener) {
        a.backgroundColor = SettingsManager.get().getIntSetting("" + String.valueOf(a.getId()), Color.WHITE);
        a.titleTextColor = SettingsManager.get().getIntSetting("" + String.valueOf(a.getId()), Color.WHITE);
        a.subtitleTextColor = SettingsManager.get().getIntSetting("" + String.valueOf(a.getId()), Color.WHITE);

        a.accentColor = SettingsManager.get().getIntSetting("" + String.valueOf(a.getId()), Color.WHITE);
        a.accentIconColor = SettingsManager.get().getIntSetting("" + String.valueOf(a.getId()), Color.WHITE);
        a.accentSecondaryIconColor = SettingsManager.get().getIntSetting("" + String.valueOf(a.getId()), Color.WHITE);

        if (listener != null) listener.onLoad();
        Log.d("Album ID: " + a.getId(), "Colors Genarated From Setting");
    }

    public interface ColorRequest {
        void onLoad();
    }

    ///////////////////////////////////////////////////////////////////////////
    // All of the stuff for loading colors into the AlbumGridAdapter
    ///////////////////////////////////////////////////////////////////////////


    private static final int COLOR_DUR = 300;
    private static final int COLOR_DELAY_BASE = 550;
    private static final int COLOR_DELAY_MAX = 750;
    public static final int TYPE_BACK = 1, TYPE_TITLE = 2, TYPE_SUBTITLE = 3;
    static final Property<TextView, Integer> textColor = new Property<TextView, Integer>(int.class, "textColor") {
        @Override
        public Integer get(TextView object) {
            return object.getCurrentTextColor();
        }

        @Override
        public void set(TextView object, Integer value) {
            object.setTextColor(value);
        }
    };

    public static void into(final Album album, final View background, final TextView title, final TextView subtitle,
                            final int position, final Context c, final AlbumGridAdapter adapter) {
        loadColors(album, new ColorRequest() {
            @Override
            public void onLoad() {
                if (!album.colorAnimated) {
                    Random colorDelayRandom = new Random();
                    int MAX = COLOR_DELAY_MAX * position;
                    int COLOR_DELAY = colorDelayRandom.nextInt(COLOR_DELAY_MAX) + COLOR_DELAY_BASE;
                    ObjectAnimator backgroundAnimator, titleAnimator, subtitleAnimator;
                    backgroundAnimator = ObjectAnimator.ofObject(background, "backgroundColor", new ArgbEvaluator(),
                            defaultColor(TYPE_BACK, c), album.backgroundColor);
                    backgroundAnimator.setDuration(COLOR_DUR).setStartDelay(COLOR_DELAY);
                    backgroundAnimator.start();

                    titleAnimator = ObjectAnimator.ofInt(title, textColor,
                            defaultColor(TYPE_TITLE, c), album.titleTextColor);
                    titleAnimator.setEvaluator(new ArgbEvaluator());
                    titleAnimator.setDuration(COLOR_DUR).setStartDelay(COLOR_DELAY);
                    titleAnimator.start();

                    subtitleAnimator = ObjectAnimator.ofInt(subtitle, textColor,
                            defaultColor(TYPE_SUBTITLE, c), album.subtitleTextColor);
                    subtitleAnimator.setEvaluator(new ArgbEvaluator());
                    subtitleAnimator.setDuration(COLOR_DUR).setStartDelay(COLOR_DELAY);
                    subtitleAnimator.start();

                    album.colorAnimated = true;
                } else {
                    background.setBackgroundColor(album.backgroundColor);
                    title.setTextColor(album.titleTextColor);
                    subtitle.setTextColor(album.subtitleTextColor);
                }
            }
        });
    }

    public static int defaultColor(int type, Context context) {
        int color;
        if (type == TYPE_BACK) {
            color = context.getResources().getColor(ThemeManager.get().useLightTheme ? R.color.primaryGreyLight : R.color.primaryGreyDark);
        } else if (type == TYPE_TITLE) {
            color = context.getResources().getColor(ThemeManager.get().useLightTheme ? R.color.primary_text_default_material_light : R.color.primary_text_default_material_dark);
        } else if (type == TYPE_SUBTITLE) {
            color = context.getResources().getColor(ThemeManager.get().useLightTheme ? R.color.secondary_text_default_material_light : R.color.secondary_text_default_material_dark);
        } else {
            color = 0;
        }
        return color;
    }

}
