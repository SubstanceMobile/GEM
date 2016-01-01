package com.animbus.music.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;

import com.animbus.music.R;
import com.animbus.music.ui.activity.splash.LaunchActivity;
import com.animbus.music.ui.activity.settings.chooseIcon.Icon;

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
            KEY_USE_TABS = base + ".ui.tabs.ENABLED",
            KEY_TABS_ICONS = base + ".ui.tabs.ICONS",
            KEY_TABS_SCROLLABLE = base + "ui.tabs.SCROLLABLE",
            KEY_CATEGORY_NAMES = base + "ui.CATEGORY_NAMES",
            KEY_PALETTE = base + "ui.PALETTE";

    private static final String
            KEY_BASE_THEME = base + ".theme.BASE",
            KEY_LIGHT_THEME = base + ".theme.IS_LIGHT",
            KEY_THEME_PRIMARY = base + ".theme.colors.PRIMARY",
            KEY_THEME_ACCENT = base + ".theme.colors.ACCENT";

    private Options(Context context) {

    }

    public static void init(Context context) {
        Options.context = context;
        Options.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Sets all of the settings as defaults
     */
    public static void setDefaults() {
        prefs.edit()
                .putBoolean(KEY_FIRST_RUN, true)
                .putInt(KEY_ICON, new Icon(IconManager.DESIGNER_SRINI, IconManager.COLOR_BLACK).getId())
                .putBoolean(KEY_LIGHT_THEME, false)
                .putInt(KEY_BASE_THEME, 1)
                .putInt(KEY_THEME_PRIMARY, R.color.background_faithful)
                .putInt(KEY_THEME_ACCENT, ContextCompat.getColor(context, R.color.default_accent))
                .putBoolean(KEY_USE_TABS, false)
                .putBoolean(KEY_TABS_ICONS, false)
                .putBoolean(KEY_TABS_SCROLLABLE, false)
                .putBoolean(KEY_CATEGORY_NAMES, false)
                .putBoolean(KEY_PALETTE, true)
                .apply();
        Log.d(TAG, "Defaults Set");
    }

    /**
     * Same as {@link #setDefaults()} but it restarts GEM
     */
    public static void resetPrefs() {
        //Settings Values
        setDefaults();

        //Restarting GEM
        PendingIntent mGEMIntent = PendingIntent.getActivity(context, 2138535432, new Intent(context, LaunchActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC, System.currentTimeMillis() + 100, mGEMIntent);
        System.exit(0);
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

    public static boolean getBool(String key) {
        return prefs.getBoolean(key, false);
    }

    public static int getInt(String key) {
        return prefs.getInt(key, 0);
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

    ///////////
    // Theme //
    ///////////

    public static void setBaseTheme(int baseTheme) {
        set(KEY_BASE_THEME, baseTheme);
    }

    public static int getBaseTheme() {
        return getInt(KEY_BASE_THEME);
    }

    public static void setLightTheme(boolean lightTheme) {
        set(KEY_LIGHT_THEME, lightTheme);
    }

    public static boolean isLightTheme() {
        return getBool(KEY_LIGHT_THEME);
    }

    public static void setPrimaryColor(@ColorInt int color) {
        set(KEY_THEME_PRIMARY, color);
    }

    @ColorInt
    public static int getPrimaryColor() {
        return getInt(KEY_THEME_PRIMARY);
    }

    public static void setAccentColor(@ColorInt int color) {
        set(KEY_THEME_ACCENT, color);
    }

    @ColorInt
    public static int getAccentColor() {
        return getInt(KEY_THEME_ACCENT);
    }

    //////////////
    // Use Tabs //
    //////////////

    public static void setUseTabs(boolean useTabs) {
        set(KEY_USE_TABS, useTabs);
    }

    public static boolean usingTabs() {
        return getBool(KEY_USE_TABS);
    }


    ///////////////////
    // Use Icon Tabs //
    ///////////////////

    public static void setUseIconTabs(boolean useIconTabs) {
        set(KEY_TABS_ICONS, useIconTabs);
    }

    public static boolean usingIconTabs() {
        return getBool(KEY_TABS_ICONS);
    }


    /////////////////////
    // Scrollable Tabs //
    /////////////////////

    public static void setUseScrollableTabs(boolean useScrollableTabs) {
        set(KEY_TABS_SCROLLABLE, useScrollableTabs);
    }

    public static boolean usingScrollableTabs() {
        return getBool(KEY_TABS_SCROLLABLE);
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

    ///////////////////////////////////////////////////////////////////////////
    // Switch Dependencies
    // TODO: Temporary
    ///////////////////////////////////////////////////////////////////////////

    /**
     * This updates a dependency between two {@link SwitchCompat}s
     *
     * @param parentSwitch    The switch to get values from
     * @param disableOn       What value the parent switch has to be on to disable the dependant switch. In other words: "Disable dependantSwitch on VALUE"
     * @param dependantSwitch The switch to where the values are set to
     * @param setValue        What value to set when disabled. In other words: "When disabled, dependentSwitch's value will be VALUE"
     */
    public static void switchDependency(SwitchCompat parentSwitch, Boolean disableOn, SwitchCompat dependantSwitch, Boolean setValue) {
        dependantSwitch.setEnabled(!disableOn && parentSwitch.isChecked());
        if (!dependantSwitch.isEnabled()) dependantSwitch.setChecked(setValue);
    }

    /**
     * This updates a dependency between two {@link SwitchCompat}s
     *
     * @param switch1         A switch to get values from.
     * @param switch2         A switch to get values from.
     * @param dependantSwitch The switch to where the values are set to
     * @param disableOn1      What value switch1 has to be on to disable the dependant switch. In other words: "Disable dependantSwitch on VALUE1")
     * @param disableOn2      What value switch2 has to be on to disable the dependant switch. In other words: "Disable dependantSwitch on VALUE2")
     * @param setTo           What value to set when disabled. In other words: "When disabled, dependentSwitch's value will be VALUE"
     */
    public static void doubleSwitchDependency(SwitchCompat switch1, SwitchCompat switch2, SwitchCompat dependantSwitch,
                                       boolean disableOn1, boolean disableOn2, boolean setTo) {
        dependantSwitch.setEnabled((!disableOn1 && switch1.isChecked()) && (!disableOn2 && switch2.isChecked()));
        if (!dependantSwitch.isEnabled()) dependantSwitch.setChecked(setTo);
    }

}
