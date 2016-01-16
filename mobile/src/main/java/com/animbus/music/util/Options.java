package com.animbus.music.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Process;
import android.os.SystemClock;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.animbus.music.ui.activity.settings.chooseIcon.Icon;
import com.animbus.music.ui.activity.splash.LaunchActivity;

import static android.content.Intent.ACTION_MAIN;

/**
 * Created by Adrian on 11/20/2015
 */
public class Options {
    private static volatile Context context;
    private static volatile SharedPreferences prefs;
    private static final String TAG = "Options";

    //Keys
    private static final String base = "com.animbus.music";
    private static final String
            KEY_FIRST_RUN = base + ".FIRST_RUN",
            KEY_ICON = base + ".ICON",
            KEY_TABS_MODE = "tab_mode",
            KEY_CATEGORY_NAMES = "screen_name",
            KEY_PALETTE = "use_palette",
            KEY_LIGHT_THEME = base + ".theme.IS_LIGHT";

    private static volatile long updatedAt;

    private Options() {

    }

    public static void init(Context context) {
        Options.context = context;
        Options.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                updatedAt = System.currentTimeMillis();
            }
        });
    }

    public static boolean shouldRecreate(long updateTime) {
        return updatedAt > updateTime;
    }

    public static void resetPrefs() {
        PendingIntent mGEMIntent = PendingIntent.getActivity(context, 2138535432,
                new Intent(context, LaunchActivity.class).setAction(ACTION_MAIN), PendingIntent.FLAG_CANCEL_CURRENT);
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC, System.currentTimeMillis() + 100, mGEMIntent);
        Options.prefs.edit().clear().commit();
        context.getSharedPreferences("[[afollestad_theme-engine]]", 0).edit().clear().commit();
        Process.killProcess(Process.myPid());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ///////////////////////////////////////////////////////////////////////////

    public static void set(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public static void set(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public static void set(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public static boolean getBool(String key) {
        return prefs.getBoolean(key, false);
    }

    public static int getInt(String key) {
        return prefs.getInt(key, 0);
    }

    public static String getString(String key) {
        return prefs.getString(key, "");
    }

    ///////////////////////////////////////////////////////////////////////////
    // All of the settings
    ///////////////////////////////////////////////////////////////////////////

    ///////////////
    // First Run //
    ///////////////

    public static boolean isFirstRun() {
        return prefs.getBoolean(KEY_FIRST_RUN, true);
    }

    public static void tripFirstRunIfNeeded() {
        if (isFirstRun()) prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply();
    }

    //////////
    // Icon //
    //////////

    public static void setSavedIconID(int id) {
        set(KEY_ICON, id);
    }

    public static int getSavedIconID() {
        return getInt(KEY_ICON);
    }

    /////////////////
    // Light Theme //
    /////////////////

    public static void setLightTheme(boolean lightTheme) {
        set(KEY_LIGHT_THEME, lightTheme);
    }

    public static boolean isLightTheme() {
        return getBool(KEY_LIGHT_THEME);
    }

    //////////
    // Tabs //
    //////////

    public static void setTabMode(String mode) {
        set(KEY_TABS_MODE, mode);
    }

    public static boolean usingTabs() {
        try {
            return Integer.parseInt(getString(KEY_TABS_MODE)) != 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean usingIconTabs() {
        try {
            return Integer.parseInt(getString(KEY_TABS_MODE)) == 2;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean usingScrollableTabs() {
        try {
            return Integer.parseInt(getString(KEY_TABS_MODE)) == 3;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    ////////////////////
    // Category Names //
    ////////////////////

    public static void setUseCategoryNames(boolean useCategoryNames) {
        set(KEY_CATEGORY_NAMES, useCategoryNames);
    }

    public static boolean usingCategoryNames() {
        return getBool(KEY_CATEGORY_NAMES);
    }


    /////////////////
    // Use Palette //
    /////////////////

    public static void setUsePalette(boolean usePalette) {
        set(KEY_PALETTE, usePalette);
    }

    public static boolean usingPalette() {
        return getBool(KEY_PALETTE);
    }

}
