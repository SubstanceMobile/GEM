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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.afollestad.appthemeengine.tagprocessors.BackgroundTagProcessor;
import com.afollestad.appthemeengine.tagprocessors.EdgeGlowTagProcessor;
import com.afollestad.appthemeengine.tagprocessors.FontTagProcessor;
import com.afollestad.appthemeengine.tagprocessors.TabLayoutTagProcessor;
import com.afollestad.appthemeengine.tagprocessors.TagProcessor;
import com.afollestad.appthemeengine.tagprocessors.TextColorTagProcessor;
import com.afollestad.appthemeengine.tagprocessors.TextShadowColorTagProcessor;
import com.afollestad.appthemeengine.tagprocessors.TextSizeTagProcessor;
import com.afollestad.appthemeengine.tagprocessors.TintTagProcessor;
import com.afollestad.appthemeengine.util.ATEUtil;
import com.afollestad.appthemeengine.viewprocessors.DefaultProcessor;
import com.afollestad.appthemeengine.viewprocessors.NavigationViewProcessor;
import com.afollestad.appthemeengine.viewprocessors.SearchViewProcessor;
import com.afollestad.appthemeengine.viewprocessors.ToolbarProcessor;
import com.afollestad.appthemeengine.viewprocessors.ViewProcessor;

import java.util.HashMap;

/**
 * @author Aidan Follestad (afollestad)
 */
class ATEBase {

    protected final static String DEFAULT_PROCESSOR = "[default]";

    private static HashMap<String, ViewProcessor> mViewProcessors;
    private static HashMap<String, TagProcessor> mTagProcessors;

    private static void initViewProcessors() {
        mViewProcessors = new HashMap<>(5);
        mViewProcessors.put(DEFAULT_PROCESSOR, new DefaultProcessor());

        mViewProcessors.put(SearchView.class.getName(), new SearchViewProcessor());
        mViewProcessors.put(Toolbar.class.getName(), new ToolbarProcessor());

        if (ATEUtil.isInClassPath(NavigationViewProcessor.MAIN_CLASS))
            mViewProcessors.put(NavigationViewProcessor.MAIN_CLASS, new NavigationViewProcessor());
        else Log.d("ATEBase", "NavigationView isn't in the class path. Ignoring.");
        if (ATEUtil.isInClassPath(SearchViewProcessor.MAIN_CLASS))
            mViewProcessors.put(SearchViewProcessor.MAIN_CLASS, new SearchViewProcessor());
        else Log.d("ATEBase", "SearchView isn't in the class path. Ignoring.");
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends View> ViewProcessor<T, ?> getViewProcessor(@Nullable Class<T> viewClass) {
        if (mViewProcessors == null)
            initViewProcessors();
        if (viewClass == null)
            return mViewProcessors.get(DEFAULT_PROCESSOR);
        ViewProcessor viewProcessor = mViewProcessors.get(viewClass.getName());
        if (viewProcessor != null)
            return viewProcessor;
        Class<?> current = viewClass;
        while (true) {
            current = current.getSuperclass();
            if (current == null || current.getName().equals(View.class.getName()))
                break;
            viewProcessor = mViewProcessors.get(current.getName());
            if (viewProcessor != null) break;
        }
        return viewProcessor;
    }

    public static <T extends View> void registerViewProcessor(@NonNull Class<T> viewCls, @NonNull ViewProcessor<T, ?> viewProcessor) {
        if (mViewProcessors == null)
            initViewProcessors();
        mViewProcessors.put(viewCls.getName(), viewProcessor);
    }

    private static void initTagProcessors() {
        mTagProcessors = new HashMap<>(14);
        mTagProcessors.put(BackgroundTagProcessor.PREFIX, new BackgroundTagProcessor());
        mTagProcessors.put(FontTagProcessor.PREFIX, new FontTagProcessor());
        mTagProcessors.put(TextColorTagProcessor.PREFIX, new TextColorTagProcessor(false, false));
        mTagProcessors.put(TextColorTagProcessor.LINK_PREFIX, new TextColorTagProcessor(true, false));
        mTagProcessors.put(TextColorTagProcessor.HINT_PREFIX, new TextColorTagProcessor(false, true));
        mTagProcessors.put(TextShadowColorTagProcessor.PREFIX, new TextShadowColorTagProcessor());
        mTagProcessors.put(TextSizeTagProcessor.PREFIX, new TextSizeTagProcessor());
        mTagProcessors.put(TintTagProcessor.PREFIX, new TintTagProcessor(false, false, false));
        mTagProcessors.put(TintTagProcessor.BACKGROUND_PREFIX, new TintTagProcessor(true, false, false));
        mTagProcessors.put(TintTagProcessor.SELECTOR_PREFIX, new TintTagProcessor(false, true, false));
        mTagProcessors.put(TintTagProcessor.SELECTOR_PREFIX_LIGHT, new TintTagProcessor(false, true, true));
        mTagProcessors.put(TabLayoutTagProcessor.TEXT_PREFIX, new TabLayoutTagProcessor(true, false));
        mTagProcessors.put(TabLayoutTagProcessor.INDICATOR_PREFIX, new TabLayoutTagProcessor(false, true));
        mTagProcessors.put(EdgeGlowTagProcessor.PREFIX, new EdgeGlowTagProcessor());
    }

    @Nullable
    public static TagProcessor getTagProcessor(@NonNull String prefix) {
        if (mTagProcessors == null)
            initTagProcessors();
        return mTagProcessors.get(prefix);
    }

    public static void registerTagProcessor(@NonNull String prefix, @NonNull TagProcessor tagProcessor) {
        if (mTagProcessors == null)
            initTagProcessors();
        mTagProcessors.put(prefix, tagProcessor);
    }

    protected static Class<?> didPreApply = null;
}