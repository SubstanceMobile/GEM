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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.appthemeengine.util.ATEUtil;
import com.afollestad.appthemeengine.util.TextInputLayoutUtil;

/**
 * @author Aidan Follestad (afollestad)
 */
public class TextColorTagProcessor extends TagProcessor {

    public static final String PREFIX = "text_color";
    public static final String LINK_PREFIX = "text_color_link";
    public static final String HINT_PREFIX = "text_color_hint";

    private final boolean mLinkMode;
    private final boolean mHintMode;

    public TextColorTagProcessor(boolean links, boolean hints) {
        mLinkMode = links;
        mHintMode = hints;
    }

    @Override
    public boolean isTypeSupported(@NonNull View view) {
        return view instanceof TextView;
    }

    // TODO is dependent parameter needed?
    private static ColorStateList getTextSelector(@ColorInt int color, View view, boolean dependent) {
        if (dependent)
            color = ATEUtil.isColorLight(color) ? Color.BLACK : Color.WHITE;
        return new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled}
        }, new int[]{
                // Buttons are gray when disabled, so the text needs to be black
                view instanceof Button ? Color.BLACK : ATEUtil.adjustAlpha(color, 0.3f),
                color
        });
    }

    @Override
    public void process(@NonNull Context context, @Nullable String key, @NonNull View view, @NonNull String suffix) {
        final TextView tv = (TextView) view;
        final ColorResult result = getColorFromSuffix(context, key, view, suffix);
        if (result == null) return;

        if (mHintMode)
            result.adjustAlpha(0.5f);

        final ColorStateList sl = getTextSelector(result.getColor(), view, false);
        if (mLinkMode) {
            tv.setLinkTextColor(sl);
        } else if (mHintMode) {
            tv.setHintTextColor(sl);
            // Sets parent TextInputLayout hint color
            if (view.getParent() != null && view.getParent() instanceof TextInputLayout) {
                final TextInputLayout til = (TextInputLayout) view.getParent();
                TextInputLayoutUtil.setHint(til, result.getColor());
            }
        } else {
            tv.setTextColor(sl);
        }
    }
}