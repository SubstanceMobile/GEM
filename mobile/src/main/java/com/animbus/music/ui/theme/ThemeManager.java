package com.animbus.music.ui.theme;

import android.content.Context;

import com.animbus.music.R;
import com.animbus.music.util.SettingsManager;

import java.util.ArrayList;

public class ThemeManager {
    public static final int BASE_DARK = R.style.AppTheme,  BASE_LIGHT = R.style.AppTheme_Light, BASE_GREY = -1;
    private static final ThemeManager i = new ThemeManager();
    public Boolean useLightTheme;
    public Context cxt;
    int base;
    int colorPrimary;
    int colorAccent;
    int colorGrey;
    int colorComplimentary;
    int colorComplimentaryGrey;
    int colorBackground;
    Theme currentTheme;
    SettingsManager settings;
    ArrayList<OnThemeChangedListener> listeners;

    private ThemeManager() {
    }

    public static ThemeManager get() {
        return i;
    }

    public int getBase() {
        return SettingsManager.get().getIntSetting(SettingsManager.KEY_THEME_BASE, BASE_DARK);
    }

    public void setBase(int newBase) {
        base = newBase;
        settings.setIntSetting(SettingsManager.KEY_THEME_BASE, newBase);
    }

    public ThemeManager setContext(Context context) {
        cxt = context;
        settings = SettingsManager.get();
        update();
        return this;
    }

    public void update() {
        useLightTheme = settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false);
        base = settings.getIntSetting(SettingsManager.KEY_THEME_BASE, BASE_DARK);
        colorPrimary = settings.getIntSetting(SettingsManager.KEY_COLOR_PRIMARY, R.color.primaryDark);
        colorAccent = settings.getIntSetting(SettingsManager.KEY_COLOR_ACCENT, R.color.accent_material_dark);
        colorGrey = settings.getIntSetting(SettingsManager.KEY_COLOR_GREY, R.color.primaryGreyDark);
        colorComplimentary = settings.getIntSetting(SettingsManager.KEY_COLOR_COMPLIMENTARY, R.color.primaryLight);
        colorComplimentaryGrey = settings.getIntSetting(SettingsManager.KEY_COLOR_COMPLIMENTARY_GREY, R.color.primaryGreyLight);
        colorBackground = settings.getIntSetting(SettingsManager.KEY_COLOR_BACKGROUND, R.color.primaryDark);
    }

    public int getNavdrawerColor() {
        int color;
        if (useLightTheme) {
            color = R.color.secondary_text_default_material_dark;
        } else {
            color = R.color.secondary_text_default_material_dark;
        }
        return color;
    }

    public int getCurrentGreyColorResource() {
        int color;
        if (useLightTheme) {
            color = R.color.primaryGreyLight;
        } else {
            color = R.color.primaryGreyDark;
        }
        return color;
    }

    public int getCurrentGreyColor() {
        return cxt.getResources().getColor(getCurrentGreyColorResource());
    }

    public Theme getTheme() {
        return new Theme.Builder().setBase(base).
                setColorPrimary(colorPrimary)
                .setColorAccent(colorAccent)
                .setColorGrey(colorGrey)
                .setColorComplimentary(colorComplimentary)
                .setColorComplimentaryGrey(colorComplimentaryGrey)
                .build();
    }

    public void setTheme(Theme theme) {
        for (OnThemeChangedListener l : listeners) {
            l.onThemeChanged(theme);
        }
        currentTheme = theme;
    }

    public interface OnThemeChangedListener {
        void onThemeChanged(Theme newTheme);
    }

}
