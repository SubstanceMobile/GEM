package com.animbus.music;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.SwitchCompat;

import com.animbus.music.ui.settings.chooseIcon.IconManager;

public class SettingsManager {
    private final static SettingsManager instance = new SettingsManager();
    public static String
            KEY_USE_LIGHT_THEME = "com.animbus.music.USE_LIGHT_THEME",
            KEY_USE_DARK_THEME_AT_NIGHT = "com.animbus.music.USE_DARK_THEME_AT_NIGHT",
            KEY_ICON = "com.animbus.music.CURRENT_ICON",
            KEY_DEFAULT_SCREEN = "com.animbus.music.DEFAULT_SCREEN",
            KEY_FIRST_RUN = "com.animbus.music.FIRST_RUN",
            KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN = "com.animbus.music.USE_CATEGORY_NAMES",
            KEY_USE_PALETTE_IN_GRID = "com.animbus.music.GRID_PALETTE",
            KEY_USE_NOW_PLAYING_PEEK = "com.animbus.music.USE_PEEK_FEATURE",
            KEY_USE_NEW_NOW_PLAYING = "com.animbus.music.USE_CLASSIC_NOW_PLAYING",
            KEY_USE_TABS = "com.animbus.music.USE_TABS",
            KEY_SCROLLABLE_TABS = "com.animbus.music.SCROLLABLE_TABS",
            KEY_USE_TAB_ICONS = "com.animbus.music.TAB_ICONS",
            KEY_CURRENT_CONFIG = "com.animbus.music.THEME_CONFIG",
            KEY_THEME_BASE = "com.animbus.music.THEME_BASE",
            KEY_COLOR_PRIMARY = "com.animbus.music.COLOR_PRIMARY",
            KEY_COLOR_ACCENT = "com.animbus.music.COLOR_ACCENT",
            KEY_COLOR_GREY = "com.animbus.music.COLOR_GREY",
            KEY_COLOR_BACKGROUND = "com.animbus.music.COLOR_BACKGROUND",
            KEY_COLOR_COMPLIMENTARY = "com.animbus.music.COLOR_COMPLIMENTARY",
            KEY_COLOR_COMPLIMENTARY_GREY = "com.animbus.music.COLOR_COMPLIMENYARY_GREY",
            KEY_INTERNAL_TESTER_REGISTERED = "com.animbus.music.testing.internal.TESTER_REGESTERED",
    KEY_ALBUM_COLOR_AT_ = "com.animbus.music.album.color.withId_";
    public static Integer TYPE_BOOLEAN = 0, TYPE_STRING = 1, TYPE_INTEGER = 2;
    public static Integer SCREEN_HOME = 0, SCREEN_ALBUMS = 1, SCREEN_SONGS = 2, SCREEN_ARTISTS = 3, SCREEN_PLAYLISTS = 4;
    public Context context;
    SharedPreferences prefrences;
    SharedPreferences.Editor prefrencesEditor;

    private SettingsManager() {
    }

    public static SettingsManager get() {
        return instance;
    }

    public SettingsManager setContext(Context cxt) {
        context = cxt;
        prefrences = cxt.getSharedPreferences("com.animbus.music", Context.MODE_PRIVATE);
        prefrencesEditor = prefrences.edit();
        return this;
    }

    public void setDefaultSettings() {
        setBooleanSetting(KEY_USE_LIGHT_THEME, false);
        setBooleanSetting(KEY_USE_DARK_THEME_AT_NIGHT, false);
        setIntSetting(KEY_ICON, IconManager.get().getIcon(IconManager.DESIGNER_SRINI, IconManager.COLOR_BLACK).getId());
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

    /**
     * This updates a dependancy between two {@link SwitchCompat}s
     *
     * @param parentSwitch    The switch to get values from
     * @param disableOn       What value the parent switch has to be on to disable the dependant switch. In other words: "Disable dependantSwitch on VALUE"
     * @param dependantSwitch The switch to where the values are set to
     * @param setValue        What value to set when disabled. In other words: "When disabled, dependentSwitch's value will be VALUE"
     */
    public void switchDependancy(SwitchCompat parentSwitch, Boolean disableOn, SwitchCompat dependantSwitch, Boolean setValue) {
        if (!disableOn) {
            if (parentSwitch.isChecked()) {
                dependantSwitch.setEnabled(true);
            } else {
                dependantSwitch.setEnabled(false);
                dependantSwitch.setChecked(setValue);
            }
        } else {
            if (parentSwitch.isChecked()) {
                dependantSwitch.setEnabled(false);
                dependantSwitch.setChecked(setValue);
            } else {
                dependantSwitch.setEnabled(true);
            }
        }
    }

    /**
     * This updates a dependancy between two {@link SwitchCompat}s
     *
     * @param switch1         The switch to get values from. Overrides switch2
     * @param switch2         The switch to get values from. Overridde
     * @param dependantSwitch The switch to where the values are set to
     * @param disableOn1      What value the parent switch has to be on to disable the dependant switch. In other words: "Disable dependantSwitch on VALUE1")
     * @param disableOn2      What value the parent switcр2 has to be on to disable the dependant switch. In other words: "Disable dependantSwitch on VALUE2")
     * @param setTo           What value to set when disabled. In other words: "When disabled, dependentSwitch's value will be VALUE"
     */
    public void doubleSwitchDependancy(SwitchCompat switch1, SwitchCompat switch2, SwitchCompat dependantSwitch,
                                       boolean disableOn1, boolean disableOn2, boolean setTo) {

        if (disableOn1) {
            if (switch1.isChecked()) {
                dependantSwitch.setEnabled(false);
                dependantSwitch.setChecked(setTo);
            } else {
                if (disableOn2) {
                    if (switch2.isChecked()) {
                        dependantSwitch.setEnabled(false);
                        dependantSwitch.setChecked(setTo);
                    } else {
                        dependantSwitch.setEnabled(true);
                    }
                } else {
                    if (switch2.isChecked()) {
                        dependantSwitch.setEnabled(true);
                    } else {
                        dependantSwitch.setEnabled(false);
                        dependantSwitch.setChecked(setTo);
                    }
                }
            }
        } else {
            if (switch1.isChecked()) {
                if (disableOn2) {
                    if (switch2.isChecked()) {
                        dependantSwitch.setEnabled(false);
                        dependantSwitch.setChecked(setTo);
                    } else {
                        dependantSwitch.setEnabled(true);
                    }
                } else {
                    if (switch2.isChecked()) {
                        dependantSwitch.setEnabled(true);
                    } else {
                        dependantSwitch.setEnabled(false);
                        dependantSwitch.setChecked(setTo);
                    }
                }
            } else {
                dependantSwitch.setEnabled(false);
                dependantSwitch.setChecked(setTo);
            }
        }

    }

}