package com.animbus.music.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.SwitchCompat;

import com.animbus.music.R;

public class SettingsManager {
    public static String KEY_USE_LIGHT_THEME = "com.animbus.music.USE_LIGHT_THEME", KEY_USE_DARK_THEME_AT_NIGHT = "com.animbus.music.USE_DARK_THEME_AT_NIGHT", KEY_ICON = "com.animbus.music.CURRENT_ICON", KEY_DEFAULT_SCREEN = "com.animbus.music.DEFAULT_SCREEN", KEY_FIRST_RUN = "com.animbus.music.FIRST_RUN",
            KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN = "com.animbus.music.USE_CATEGORY_NAMES", KEY_USE_PALETTE_IN_GRID = "com.animbus.music.GRID_PALETTE", KEY_USE_LIGHT_BACKGROUND_IN_ICON_PREVIEW = "com.animbus.music.LIGHT_BACKGROUND_ICON_PREVIEW", KEY_SELECTED_ICON_RADIOBUTTON = "com.animbus.music.SELECTED_ICON_RADIOBUTTON",
            KEY_USE_COLOR_EXTRACTION_MASTER = "com.animbus.music.USE_PALETTE_MASTER", KEY_EXTRACT_COLORS_IN_NOW_PLAYING_SCREEN = "com.animbus.music.NOW_PLAYING_PALETTE", KEY_USE_NOW_PLAYING_PEEK = "com.animbus.music.USE_PEEK_FEATURE", KEY_USE_NEW_NOW_PLAYING = "com.animbus.music.USE_CLASSIC_NOW_PLAYING",
            KEY_USE_TABS = "com.animbus.music.USE_TABS";
    public static Integer TYPE_BOOLEAN = 0, TYPE_STRING = 1, TYPE_INTEGER = 2;
    public static Integer SCREEN_HOME = 0, SCREEN_ALBUMS = 1, SCREEN_SONGS = 2, SCREEN_ARTISTS = 3, SCREEN_PLAYLISTS = 4;
    SharedPreferences prefrences;
    SharedPreferences.Editor prefrencesEditor;
    Context context;


    public SettingsManager(Context cxt) {
        context = cxt;
        prefrences = cxt.getSharedPreferences("com.animbus.music", Context.MODE_PRIVATE);
        prefrencesEditor = prefrences.edit();
    }

    public void setDefaultSettings() {
        setBooleanSetting(KEY_USE_LIGHT_THEME, false);
        setBooleanSetting(KEY_USE_DARK_THEME_AT_NIGHT, false);
        setIntSetting(KEY_ICON, R.mipmap.ic_launcher_new_dark);
        setIntegerSetting(KEY_DEFAULT_SCREEN, SCREEN_ALBUMS);
        setBooleanSetting(KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false);
    }

    public Boolean getBooleanSetting(String key, Boolean defaultValue) {
        return prefrences.getBoolean(key, defaultValue);
    }

    public Integer getIntegerSetting(String key, Integer defaultValue) {
        return prefrences.getInt(key, defaultValue);
    }

    public String getStringSetting(String key, String defaultValue) {
        return prefrences.getString(key, defaultValue);
    }

    public int getIntSetting(String key, int defaultValue) {
        return prefrences.getInt(key, defaultValue);
    }

    public void setBooleanSetting(String key, Boolean value) {
        prefrencesEditor.putBoolean(key, value).apply();
    }

    public void setIntegerSetting(String key, Integer value) {
        prefrencesEditor.putInt(key, value).apply();
    }

    public void setIntSetting(String key, int value) {
        prefrencesEditor.putInt(key, value).apply();
    }

    public void setStringSetting(String key, String value) {
        prefrencesEditor.putString(key, value).apply();
    }

    public void switchDependancy(SwitchCompat parentSwitch, SwitchCompat dependantSwitch) {
        if (parentSwitch.isChecked()) {
            dependantSwitch.setEnabled(true);
        } else {
            dependantSwitch.setEnabled(false);
            dependantSwitch.setChecked(false);
        }
    }

    public int getExitIcon() {
        int icon;
        if (getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
            icon = R.drawable.ic_action_exit_light;
        } else {
            icon = R.drawable.ic_action_exit;
        }
        return icon;
    }

}