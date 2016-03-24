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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.afollestad.appthemeengine.util.ATEUtil;
import com.afollestad.appthemeengine.util.TintHelper;

/**
 * @author Aidan Follestad (afollestad)
 */
public class TabLayoutTagProcessor extends TagProcessor {

    public static final String MAIN_CLASS = "android.support.design.widget.TabLayout";

    public static final String TEXT_PREFIX = "tab_text";
    public static final String INDICATOR_PREFIX = "tab_indicator";

    private final static float UNFOCUSED_ALPHA = 0.5f;

    private final boolean mTextMode;
    private final boolean mIndicatorMode;

    public TabLayoutTagProcessor(boolean text, boolean indicator) {
        mTextMode = text;
        mIndicatorMode = indicator;
    }

    @Override
    public boolean isTypeSupported(@NonNull View view) {
        return view instanceof TabLayout;
    }

    @Override
    public void process(@NonNull Context context, @Nullable String key, @NonNull View view, @NonNull String suffix) {
        final TabLayout tl = (TabLayout) view;
        final ColorResult result = getColorFromSuffix(context, key, view, suffix);
        if (result == null) return;
        final int color = result.getColor();

        if (mTextMode) {
            tl.setTabTextColors(ATEUtil.adjustAlpha(color, UNFOCUSED_ALPHA), color);
        } else if (mIndicatorMode) {
            tl.setSelectedTabIndicatorColor(color);

            final ColorStateList sl = new ColorStateList(new int[][]{
                    new int[]{-android.R.attr.state_selected},
                    new int[]{android.R.attr.state_selected}
            },
                    new int[]{
                            ATEUtil.adjustAlpha(color, UNFOCUSED_ALPHA),
                            color
                    });
            for (int i = 0; i < tl.getTabCount(); i++) {
                final TabLayout.Tab tab = tl.getTabAt(i);
                if (tab != null && tab.getIcon() != null)
                    tab.setIcon(TintHelper.createTintedDrawable(tab.getIcon(), sl));
            }
        }
    }
}