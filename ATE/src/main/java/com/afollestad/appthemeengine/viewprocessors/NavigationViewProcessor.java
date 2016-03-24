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

package com.afollestad.appthemeengine.viewprocessors;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;

import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.util.ATEUtil;

/**
 * @author Aidan Follestad (afollestad)
 */
public class NavigationViewProcessor implements ViewProcessor<NavigationView, Void> {

    public static final String MAIN_CLASS = "android.support.design.widget.NavigationView";

    @Override
    public void process(@NonNull Context context, @Nullable String key, @Nullable NavigationView view, @Nullable Void extra) {
        if (view == null || !Config.navigationViewThemed(context, key))
            return;

        boolean darkTheme = false;
        if (view.getBackground() != null && view.getBackground() instanceof ColorDrawable) {
            final ColorDrawable cd = (ColorDrawable) view.getBackground();
            darkTheme = !ATEUtil.isColorLight(cd.getColor());
        }

        final ColorStateList iconSl = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        Config.navigationViewNormalIcon(context, key, darkTheme),
                        Config.navigationViewSelectedIcon(context, key, darkTheme)
                });
        final ColorStateList textSl = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        Config.navigationViewNormalText(context, key, darkTheme),
                        Config.navigationViewSelectedText(context, key, darkTheme)
                });
        view.setItemTextColor(textSl);
        view.setItemIconTintList(iconSl);

        StateListDrawable bgDrawable = new StateListDrawable();
        bgDrawable.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(
                Config.navigationViewSelectedBg(context, key, darkTheme)));
        view.setItemBackground(bgDrawable);

        // TODO not needed since the layout inflater will catch it?
//        final View headerView = view.getHeaderView(0);
//        if (headerView != null) ATE.themeView(context, headerView, key);
    }
}
