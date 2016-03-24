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
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.afollestad.appthemeengine.util.ATEUtil;
import com.afollestad.appthemeengine.util.EdgeGlowUtil;
import com.afollestad.appthemeengine.util.NestedScrollViewUtil;
import com.afollestad.appthemeengine.util.RecyclerViewUtil;
import com.afollestad.appthemeengine.util.ViewPagerUtil;

/**
 * @author Aidan Follestad (afollestad)
 */
public class EdgeGlowTagProcessor extends TagProcessor {

    public static final String NESTEDSCROLLVIEW_CLASS = "android.support.v4.widget.NestedScrollView";
    public static final String RECYCLERVIEW_CLASS = "android.support.v7.widget.RecyclerView";
    public static final String VIEWPAGER_CLASS = "android.support.v4.view.ViewPager";

    public static final String PREFIX = "edge_glow";

    @Override
    public boolean isTypeSupported(@NonNull View view) {
        return view instanceof ScrollView ||
                view instanceof AbsListView ||
                (ATEUtil.isInClassPath(NESTEDSCROLLVIEW_CLASS) && NestedScrollViewUtil.isNestedScrollView(view)) ||
                (ATEUtil.isInClassPath(RECYCLERVIEW_CLASS) && RecyclerViewUtil.isRecyclerView(view)) ||
                (ATEUtil.isInClassPath(VIEWPAGER_CLASS) && ViewPagerUtil.isViewPager(view));
    }

    @Override
    public void process(@NonNull Context context, @Nullable String key, @NonNull View view, @NonNull String suffix) {
        final ColorResult result = getColorFromSuffix(context, key, view, suffix);
        if (result == null)
            return;
        EdgeGlowUtil.setEdgeGlowColorAuto(view, result.getColor());
    }
}