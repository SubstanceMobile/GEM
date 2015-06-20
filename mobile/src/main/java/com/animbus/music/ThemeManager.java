package com.animbus.music;

import android.content.Context;

import com.animbus.music.data.SettingsManager;

public class ThemeManager {
    public static int TYPE_NORMAL = 1, TYPE_TRANSPARENT_APPBAR = 2, TYPE_PEEK = 3;
    SettingsManager settings;
    Integer type;
    Context cxt;

    Boolean useLightTheme;

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
                    theme = R.style.AppTheme_Light_TranslucntAppbar;
                } else {
                    theme = R.style.AppTheme;
                }
                break;
            case 3:
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

    public int getCurrentBackgroundColor() {
        return cxt.getResources().getColor(getCurrentBackgroundColorResource());
    }
}
