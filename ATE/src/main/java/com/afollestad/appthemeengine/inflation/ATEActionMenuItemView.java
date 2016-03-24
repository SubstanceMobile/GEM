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

package com.afollestad.appthemeengine.inflation;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.MenuItemImpl;
import android.util.AttributeSet;
import android.view.View;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.ATEActivity;
import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.util.TintHelper;
import com.afollestad.appthemeengine.viewprocessors.ViewProcessor;

import java.lang.reflect.Field;

/**
 * @author Aidan Follestad (afollestad)
 */
class ATEActionMenuItemView extends ActionMenuItemView implements ViewInterface {

    public ATEActionMenuItemView(Context context) {
        super(context);
        init(context, null);
    }

    public ATEActionMenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public ATEActionMenuItemView(Context context, AttributeSet attrs, @Nullable ATEActivity keyContext) {
        super(context, attrs);
        init(context, keyContext);
    }

    private String mKey;
    private int mTintColor;
    private Drawable mIcon;
    private boolean mCheckedActionView;

    private void init(Context context, @Nullable ATEActivity keyContext) {
        if (keyContext == null && context instanceof ATEActivity)
            keyContext = (ATEActivity) context;
        mKey = null;
        if (keyContext != null)
            mKey = keyContext.getATEKey();

        if (mIcon != null)
            setIcon(mIcon); // invalidates initial icon tint
        else invalidateTintColor();

        ATE.themeView(context, this, mKey);
        setTextColor(mTintColor); // sets menu item text color
    }

    private void invalidateTintColor() {
        // TODO get a reference to toolbar instead of null here?
        final int mToolbarColor = Config.toolbarColor(getContext(), mKey, null);
        mTintColor = Config.getToolbarTitleColor(getContext(), null, mKey, mToolbarColor);
    }

    @Override
    public void setIcon(Drawable icon) {
        invalidateTintColor();
        mIcon = TintHelper.createTintedDrawable(icon, mTintColor);
        super.setIcon(mIcon);
        invalidateActionView();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        invalidateActionView();
    }

    @SuppressWarnings("unchecked")
    private void invalidateActionView() {
        if (mCheckedActionView) return;
        mCheckedActionView = true;
        View actionView = getActionView();
        if (actionView != null) {
            ViewProcessor processor = ATE.getViewProcessor(actionView.getClass());
            if (processor != null)
                processor.process(getContext(), mKey, actionView, null);
        }
    }

    @Nullable
    private View getActionView() {
        try {
            final Field itemData = getClass().getSuperclass().getDeclaredField("mItemData");
            itemData.setAccessible(true);
            final MenuItemImpl menuImpl = (MenuItemImpl) itemData.get(this);
            if (menuImpl == null) return null;
            return menuImpl.getActionView();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to get ActionView from an ActionMenuItemView: " + t.getLocalizedMessage(), t);
        }
    }

    @Override
    public boolean setsStatusBarColor() {
        return false;
    }

    @Override
    public boolean setsToolbarColor() {
        return false;
    }
}