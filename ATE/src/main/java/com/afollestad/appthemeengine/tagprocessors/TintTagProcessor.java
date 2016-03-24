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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.util.TextInputLayoutUtil;
import com.afollestad.appthemeengine.util.TintHelper;

/**
 * @author Aidan Follestad (afollestad)
 */
public class TintTagProcessor extends TagProcessor {

    public static final String PREFIX = "tint";
    public static final String BACKGROUND_PREFIX = "tint_background";
    public static final String SELECTOR_PREFIX_LIGHT = "tint_selector_lighter";
    public static final String SELECTOR_PREFIX = "tint_selector";

    private final boolean mBackgroundMode;
    private final boolean mSelectorMode;
    private final boolean mLightSelector;

    public TintTagProcessor(boolean backgroundMode, boolean selectorMode, boolean lighter) {
        mBackgroundMode = backgroundMode;
        mSelectorMode = selectorMode;
        mLightSelector = lighter;
    }

    @Override
    public boolean isTypeSupported(@NonNull View view) {
        return mBackgroundMode ||
                mSelectorMode ||
                view instanceof CheckBox ||
                view instanceof RadioButton ||
                view instanceof ProgressBar || // includes SeekBar
                view instanceof EditText ||
                view instanceof ImageView ||
                view instanceof Switch ||
                view instanceof SwitchCompat ||
                view instanceof CheckedTextView ||
                view instanceof Spinner;
    }

    @Override
    public void process(@NonNull Context context, @Nullable String key, @NonNull View view, @NonNull String suffix) {
        final ColorResult result;
        if (isAccentColorTinted(view)) {
            result = getAccentColorTintFromSuffix(context, key, view, suffix);
        } else {
            result = getColorFromSuffix(context, key, view, suffix);
        }
        if (result == null) return;

        if (mSelectorMode) {
            TintHelper.setTintSelector(view, result.getColor(), !mLightSelector, result.isDark(context));
        } else {
            TintHelper.setTintAuto(view, result.getColor(), mBackgroundMode, result.isDark(context));
        }

        if (view instanceof EditText) {
            // Sets accent (expanded hint) color of parent TextInputLayouts
            if (view.getParent() != null && view.getParent() instanceof TextInputLayout) {
                final TextInputLayout til = (TextInputLayout) view.getParent();
                TextInputLayoutUtil.setAccent(til, result.getColor());
            }
        }
    }

    /**
     * Returns the same as getColorFromSuffix, but adjusts the result of dependent colors
     * to be the accent color (used for activated state)
     */
    private static ColorResult getAccentColorTintFromSuffix(@NonNull Context context, @Nullable String key, @NonNull View view, @NonNull String suffix) {
        ColorResult result = getColorFromSuffix(context, key, view, suffix);
        if (result == null) return null;
        // Tinted dependent color should be the accent color (only base color is dependent)
        switch (suffix) {
            case PARENT_DEPENDENT:
            case PRIMARY_COLOR_DEPENDENT:
            case ACCENT_COLOR_DEPENDENT:
            case WINDOW_BG_DEPENDENT:
                result.setColor(Config.accentColor(context, key));
        }
        return result;
    }

    /**
     * Checks whether this view should be tinted with the accent color
     * when the tag is background dependent
     */
    private static boolean isAccentColorTinted(View view) {
        return view instanceof EditText;
    }
}