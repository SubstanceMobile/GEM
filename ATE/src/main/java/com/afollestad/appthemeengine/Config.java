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

package com.afollestad.appthemeengine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;

import com.afollestad.appthemeengine.customizers.ATENavigationBarCustomizer;
import com.afollestad.appthemeengine.customizers.ATEStatusBarCustomizer;
import com.afollestad.appthemeengine.customizers.ATEToolbarCustomizer;
import com.afollestad.appthemeengine.util.ATEUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class Config implements ConfigKeys, ConfigInterface {

    private final Context mContext;
    private final String mKey;
    private final SharedPreferences.Editor mEditor;

    @SuppressLint("CommitPrefEdits")
    protected Config(@NonNull Context context, @Nullable String key) {
        mContext = context;
        if (key == null)
            mKey = getKey(context);
        else
            mKey = key;
        mEditor = prefs(context, key).edit();
    }

    @CheckResult
    @Override
    public boolean isConfigured() {
        return prefs(mContext, mKey).getBoolean(IS_CONFIGURED_KEY, false);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public boolean isConfigured(@IntRange(from = 0, to = Integer.MAX_VALUE) int version) {
        final SharedPreferences prefs = prefs(mContext, mKey);
        final int lastVersion = prefs.getInt(IS_CONFIGURED_VERSION_KEY, -1);
        if (version > lastVersion) {
            prefs.edit().putInt(IS_CONFIGURED_VERSION_KEY, version).commit();
            return false;
        }
        return true;
    }

    @Override
    public Config activityTheme(@StyleRes int theme) {
        final Resources r = mContext.getResources();
        final String name = r.getResourceName(theme);
        final String defType = r.getResourceTypeName(theme);
        mEditor.putString(KEY_ACTIVITY_THEME, name);
        mEditor.putString(KEY_ACTIVITY_THEME_DEFTYPE, defType);
        return this;
    }

    @Override
    public Config primaryColor(@ColorInt int color) {
        mEditor.putInt(KEY_PRIMARY_COLOR, color);
        if (autoGeneratePrimaryDark(mContext, mKey))
            primaryColorDark(ATEUtil.darkenColor(color));
        return this;
    }

    @Override
    public Config primaryColorRes(@ColorRes int colorRes) {
        return primaryColor(ContextCompat.getColor(mContext, colorRes));
    }

    @Override
    public Config primaryColorAttr(@AttrRes int colorAttr) {
        return primaryColor(ATEUtil.resolveColor(mContext, colorAttr));
    }

    @Override
    public Config primaryColorDark(@ColorInt int color) {
        mEditor.putInt(KEY_PRIMARY_COLOR_DARK, color);
        return this;
    }

    @Override
    public Config primaryColorDarkRes(@ColorRes int colorRes) {
        return primaryColorDark(ContextCompat.getColor(mContext, colorRes));
    }

    @Override
    public Config primaryColorDarkAttr(@AttrRes int colorAttr) {
        return primaryColorDark(ATEUtil.resolveColor(mContext, colorAttr));
    }

    @Override
    public Config accentColor(@ColorInt int color) {
        mEditor.putInt(KEY_ACCENT_COLOR, color);
        return this;
    }

    @Override
    public Config accentColorRes(@ColorRes int colorRes) {
        return accentColor(ContextCompat.getColor(mContext, colorRes));
    }

    @Override
    public Config accentColorAttr(@AttrRes int colorAttr) {
        return accentColor(ATEUtil.resolveColor(mContext, colorAttr));
    }

    @Override
    public Config statusBarColor(@ColorInt int color) {
        mEditor.putInt(KEY_STATUS_BAR_COLOR, color);
        return this;
    }

    @Override
    public Config statusBarColorRes(@ColorRes int colorRes) {
        return statusBarColor(ContextCompat.getColor(mContext, colorRes));
    }

    @Override
    public Config statusBarColorAttr(@AttrRes int colorAttr) {
        return statusBarColor(ATEUtil.resolveColor(mContext, colorAttr));
    }

    @Override
    public Config toolbarColor(@ColorInt int color) {
        mEditor.putInt(KEY_TOOLBAR_COLOR, color);
        return this;
    }

    @Override
    public Config toolbarColorRes(@ColorRes int colorRes) {
        return toolbarColor(ContextCompat.getColor(mContext, colorRes));
    }

    @Override
    public Config toolbarColorAttr(@AttrRes int colorAttr) {
        return toolbarColor(ATEUtil.resolveColor(mContext, colorAttr));
    }

    @Override
    public Config navigationBarColor(@ColorInt int color) {
        mEditor.putInt(KEY_NAVIGATION_BAR_COLOR, color);
        return this;
    }

    @Override
    public Config navigationBarColorRes(@ColorRes int colorRes) {
        return navigationBarColor(ContextCompat.getColor(mContext, colorRes));
    }

    @Override
    public Config navigationBarColorAttr(@AttrRes int colorAttr) {
        return navigationBarColor(ATEUtil.resolveColor(mContext, colorAttr));
    }

    @Override
    public Config textColorPrimary(@ColorInt int color) {
        mEditor.putInt(KEY_TEXT_COLOR_PRIMARY, color);
        return this;
    }

    @Override
    public Config textColorPrimaryRes(@ColorRes int colorRes) {
        return textColorPrimary(ContextCompat.getColor(mContext, colorRes));
    }

    @Override
    public Config textColorPrimaryAttr(@AttrRes int colorAttr) {
        return textColorPrimary(ATEUtil.resolveColor(mContext, colorAttr));
    }

    @Override
    public Config textColorSecondary(@ColorInt int color) {
        mEditor.putInt(KEY_TEXT_COLOR_SECONDARY, color);
        return this;
    }

    @Override
    public Config textColorSecondaryRes(@ColorRes int colorRes) {
        return textColorSecondary(ContextCompat.getColor(mContext, colorRes));
    }

    @Override
    public Config textColorSecondaryAttr(@AttrRes int colorAttr) {
        return textColorSecondary(ATEUtil.resolveColor(mContext, colorAttr));
    }

    @Override
    public Config coloredStatusBar(boolean colored) {
        mEditor.putBoolean(KEY_APPLY_PRIMARYDARK_STATUSBAR, colored);
        return this;
    }

    @Override
    public Config coloredActionBar(boolean applyToActionBar) {
        mEditor.putBoolean(KEY_APPLY_PRIMARY_SUPPORTAB, applyToActionBar);
        return this;
    }

    @Override
    public Config coloredNavigationBar(boolean applyToNavBar) {
        mEditor.putBoolean(KEY_APPLY_PRIMARY_NAVBAR, applyToNavBar);
        return this;
    }

    @Override
    public Config autoGeneratePrimaryDark(boolean autoGenerate) {
        mEditor.putBoolean(KEY_AUTO_GENERATE_PRIMARYDARK, autoGenerate);
        return this;
    }

    @Override
    public Config lightStatusBarMode(@LightStatusBarMode int mode) {
        mEditor.putInt(KEY_LIGHT_STATUS_BAR_MODE, mode);
        return this;
    }

    @Override
    public Config lightToolbarMode(@LightToolbarMode int mode) {
        mEditor.putInt(KEY_LIGHT_TOOLBAR_MODE, mode);
        return this;
    }

    @Override
    public Config navigationViewThemed(boolean themed) {
        mEditor.putBoolean(KEY_THEMED_NAVIGATION_VIEW, themed);
        return this;
    }

    @Override
    public Config navigationViewSelectedIcon(@ColorInt int color) {
        mEditor.putInt(KEY_NAVIGATIONVIEW_SELECTED_ICON, color);
        return this;
    }

    @Override
    public Config navigationViewSelectedIconRes(@ColorRes int colorRes) {
        return navigationViewSelectedIcon(ContextCompat.getColor(mContext, colorRes));
    }

    @Override
    public Config navigationViewSelectedIconAttr(@AttrRes int colorAttr) {
        return navigationViewSelectedIcon(ATEUtil.resolveColor(mContext, colorAttr));
    }

    @Override
    public Config navigationViewSelectedText(@ColorInt int color) {
        mEditor.putInt(KEY_NAVIGATIONVIEW_SELECTED_TEXT, color);
        return this;
    }

    @Override
    public Config navigationViewSelectedTextRes(@ColorRes int colorRes) {
        return navigationViewSelectedText(ContextCompat.getColor(mContext, colorRes));
    }

    @Override
    public Config navigationViewSelectedTextAttr(@AttrRes int colorAttr) {
        return navigationViewSelectedText(ATEUtil.resolveColor(mContext, colorAttr));
    }

    @Override
    public Config navigationViewNormalIcon(@ColorInt int color) {
        mEditor.putInt(KEY_NAVIGATIONVIEW_NORMAL_ICON, color);
        return this;
    }

    @Override
    public Config navigationViewNormalIconRes(@ColorRes int colorRes) {
        return navigationViewNormalIcon(ContextCompat.getColor(mContext, colorRes));
    }

    @Override
    public Config navigationViewNormalIconAttr(@AttrRes int colorAttr) {
        return navigationViewNormalIcon(ATEUtil.resolveColor(mContext, colorAttr));
    }

    @Override
    public Config navigationViewNormalText(@ColorInt int color) {
        mEditor.putInt(KEY_NAVIGATIONVIEW_NORMAL_TEXT, color);
        return this;
    }

    @Override
    public Config navigationViewNormalTextRes(@ColorRes int colorRes) {
        return navigationViewNormalText(ContextCompat.getColor(mContext, colorRes));
    }

    @Override
    public Config navigationViewNormalTextAttr(@AttrRes int colorAttr) {
        return navigationViewNormalText(ATEUtil.resolveColor(mContext, colorAttr));
    }

    @Override
    public Config navigationViewSelectedBg(@ColorInt int color) {
        mEditor.putInt(KEY_NAVIGATIONVIEW_SELECTED_BG, color);
        return this;
    }

    @Override
    public Config navigationViewSelectedBgRes(@ColorRes int colorRes) {
        return navigationViewSelectedBg(ContextCompat.getColor(mContext, colorRes));
    }

    @Override
    public Config navigationViewSelectedBgAttr(@AttrRes int colorAttr) {
        return navigationViewSelectedBg(ATEUtil.resolveColor(mContext, colorAttr));
    }

    // Text size

    @Override
    public Config textSizePxForMode(@IntRange(from = 1, to = Integer.MAX_VALUE) int pxValue, @TextSizeMode String mode) {
        mEditor.putInt(mode, pxValue);
        return this;
    }

    @Override
    public Config textSizeSpForMode(@IntRange(from = 1, to = Integer.MAX_VALUE) int spValue, @TextSizeMode String mode) {
        final int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, mContext.getResources().getDisplayMetrics());
        return textSizePxForMode(px, mode);
    }

    @Override
    public Config textSizeResForMode(@DimenRes int resId, @TextSizeMode String mode) {
        return textSizePxForMode(mContext.getResources().getDimensionPixelSize(resId), mode);
    }

    // Apply and commit methods

    @SuppressWarnings("unchecked")
    @Override
    public void commit() {
        mEditor.putLong(VALUES_CHANGED, System.currentTimeMillis())
                .putBoolean(IS_CONFIGURED_KEY, true)
                .commit();
    }

    @Override
    public void apply(@NonNull Activity activity) {
        commit();
        ATE.postApply(activity, mKey);
    }

//    @Override
//    public void themeView(@NonNull android.support.v4.app.Fragment fragment) {
//        commit();
//        ATE.themeView(fragment, mKey);
//    }
//
//    @Override
//    public void themeView(@NonNull android.app.Fragment fragment) {
//        commit();
//        ATE.themeView(fragment, mKey);
//    }

    @Override
    public void apply(@NonNull View view) {
        commit();
        ATE.themeView(view.getContext(), view, mKey);
    }

    // Static getters

    @CheckResult
    @NonNull
    protected static SharedPreferences prefs(@NonNull Context context, @Nullable String key) {
        return context.getSharedPreferences(
                key != null ? String.format(CONFIG_PREFS_KEY_CUSTOM, key) : CONFIG_PREFS_KEY_DEFAULT,
                Context.MODE_PRIVATE);
    }

    public static void markChanged(@NonNull Context context, @Nullable String... keys) {
        if (keys == null) {
            new Config(context, null).commit();
        } else {
            for (String key : keys)
                new Config(context, key).commit();
        }
    }

    @Nullable
    protected static String getKey(@NonNull Context context) {
        if (context instanceof ATEActivity)
            return ((ATEActivity) context).getATEKey();
        return null;
    }

    @CheckResult
    @StyleRes
    public static int activityTheme(@NonNull Context context, @Nullable String key) {
        final SharedPreferences prefs = prefs(context, key);
        final String valueStr = prefs.getString(KEY_ACTIVITY_THEME, null);
        String valueTypeStr = prefs.getString(KEY_ACTIVITY_THEME_DEFTYPE, null);
        if (valueStr != null) {
            if (valueTypeStr == null) valueTypeStr = "style";
            return context.getResources().getIdentifier(valueStr, valueTypeStr, context.getPackageName());
        }
        return 0;
    }

    @CheckResult
    @ColorInt
    public static int primaryColor(@NonNull Context context, @Nullable String key) {
        return prefs(context, key).getInt(KEY_PRIMARY_COLOR, ATEUtil.resolveColor(context, R.attr.colorPrimary, Color.parseColor("#455A64")));
    }

    @CheckResult
    @ColorInt
    public static int primaryColorDark(@NonNull Context context, @Nullable String key) {
        return prefs(context, key).getInt(KEY_PRIMARY_COLOR_DARK, ATEUtil.resolveColor(context, R.attr.colorPrimaryDark, Color.parseColor("#37474F")));
    }

    @CheckResult
    @ColorInt
    public static int accentColor(@NonNull Context context, @Nullable String key) {
        return prefs(context, key).getInt(KEY_ACCENT_COLOR, ATEUtil.resolveColor(context, R.attr.colorAccent, Color.parseColor("#263238")));
    }

    @CheckResult
    @ColorInt
    public static int statusBarColor(@NonNull Context context, @Nullable String key) {
        if (context instanceof ATEStatusBarCustomizer) {
            final int color = ((ATEStatusBarCustomizer) context).getStatusBarColor();
            if (color != ATE.USE_DEFAULT) return color;
        } else if (!coloredStatusBar(context, key)) {
            return Color.BLACK;
        }
        return prefs(context, key).getInt(KEY_STATUS_BAR_COLOR, primaryColorDark(context, key));
    }

    @CheckResult
    @ColorInt
    public static int toolbarColor(@NonNull Context context, @Nullable String key, @Nullable Toolbar toolbar) {
        if (context instanceof ATEToolbarCustomizer) {
            int color = ((ATEToolbarCustomizer) context).getToolbarColor(toolbar);
            if (color != ATE.USE_DEFAULT) return color;
        }
        return prefs(context, key).getInt(KEY_TOOLBAR_COLOR, primaryColor(context, key));
    }

    @CheckResult
    @ColorInt
    public static int navigationBarColor(@NonNull Context context, @Nullable String key) {
        if (context instanceof ATENavigationBarCustomizer) {
            int color = ((ATENavigationBarCustomizer) context).getNavigationBarColor();
            if (color != ATE.USE_DEFAULT) return color;
        } else if (!coloredNavigationBar(context, key)) {
            return Color.BLACK;
        }
        return prefs(context, key).getInt(KEY_NAVIGATION_BAR_COLOR, primaryColor(context, key));
    }

    @CheckResult
    @ColorInt
    public static int textColorPrimary(@NonNull Context context, @Nullable String key) {
        return prefs(context, key).getInt(KEY_TEXT_COLOR_PRIMARY, ATEUtil.resolveColor(context, android.R.attr.textColorPrimary));
    }

    @CheckResult
    @ColorInt
    public static int textColorPrimaryInverse(@NonNull Context context, @Nullable String key) {
        return prefs(context, key).getInt(KEY_TEXT_COLOR_PRIMARY_INVERSE, ATEUtil.resolveColor(context, android.R.attr.textColorPrimaryInverse));
    }

    @CheckResult
    @ColorInt
    public static int textColorSecondary(@NonNull Context context, @Nullable String key) {
        return prefs(context, key).getInt(KEY_TEXT_COLOR_SECONDARY, ATEUtil.resolveColor(context, android.R.attr.textColorSecondary));
    }

    @CheckResult
    @ColorInt
    public static int textColorSecondaryInverse(@NonNull Context context, @Nullable String key) {
        return prefs(context, key).getInt(KEY_TEXT_COLOR_SECONDARY_INVERSE, ATEUtil.resolveColor(context, android.R.attr.textColorSecondaryInverse));
    }

    @CheckResult
    public static boolean coloredStatusBar(@NonNull Context context, @Nullable String key) {
        return prefs(context, key).getBoolean(KEY_APPLY_PRIMARYDARK_STATUSBAR, true);
    }

    @CheckResult
    public static boolean coloredActionBar(@NonNull Context context, @Nullable String key) {
        return prefs(context, key).getBoolean(KEY_APPLY_PRIMARY_SUPPORTAB, true);
    }

    @CheckResult
    public static boolean coloredNavigationBar(@NonNull Context context, @Nullable String key) {
        return prefs(context, key).getBoolean(KEY_APPLY_PRIMARY_NAVBAR, false);
    }

    @SuppressWarnings("ResourceType")
    @CheckResult
    @LightStatusBarMode
    public static int lightStatusBarMode(@NonNull Context context, @Nullable String key) {
        if (context instanceof ATEStatusBarCustomizer) {
            int color = ((ATEStatusBarCustomizer) context).getLightStatusBarMode();
            if (color != 0) return color;
        }
        int value = prefs(context, key).getInt(KEY_LIGHT_STATUS_BAR_MODE, Config.LIGHT_STATUS_BAR_AUTO);
        if (value < 1) value = Config.LIGHT_STATUS_BAR_AUTO;
        return value;
    }

    @SuppressWarnings("ResourceType")
    @CheckResult
    @LightToolbarMode
    public static int lightToolbarMode(@NonNull Context context, @Nullable String key, @Nullable Toolbar toolbar) {
        if (context instanceof ATEToolbarCustomizer)
            return ((ATEToolbarCustomizer) context).getLightToolbarMode(toolbar);
        int value = prefs(context, key).getInt(KEY_LIGHT_TOOLBAR_MODE, Config.LIGHT_TOOLBAR_AUTO);
        if (value < 1) value = Config.LIGHT_TOOLBAR_AUTO;
        return value;
    }

    @CheckResult
    public static boolean autoGeneratePrimaryDark(@NonNull Context context, @Nullable String key) {
        return prefs(context, key).getBoolean(KEY_AUTO_GENERATE_PRIMARYDARK, true);
    }

    @CheckResult
    public static boolean navigationViewThemed(@NonNull Context context, @Nullable String key) {
        return prefs(context, key).getBoolean(KEY_THEMED_NAVIGATION_VIEW, true);
    }

    @CheckResult
    @ColorInt
    public static int navigationViewSelectedIcon(@NonNull Context context, @Nullable String key, boolean darkTheme) {
        int defaultColor = primaryColor(context, key);
        if (darkTheme != ATEUtil.isColorLight(defaultColor))
            defaultColor = ATEUtil.invertColor(defaultColor);
        return prefs(context, key).getInt(KEY_NAVIGATIONVIEW_SELECTED_ICON, defaultColor);
    }

    @CheckResult
    @ColorInt
    public static int navigationViewSelectedText(@NonNull Context context, @Nullable String key, boolean darkTheme) {
        int defaultColor = primaryColor(context, key);
        if (darkTheme != ATEUtil.isColorLight(defaultColor))
            defaultColor = ATEUtil.invertColor(defaultColor);
        return prefs(context, key).getInt(KEY_NAVIGATIONVIEW_SELECTED_TEXT, defaultColor);
    }

    @CheckResult
    @ColorInt
    public static int navigationViewNormalIcon(@NonNull Context context, @Nullable String key, boolean darkTheme) {
        final int defaultColor = ContextCompat.getColor(context, darkTheme ?
                R.color.ate_icon_dark : R.color.ate_icon_light);
        return prefs(context, key).getInt(KEY_NAVIGATIONVIEW_NORMAL_ICON, defaultColor);
    }

    @CheckResult
    @ColorInt
    public static int navigationViewNormalText(@NonNull Context context, @Nullable String key, boolean darkTheme) {
        final int defaultColor = ContextCompat.getColor(context, darkTheme ?
                R.color.ate_primary_text_dark : R.color.ate_primary_text_light);
        return prefs(context, key).getInt(KEY_NAVIGATIONVIEW_NORMAL_TEXT, defaultColor);
    }

    @CheckResult
    @ColorInt
    public static int navigationViewSelectedBg(@NonNull Context context, @Nullable String key, boolean darkTheme) {
        final int defaultColor = ContextCompat.getColor(context, darkTheme ?
                R.color.ate_navigation_drawer_selected_dark : R.color.ate_navigation_drawer_selected_light);
        return prefs(context, key).getInt(KEY_NAVIGATIONVIEW_SELECTED_BG, defaultColor);
    }

    @CheckResult
    @IntRange(from = 1, to = Integer.MAX_VALUE)
    public static int textSizeForMode(@NonNull Context context, @Nullable String key, @TextSizeMode String mode) {
        int size = prefs(context, key).getInt(mode, 0);
        if (size == 0) {
            switch (mode) {
                default:
                    throw new IllegalArgumentException(String.format("Unknown text size mode: %s", mode));
                case TEXTSIZE_CAPTION:
                    size = context.getResources().getDimensionPixelSize(R.dimen.ate_default_textsize_caption);
                    break;
                case TEXTSIZE_BODY:
                    size = context.getResources().getDimensionPixelSize(R.dimen.ate_default_textsize_body);
                    break;
                case TEXTSIZE_SUBHEADING:
                    size = context.getResources().getDimensionPixelSize(R.dimen.ate_default_textsize_subheading);
                    break;
                case TEXTSIZE_TITLE:
                    size = context.getResources().getDimensionPixelSize(R.dimen.ate_default_textsize_title);
                    break;
                case TEXTSIZE_HEADLINE:
                    size = context.getResources().getDimensionPixelSize(R.dimen.ate_default_textsize_headline);
                    break;
                case TEXTSIZE_DISPLAY1:
                    size = context.getResources().getDimensionPixelSize(R.dimen.ate_default_textsize_display1);
                    break;
                case TEXTSIZE_DISPLAY2:
                    size = context.getResources().getDimensionPixelSize(R.dimen.ate_default_textsize_display2);
                    break;
                case TEXTSIZE_DISPLAY3:
                    size = context.getResources().getDimensionPixelSize(R.dimen.ate_default_textsize_display3);
                    break;
                case TEXTSIZE_DISPLAY4:
                    size = context.getResources().getDimensionPixelSize(R.dimen.ate_default_textsize_display4);
                    break;
            }
        }
        return size;
    }


    @IntDef({LIGHT_STATUS_BAR_OFF, LIGHT_STATUS_BAR_ON, LIGHT_STATUS_BAR_AUTO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LightStatusBarMode {
    }

    @IntDef({LIGHT_TOOLBAR_OFF, LIGHT_TOOLBAR_ON, LIGHT_TOOLBAR_AUTO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LightToolbarMode {
    }

    @StringDef({TEXTSIZE_DISPLAY4, TEXTSIZE_DISPLAY3, TEXTSIZE_DISPLAY2, TEXTSIZE_DISPLAY1,
            TEXTSIZE_HEADLINE, TEXTSIZE_TITLE, TEXTSIZE_SUBHEADING, TEXTSIZE_BODY, TEXTSIZE_CAPTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TextSizeMode {
    }

    public static final int LIGHT_STATUS_BAR_AUTO = 1;
    public static final int LIGHT_STATUS_BAR_ON = 2;
    public static final int LIGHT_STATUS_BAR_OFF = 3;

    public static final int LIGHT_TOOLBAR_AUTO = 1;
    public static final int LIGHT_TOOLBAR_ON = 2;
    public static final int LIGHT_TOOLBAR_OFF = 3;

    public final static String TEXTSIZE_DISPLAY4 = "display4";
    public final static String TEXTSIZE_DISPLAY3 = "display3";
    public final static String TEXTSIZE_DISPLAY2 = "display2";
    public final static String TEXTSIZE_DISPLAY1 = "display1";
    public final static String TEXTSIZE_HEADLINE = "headline";
    public final static String TEXTSIZE_TITLE = "title";
    public final static String TEXTSIZE_SUBHEADING = "subheading";
    public final static String TEXTSIZE_BODY = "body";
    public final static String TEXTSIZE_CAPTION = "caption";

    public static boolean isLightToolbar(@NonNull Context context, @Nullable Toolbar toolbar, @Nullable String key, @ColorInt int toolbarColor) {
        @Config.LightToolbarMode
        final int lightToolbarMode = Config.lightToolbarMode(context, key, toolbar);
        switch (lightToolbarMode) {
            case Config.LIGHT_TOOLBAR_ON:
                return true;
            case Config.LIGHT_TOOLBAR_OFF:
                return false;
            default:
            case Config.LIGHT_TOOLBAR_AUTO:
                return ATEUtil.isColorLight(toolbarColor);
        }
    }

    @ColorInt
    public static int getToolbarTitleColor(@NonNull Context context, @Nullable Toolbar toolbar, @Nullable String key) {
        final int toolbarColor = Config.toolbarColor(context, key, toolbar);
        return getToolbarTitleColor(context, toolbar, key, toolbarColor);
    }

    @ColorInt
    public static int getToolbarTitleColor(@NonNull Context context, @Nullable Toolbar toolbar, @Nullable String key, @ColorInt int toolbarColor) {
        return ContextCompat.getColor(context, isLightToolbar(context, toolbar, key, toolbarColor) ? R.color.ate_primary_text_light : R.color.ate_primary_text_dark);
    }

    @ColorInt
    public static int getToolbarSubtitleColor(@NonNull Context context, @Nullable Toolbar toolbar, @Nullable String key, @ColorInt int toolbarColor) {
        return ContextCompat.getColor(context, isLightToolbar(context, toolbar, key, toolbarColor) ? R.color.ate_secondary_text_light : R.color.ate_secondary_text_dark);
    }
}