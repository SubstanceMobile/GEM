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

package com.afollestad.appthemeengine.tagprocessors;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.util.ATEUtil;

/**
 * @author Aidan Follestad (afollestad)
 */
public abstract class TagProcessor {

    public TagProcessor() {
    }

    public static class ColorResult {

        private int mColor;
        private boolean mDependent;
        private boolean mDark;

        public ColorResult(@ColorInt int color, boolean dependent, boolean dark) {
            mColor = color;
            mDependent = dependent;
            mDark = dark;
        }

        public void setColor(@ColorInt int color) {
            mColor = color;
        }

        public int getColor() {
            return mColor;
        }

        public boolean isDependent() {
            return mDependent;
        }

        public boolean isDark(Context context) {
            if (!mDependent) {
                // mDark wasn't loaded, determine from window background instead.
                final int windowBg = ATEUtil.resolveColor(context, android.R.attr.windowBackground);
                mDark = !ATEUtil.isColorLight(windowBg);
            }
            return mDark;
        }

        public void adjustAlpha(float factor) {
            mColor = ATEUtil.adjustAlpha(mColor, factor);
        }
    }

    /**
     * Returns null if no action should be taken in the tag processors now (e.g. if the view
     * is being added to the post inflation views).
     */
    @Nullable
    public static ColorResult getColorFromSuffix(@NonNull Context context, @Nullable String key, @NonNull View view, @NonNull String suffix) {
        @ColorInt
        final int result;
        boolean isDependent = false;
        boolean isDark = false;

        switch (suffix) {
            case PRIMARY_COLOR:
                result = Config.primaryColor(context, key);
                break;
            case PRIMARY_COLOR_DARK:
                result = Config.primaryColorDark(context, key);
                break;
            case ACCENT_COLOR:
                result = Config.accentColor(context, key);
                break;
            case PRIMARY_TEXT_COLOR:
                result = Config.textColorPrimary(context, key);
                break;
            case PRIMARY_TEXT_COLOR_INVERSE:
                result = Config.textColorPrimaryInverse(context, key);
                break;
            case SECONDARY_TEXT_COLOR:
                result = Config.textColorSecondary(context, key);
                break;
            case SECONDARY_TEXT_COLOR_INVERSE:
                result = Config.textColorSecondaryInverse(context, key);
                break;

            case PARENT_DEPENDENT: {
                if (view.getParent() == null) {
                    // Wait for post inflation when parents are assigned
                    ATE.addPostInflationView(view);
                    return null;
                }
                isDependent = true;
                final View firstBgView = getBackgroundView(view);
                if (firstBgView == null) {
                    Log.d("ATETagProcessor", "No parents with color drawables as backgrounds found, falling back to windowBackground.");
                    final int dependentColor = ATEUtil.resolveColor(context, android.R.attr.windowBackground);
                    isDark = !ATEUtil.isColorLight(dependentColor);
                    result = isDark ? Color.WHITE : Color.BLACK;
                } else {
                    final ColorDrawable bg = (ColorDrawable) firstBgView.getBackground();
                    isDark = !ATEUtil.isColorLight(bg.getColor());
                    result = isDark ? Color.WHITE : Color.BLACK;
                }
                break;
            }
            case PRIMARY_COLOR_DEPENDENT: {
                isDependent = true;
                isDark = !ATEUtil.isColorLight(Config.primaryColor(context, key));
                result = isDark ? Color.WHITE : Color.BLACK;
                break;
            }
            case ACCENT_COLOR_DEPENDENT: {
                isDependent = true;
                isDark = !ATEUtil.isColorLight(Config.accentColor(context, key));
                result = isDark ? Color.WHITE : Color.BLACK;
                break;
            }
            case WINDOW_BG_DEPENDENT: {
                isDependent = true;
                isDark = !ATEUtil.isColorLight(ATEUtil.resolveColor(context, android.R.attr.windowBackground));
                result = isDark ? Color.WHITE : Color.BLACK;
                break;
            }
            default:
                throw new IllegalArgumentException(String.format("Unknown suffix: %s", suffix));
        }
        return new ColorResult(result, isDependent, isDark);
    }

    @Nullable
    public static View getBackgroundView(View base) {
        View current = base;
        do {
            if (current.getBackground() != null && current.getBackground() instanceof ColorDrawable)
                return current;
            if (current.getParent() instanceof View)
                current = (View) current.getParent();
            else break;
        } while (current != null);
        return null;
    }

    public abstract boolean isTypeSupported(@NonNull View view);

    public abstract void process(@NonNull Context context, @Nullable String key, @NonNull View view, @NonNull String suffix);


    protected static final String PRIMARY_COLOR = "primary_color";
    protected static final String PRIMARY_COLOR_DARK = "primary_color_dark";
    protected static final String ACCENT_COLOR = "accent_color";
    protected static final String PRIMARY_TEXT_COLOR = "primary_text";
    protected static final String PRIMARY_TEXT_COLOR_INVERSE = "primary_text_inverse";
    protected static final String SECONDARY_TEXT_COLOR = "secondary_text";
    protected static final String SECONDARY_TEXT_COLOR_INVERSE = "secondary_text_inverse";

    protected static final String PARENT_DEPENDENT = "parent_dependent";
    protected static final String PRIMARY_COLOR_DEPENDENT = "primary_color_dependent";
    protected static final String ACCENT_COLOR_DEPENDENT = "accent_color_dependent";
    protected static final String WINDOW_BG_DEPENDENT = "window_bg_dependent";
}