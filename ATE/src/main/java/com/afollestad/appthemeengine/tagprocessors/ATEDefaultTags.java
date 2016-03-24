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

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.appthemeengine.util.ATEUtil;
import com.afollestad.appthemeengine.util.NestedScrollViewUtil;
import com.afollestad.appthemeengine.util.RecyclerViewUtil;
import com.afollestad.appthemeengine.util.TabLayoutUtil;
import com.afollestad.appthemeengine.util.ViewPagerUtil;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class ATEDefaultTags {

    // TODO toolbars should use default tags instead of forcing toolbarColor()?

    public static void process(View view) {
        final HashMap<String, String> mDefaults = get(view);
        if (mDefaults == null || (view.getTag() != null && !(view.getTag() instanceof String)))
            return;
        if (view.getTag() != null) {
            final String tag = (String) view.getTag();
            final String[] split = tag.split(",");
            for (String part : split) {
                final int pipe = part.indexOf('|');
                if (pipe == -1) continue;
                final String prefix = part.substring(0, pipe);
                if (mDefaults.containsKey(prefix))
                    mDefaults.remove(prefix);
                if (mDefaults.isEmpty()) break;
            }
        }
        String tag = view.getTag() == null ? "" : (String) view.getTag();
        Log.d("ATEDefaultTags", String.format(Locale.getDefault(), "Before processing %s: %s", view.getClass().getSimpleName(), tag));
        for (String key : mDefaults.keySet()) {
            if (!tag.isEmpty()) tag = tag + ",";
            tag += String.format(Locale.getDefault(), "%s|%s", key, mDefaults.get(key));
        }
        view.setTag(tag);
        Log.d("ATEDefaultTags", String.format(Locale.getDefault(), "After processing %s: %s", view.getClass().getSimpleName(), tag));
    }

    @Nullable
    private static HashMap<String, String> get(View forView) {
        if (forView instanceof EditText)
            return getDefaultEditText();
        else if (forView instanceof CompoundButton)
            return getDefaultCompoundButton();
        else if (forView instanceof ProgressBar)
            return getDefaultWidget();
        else if (forView instanceof CheckedTextView)
            return getDefaultCheckedTextView();
        else if (forView instanceof FloatingActionButton)
            return getDefaultFloatingActionButton();
        else if (forView instanceof ScrollView || forView instanceof AbsListView)
            return getDefaultScrollableView();
        else if (ATEUtil.isInClassPath(EdgeGlowTagProcessor.RECYCLERVIEW_CLASS) &&
                RecyclerViewUtil.isRecyclerView(forView)) {
            return getDefaultScrollableView();
        } else if (ATEUtil.isInClassPath(EdgeGlowTagProcessor.NESTEDSCROLLVIEW_CLASS) &&
                NestedScrollViewUtil.isNestedScrollView(forView)) {
            return getDefaultScrollableView();
        } else if (ATEUtil.isInClassPath(EdgeGlowTagProcessor.VIEWPAGER_CLASS) &&
                ViewPagerUtil.isViewPager(forView)) {
            return getDefaultScrollableView();
        } else if (ATEUtil.isInClassPath(TabLayoutTagProcessor.MAIN_CLASS) &&
                TabLayoutUtil.isTabLayout(forView)) {
            return getDefaultTabLayout();
        } else if (forView instanceof TextView) {
            return getDefaultTextView();
        }
        return null;
    }

    private static HashMap<String, String> getDefaultTextView() {
        HashMap<String, String> map = new HashMap<>(1);
        map.put(TextColorTagProcessor.LINK_PREFIX, "accent_color");
        return map;
    }

    private static HashMap<String, String> getDefaultCheckedTextView() {
        HashMap<String, String> map = new HashMap<>(1);
        map.put(TintTagProcessor.PREFIX, "accent_color");
        return map;
    }

    private static HashMap<String, String> getDefaultFloatingActionButton() {
        HashMap<String, String> map = new HashMap<>(1);
        map.put(TintTagProcessor.SELECTOR_PREFIX_LIGHT, "accent_color");
        return map;
    }

    private static HashMap<String, String> getDefaultEditText() {
        HashMap<String, String> map = new HashMap<>(3);
        map.put(TintTagProcessor.PREFIX, "accent_color");
        map.put(TextColorTagProcessor.PREFIX, "primary_text");
        map.put(TextColorTagProcessor.HINT_PREFIX, "primary_text");
        return map;
    }

    private static HashMap<String, String> getDefaultCompoundButton() {
        HashMap<String, String> map = new HashMap<>(2);
        map.put(TintTagProcessor.PREFIX, "accent_color");
        map.put(TextColorTagProcessor.PREFIX, "primary_text");
        return map;
    }

    private static HashMap<String, String> getDefaultWidget() {
        HashMap<String, String> map = new HashMap<>(1);
        map.put(TintTagProcessor.PREFIX, "accent_color");
        return map;
    }

    private static HashMap<String, String> getDefaultScrollableView() {
        HashMap<String, String> map = new HashMap<>(1);
        map.put(EdgeGlowTagProcessor.PREFIX, "accent_color");
        return map;
    }

    private static HashMap<String, String> getDefaultTabLayout() {
        HashMap<String, String> map = new HashMap<>(2);
        map.put(TabLayoutTagProcessor.TEXT_PREFIX, "parent_dependent");
        map.put(TabLayoutTagProcessor.INDICATOR_PREFIX, "accent_color");
        return map;
    }

    private ATEDefaultTags() {
    }
}