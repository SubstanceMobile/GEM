package com.animbus.music;

import android.content.Context;

import com.animbus.music.data.SettingsManager;

public class ThemeManager {
    public static int TYPE_NORMAL = 1, TYPE_PEEK = 2;
    SettingsManager settings;
    Integer type;
    Context cxt;

    public Boolean useLightTheme;

    public ThemeManager(Context context, int type) {
        this.type = type;
        cxt = context;
        settings = new SettingsManager(context);
        update();
    }

    public void update() {
        useLightTheme = settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false);
    }

    public int getCurrentTheme() {
        int theme = 0;
        switch (type) {
            case 1:
                if (useLightTheme) {
                    theme = R.style.AppTheme_Light;
                } else {
                    theme = R.style.AppTheme;
                }
                break;
            case 2:
                if (useLightTheme) {
                    theme = R.style.AppTheme_Light_QuickPeek;
                } else {
                    theme = R.style.AppTheme_QuickPeek;
                }
                break;
        }
        return theme;
    }


    public int getCurrentBackgroundColorResource() {
        int color;
        if (useLightTheme) {
            color = R.color.primaryLight;
        } else {
            color = R.color.primaryDark;
        }
        return color;
    }

    public int getNavdrawerColor(){
        int color;
        if (useLightTheme) {
            color = R.color.secondary_text_default_material_dark;
        } else {
            color = R.color.secondary_text_default_material_dark;
        }
        return color;
    }

    public int getCurrentGreyColorResource(){
        int color;
        if (useLightTheme) {
            color = R.color.primaryGreyLight;
        } else {
            color = R.color.primaryGreyDark;
        }
        return color;
    }

    public int getCurrentBackgroundColor() {
        return cxt.getResources().getColor(getCurrentBackgroundColorResource());
    }

    public int getCurrentGreyColor() {
        return cxt.getResources().getColor(getCurrentGreyColorResource());
    }
}
