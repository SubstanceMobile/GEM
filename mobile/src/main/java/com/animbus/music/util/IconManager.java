package com.animbus.music.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;

import com.animbus.music.R;
import com.animbus.music.ui.activity.settings.chooseIcon.Icon;

/**
 * Created by Adrian on 7/26/2015.
 */
public class IconManager {
    public static final int DESIGNER_SRINI = 0, DESIGNER_ALEX = 1, DESIGNER_JAKA = 2, DESIGNER_NGUYEN = 3;
    public static final int COLOR_BLACK = 0, COLOR_WHITE = 1, COLOR_SLATE = 2, COLOR_RED = 3, COLOR_BLUE = 4, COLOR_GREEN = 5, COLOR_ORANGE = 6, COLOR_COLORFUL = 7;
    public static final int COLOR_BLACK_SIMPLE = 8, COLOR_WHITE_SIMPLE = 9;
    private static IconManager ourInstance = new IconManager();
    Context cxt;
    SettingsManager settings;

    int ICON_TRUE, ICON_FALSE;

    private IconManager() {
        ICON_FALSE = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        ICON_TRUE = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }

    public static IconManager get() {
        return ourInstance;
    }

    public IconManager setContext(Context context) {
        this.cxt = context;
        settings = SettingsManager.get();
        return this;
    }

    public void setIcon(int designer, int color) {
        Icon icon = new Icon(designer, color);
        settings.setIntSetting(SettingsManager.KEY_ICON, icon.getId());
    }

    public Icon getIcon() {
        return getIcon(settings.getIntSetting(SettingsManager.KEY_ICON, new Icon(DESIGNER_SRINI, COLOR_BLACK).getId()));
    }

    public void setIcon(Icon icon) {
        settings.setIntSetting(SettingsManager.KEY_ICON, icon.getId());
    }

    public Icon getIcon(int id) {
        if (id == getID(DESIGNER_SRINI, COLOR_BLACK)) {
            return new Icon(DESIGNER_SRINI, COLOR_BLACK);
        } else if (id == getID(DESIGNER_SRINI, COLOR_WHITE)) {
            return new Icon(DESIGNER_SRINI, COLOR_WHITE);
        } else if (id == getID(DESIGNER_SRINI, COLOR_RED)) {
            return new Icon(DESIGNER_SRINI, COLOR_RED);
        } else if (id == getID(DESIGNER_SRINI, COLOR_SLATE)) {
            return new Icon(DESIGNER_SRINI, COLOR_SLATE);
        } else if (id == getID(DESIGNER_SRINI, COLOR_GREEN)) {
            return new Icon(DESIGNER_SRINI, COLOR_GREEN);
        } else if (id == getID(DESIGNER_SRINI, COLOR_BLUE)) {
            return new Icon(DESIGNER_SRINI, COLOR_BLUE);
        } else if (id == getID(DESIGNER_ALEX, COLOR_BLACK)) {
            return new Icon(DESIGNER_ALEX, COLOR_BLACK);
        } else if (id == getID(DESIGNER_ALEX, COLOR_WHITE)) {
            return new Icon(DESIGNER_ALEX, COLOR_WHITE);
        } else if (id == getID(DESIGNER_ALEX, COLOR_COLORFUL)) {
            return new Icon(DESIGNER_ALEX, COLOR_COLORFUL);
        } else if (id == getID(DESIGNER_JAKA, COLOR_BLACK)) {
            return new Icon(DESIGNER_JAKA, COLOR_BLACK);
        } else if (id == getID(DESIGNER_JAKA, COLOR_WHITE)) {
            return new Icon(DESIGNER_JAKA, COLOR_WHITE);
        } else if (id == getID(DESIGNER_NGUYEN, COLOR_ORANGE)) {
            return new Icon(DESIGNER_NGUYEN, COLOR_ORANGE);
        } else if (id == getID(DESIGNER_NGUYEN, COLOR_GREEN)) {
            return new Icon(DESIGNER_NGUYEN, COLOR_GREEN);
        } else if (id == getID(DESIGNER_NGUYEN, COLOR_RED)) {
            return new Icon(DESIGNER_NGUYEN, COLOR_RED);
        } else {
            return null;
        }
    }

    public Icon getOverviewIcon(Icon icon){
        int id = icon.getId();
        boolean useLight = settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false);
        if (id == getID(DESIGNER_SRINI, COLOR_BLACK)) {
            if (useLight){
                return new Icon(DESIGNER_SRINI, COLOR_BLACK);
            } else {
                return new Icon(DESIGNER_SRINI, COLOR_WHITE);
            }
        } else if (id == getID(DESIGNER_SRINI, COLOR_WHITE)) {
            if (useLight){
                return new Icon(DESIGNER_SRINI, COLOR_BLACK);
            } else {
                return new Icon(DESIGNER_SRINI, COLOR_WHITE);
            }
        } else if (id == getID(DESIGNER_SRINI, COLOR_RED)) {
            return new Icon(DESIGNER_SRINI, COLOR_RED);
        } else if (id == getID(DESIGNER_SRINI, COLOR_SLATE)) {
            return new Icon(DESIGNER_SRINI, COLOR_SLATE);
        } else if (id == getID(DESIGNER_SRINI, COLOR_GREEN)) {
            return new Icon(DESIGNER_SRINI, COLOR_GREEN);
        } else if (id == getID(DESIGNER_SRINI, COLOR_BLUE)) {
            return new Icon(DESIGNER_SRINI, COLOR_BLUE);
        } else if (id == getID(DESIGNER_ALEX, COLOR_BLACK)) {
            if (useLight){
                return new Icon(DESIGNER_ALEX, COLOR_BLACK);
            } else {
                return new Icon(DESIGNER_ALEX, COLOR_WHITE);
            }
        } else if (id == getID(DESIGNER_ALEX, COLOR_WHITE)) {
            if (useLight){
                return new Icon(DESIGNER_ALEX, COLOR_BLACK);
            } else {
                return new Icon(DESIGNER_ALEX, COLOR_WHITE);
            }
        } else if (id == getID(DESIGNER_ALEX, COLOR_COLORFUL)) {
            return new Icon(DESIGNER_ALEX, COLOR_COLORFUL);
        } else if (id == getID(DESIGNER_JAKA, COLOR_BLACK)) {
            if (useLight){
                return new Icon(DESIGNER_JAKA, COLOR_BLACK_SIMPLE);
            } else {
                return new Icon(DESIGNER_JAKA, COLOR_WHITE_SIMPLE);
            }
        } else if (id == getID(DESIGNER_JAKA, COLOR_WHITE)) {
            if (useLight){
                return new Icon(DESIGNER_SRINI, COLOR_BLACK_SIMPLE);
            } else {
                return new Icon(DESIGNER_SRINI, COLOR_WHITE_SIMPLE);
            }
        } else if (id == getID(DESIGNER_NGUYEN, COLOR_ORANGE)) {
            return new Icon(DESIGNER_NGUYEN, COLOR_ORANGE);
        } else if (id == getID(DESIGNER_NGUYEN, COLOR_GREEN)) {
            return new Icon(DESIGNER_NGUYEN, COLOR_GREEN);
        } else if (id == getID(DESIGNER_NGUYEN, COLOR_RED)) {
            return new Icon(DESIGNER_NGUYEN, COLOR_RED);
        } else {
            return null;
        }
    }

    public Icon getIcon(View v) {
        int id = -1;
        switch (v.getId()) {
            case R.id.settings_choose_icon_srini_black:
                id = getID(DESIGNER_SRINI, COLOR_BLACK);
                break;
            case R.id.settings_choose_icon_srini_white:
                id = getID(DESIGNER_SRINI, COLOR_WHITE);
                break;
            case R.id.settings_choose_icon_srini_red:
                id = getID(DESIGNER_SRINI, COLOR_RED);
                break;
            case R.id.settings_choose_icon_strini_slate:
                id = getID(DESIGNER_SRINI, COLOR_SLATE);
                break;
            case R.id.settings_choose_icon_srini_green:
                id = getID(DESIGNER_SRINI, COLOR_GREEN);
                break;
            case R.id.settings_choose_icon_srini_blue:
                id = getID(DESIGNER_SRINI, COLOR_BLUE);
                break;
            case R.id.settings_choose_icon_alex_black:
                id = getID(DESIGNER_ALEX, COLOR_BLACK);
                break;
            case R.id.settings_choose_icon_alex_white:
                id = getID(DESIGNER_ALEX, COLOR_WHITE);
                break;
            case R.id.settings_choose_icon_alex_color:
                id = getID(DESIGNER_ALEX, COLOR_COLORFUL);
                break;
            case R.id.settings_choose_icon_jaka_black:
                id = getID(DESIGNER_JAKA, COLOR_BLACK);
                break;
            case R.id.settings_choose_icon_jaka_white:
                id = getID(DESIGNER_JAKA, COLOR_WHITE);
                break;
            case R.id.settings_choose_icon_nguyen_orange:
                id = getID(DESIGNER_NGUYEN, COLOR_ORANGE);
                break;
            case R.id.settings_choose_icon_nguyen_green:
                id = getID(DESIGNER_NGUYEN, COLOR_GREEN);
                break;
            case R.id.settings_choose_icon_nguyen_red:
                id = getID(DESIGNER_NGUYEN, COLOR_RED);
                break;
        }
        return getIcon(id);
    }

    public Icon getIcon(int designer, int color) {
        return getIcon(getID(designer, color));
    }

    public int getID(int designer, int color) {
        if (designer == DESIGNER_SRINI) {
            if (color == COLOR_BLACK) {
                return 0;
            } else if (color == COLOR_WHITE) {
                return 1;
            } else if (color == COLOR_RED) {
                return 2;
            } else if (color == COLOR_SLATE) {
                return 3;
            } else if (color == COLOR_GREEN) {
                return 4;
            } else if (color == COLOR_BLUE) {
                return 5;
            } else {
                return -1;
            }
        } else if (designer == DESIGNER_ALEX) {
            if (color == COLOR_BLACK) {
                return 6;
            } else if (color == COLOR_WHITE) {
                return 7;
            } else if (color == COLOR_COLORFUL) {
                return 8;
            } else {
                return -1;
            }
        } else if (designer == DESIGNER_JAKA) {
            if (color == COLOR_BLACK) {
                return 9;
            } else if (color == COLOR_WHITE) {
                return 10;
            } else if (color == COLOR_WHITE_SIMPLE) {
                return 11;
            } else if (color == COLOR_BLACK_SIMPLE) {
                return 12;
            } else {
                return -1;
            }
        } else if (designer == DESIGNER_NGUYEN) {
            if (color == COLOR_ORANGE) {
                return 13;
            } else if (color == COLOR_GREEN) {
                return 14;
            } else if (color == COLOR_RED) {
                return 15;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public ComponentName getName(int id) {
        if (id == getID(DESIGNER_SRINI, COLOR_BLACK)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.srini.black");
        } else if (id == getID(DESIGNER_SRINI, COLOR_WHITE)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.srini.white");
        } else if (id == getID(DESIGNER_SRINI, COLOR_RED)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.srini.red");
        } else if (id == getID(DESIGNER_SRINI, COLOR_SLATE)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.srini.slate");
        } else if (id == getID(DESIGNER_SRINI, COLOR_GREEN)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.srini.green");
        } else if (id == getID(DESIGNER_SRINI, COLOR_BLUE)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.srini.blue");
        } else if (id == getID(DESIGNER_ALEX, COLOR_BLACK)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.alex.black");
        } else if (id == getID(DESIGNER_ALEX, COLOR_WHITE)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.alex.white");
        } else if (id == getID(DESIGNER_ALEX, COLOR_COLORFUL)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.alex.color");
        } else if (id == getID(DESIGNER_JAKA, COLOR_BLACK)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.jaka.black");
        } else if (id == getID(DESIGNER_JAKA, COLOR_WHITE)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.jaka.white");
        } else if (id == getID(DESIGNER_NGUYEN, COLOR_ORANGE)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.nguyen.orange");
        } else if (id == getID(DESIGNER_NGUYEN, COLOR_GREEN)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.nguyen.green");
        } else if (id == getID(DESIGNER_NGUYEN, COLOR_RED)) {
            return new ComponentName(cxt.getPackageName(), "com.animbus.music.activities.app.icon.nguyen.red");
        } else {
            return null;
        }
    }

    public int getDrawable(int id){
        if (id == getID(DESIGNER_SRINI, COLOR_BLACK)) {
            return R.mipmap.ic_launcher_srini_black;
        } else if (id == getID(DESIGNER_SRINI, COLOR_WHITE)) {
            return R.mipmap.ic_launcher_srini_white;
        } else if (id == getID(DESIGNER_SRINI, COLOR_RED)) {
            return R.mipmap.ic_launcher_srini_red;
        } else if (id == getID(DESIGNER_SRINI, COLOR_SLATE)) {
            return R.mipmap.ic_launcher_srini_slate;
        } else if (id == getID(DESIGNER_SRINI, COLOR_GREEN)) {
            return R.mipmap.ic_launcher_srini_black;
        } else if (id == getID(DESIGNER_SRINI, COLOR_BLUE)) {
            return R.mipmap.ic_launcher_srini_black;
        } else if (id == getID(DESIGNER_ALEX, COLOR_BLACK)) {
            return R.mipmap.ic_launcher_alex_black;
        } else if (id == getID(DESIGNER_ALEX, COLOR_WHITE)) {
            return R.mipmap.ic_launcher_alex_white;
        } else if (id == getID(DESIGNER_ALEX, COLOR_COLORFUL)) {
            return R.mipmap.ic_launcher_alex_color;
        } else if (id == getID(DESIGNER_JAKA, COLOR_BLACK)) {
            return R.mipmap.ic_launcher_jaka_dark;
        } else if (id == getID(DESIGNER_JAKA, COLOR_WHITE)) {
            return R.mipmap.ic_launcher_jaka_light;
        } else if (id == getID(DESIGNER_JAKA, COLOR_BLACK_SIMPLE)) {
            return R.mipmap.ic_launcher_jaka_dark_simple;
        } else if (id == getID(DESIGNER_JAKA, COLOR_WHITE_SIMPLE)) {
            return R.mipmap.ic_launcher_jaka_light_simple;
        } else if (id == getID(DESIGNER_NGUYEN, COLOR_ORANGE)) {
            return R.mipmap.ic_launcher_nguyen_orange;
        } else if (id == getID(DESIGNER_NGUYEN, COLOR_GREEN)) {
            return R.mipmap.ic_launcher_nguyen_green;
        } else if (id == getID(DESIGNER_NGUYEN, COLOR_RED)) {
            return R.mipmap.ic_launcher_nguyen_red;
        } else {
            return -1;
        }
    }

    public void enable(Icon ico){
        cxt.getPackageManager().setComponentEnabledSetting(ico.getName(), ICON_TRUE, PackageManager.DONT_KILL_APP);
    }

    public void disable(Icon ico){
        cxt.getPackageManager().setComponentEnabledSetting(ico.getName(), ICON_FALSE, PackageManager.DONT_KILL_APP);
    }

    public void switchTo(Icon ico){
        disableAll();
        enable(ico);
    }

    public void disableAll(){
        disable(getIcon(DESIGNER_SRINI, COLOR_BLACK));
        disable(getIcon(DESIGNER_SRINI, COLOR_WHITE));
        disable(getIcon(DESIGNER_SRINI, COLOR_RED));
        disable(getIcon(DESIGNER_SRINI, COLOR_SLATE));
        disable(getIcon(DESIGNER_SRINI, COLOR_GREEN));
        disable(getIcon(DESIGNER_SRINI, COLOR_BLUE));
        disable(getIcon(DESIGNER_ALEX, COLOR_BLACK));
        disable(getIcon(DESIGNER_ALEX, COLOR_WHITE));
        disable(getIcon(DESIGNER_ALEX, COLOR_COLORFUL));
        disable(getIcon(DESIGNER_JAKA, COLOR_BLACK));
        disable(getIcon(DESIGNER_JAKA, COLOR_WHITE));
        disable(getIcon(DESIGNER_NGUYEN, COLOR_ORANGE));
        disable(getIcon(DESIGNER_NGUYEN, COLOR_GREEN));
        disable(getIcon(DESIGNER_NGUYEN, COLOR_RED));
    }

    public void enableAll(){
        enable(getIcon(DESIGNER_SRINI, COLOR_BLACK));
        enable(getIcon(DESIGNER_SRINI, COLOR_WHITE));
        enable(getIcon(DESIGNER_SRINI, COLOR_RED));
        enable(getIcon(DESIGNER_SRINI, COLOR_SLATE));
        enable(getIcon(DESIGNER_SRINI, COLOR_GREEN));
        enable(getIcon(DESIGNER_SRINI, COLOR_BLUE));
        enable(getIcon(DESIGNER_ALEX, COLOR_BLACK));
        enable(getIcon(DESIGNER_ALEX, COLOR_WHITE));
        enable(getIcon(DESIGNER_ALEX, COLOR_COLORFUL));
        enable(getIcon(DESIGNER_JAKA, COLOR_BLACK));
        enable(getIcon(DESIGNER_JAKA, COLOR_WHITE));
        enable(getIcon(DESIGNER_NGUYEN, COLOR_ORANGE));
        enable(getIcon(DESIGNER_NGUYEN, COLOR_GREEN));
        enable(getIcon(DESIGNER_NGUYEN, COLOR_RED));
    }
}
