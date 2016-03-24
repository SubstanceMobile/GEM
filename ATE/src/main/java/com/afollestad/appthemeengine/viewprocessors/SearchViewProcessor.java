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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.R;
import com.afollestad.appthemeengine.util.ATEUtil;
import com.afollestad.appthemeengine.util.TintHelper;

import java.lang.reflect.Field;

/**
 * @author Aidan Follestad (afollestad)
 */
public class SearchViewProcessor implements ViewProcessor<View, Integer> {

    public static final String MAIN_CLASS = "android.support.v7.widget.SearchView";

    private void tintImageView(Object target, Field field, int tintColor) throws Exception {
        field.setAccessible(true);
        final ImageView imageView = (ImageView) field.get(target);
        if (imageView.getDrawable() != null)
            imageView.setImageDrawable(TintHelper.createTintedDrawable(imageView.getDrawable(), tintColor));
    }

    @Override
    public void process(@NonNull Context context, @Nullable String key, @Nullable View target, @Nullable Integer tintColor) {
        if (target == null)
            return;
        if (tintColor == null) {
            // TODO pass a toolbar here?
            final int toolbarColor = Config.toolbarColor(context, key, null);
            tintColor = Config.getToolbarTitleColor(context, null, key, toolbarColor);
        }
        final Class<?> cls = target.getClass();
        try {
            final Field mSearchSrcTextViewField = cls.getDeclaredField("mSearchSrcTextView");
            mSearchSrcTextViewField.setAccessible(true);
            final EditText mSearchSrcTextView = (EditText) mSearchSrcTextViewField.get(target);
            mSearchSrcTextView.setTextColor(tintColor);
            mSearchSrcTextView.setHintTextColor(ContextCompat.getColor(context, ATEUtil.isColorLight(tintColor) ? R.color.ate_text_disabled_dark : R.color.ate_text_disabled_light));
            TintHelper.setCursorTint(mSearchSrcTextView, tintColor);

            Field field = cls.getDeclaredField("mSearchButton");
            tintImageView(target, field, tintColor);
            field = cls.getDeclaredField("mGoButton");
            tintImageView(target, field, tintColor);
            field = cls.getDeclaredField("mCloseButton");
            tintImageView(target, field, tintColor);
            field = cls.getDeclaredField("mVoiceButton");
            tintImageView(target, field, tintColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
