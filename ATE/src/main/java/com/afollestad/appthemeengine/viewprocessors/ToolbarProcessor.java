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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.R;
import com.afollestad.appthemeengine.customizers.ATECollapsingTbCustomizer;
import com.afollestad.appthemeengine.inflation.ViewInterface;
import com.afollestad.appthemeengine.util.ATEUtil;
import com.afollestad.appthemeengine.util.TintHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ToolbarProcessor implements ViewProcessor<Toolbar, Menu> {

    public static final String MAIN_CLASS = "android.support.v7.widget.Toolbar";

    @SuppressWarnings("unchecked")
    @Override
    public void process(@NonNull Context context, @Nullable String key, @Nullable Toolbar toolbar, @Nullable Menu menu) {
        if (toolbar == null && context instanceof AppCompatActivity)
            toolbar = ATEUtil.getSupportActionBarView(((AppCompatActivity) context).getSupportActionBar());
        if (toolbar != null && ATE.IGNORE_TAG.equals(toolbar.getTag()))
            return;

        final int toolbarColor = Config.toolbarColor(context, key, toolbar);
        if (toolbar == null) {
            // No toolbar view, Activity might have another variation of Support ActionBar (e.g. window decor vs toolbar)
            if (context instanceof Activity) {
                final Activity activity = (Activity) context;
                final View rootView = ATE.getRootView(activity);
                final boolean rootSetsToolbarColor = rootView != null && rootView instanceof ViewInterface &&
                        ((ViewInterface) rootView).setsToolbarColor();

                if (!rootSetsToolbarColor) {
                    if (activity instanceof AppCompatActivity) {
                        final ActionBar ab = ((AppCompatActivity) activity).getSupportActionBar();
                        if (ab != null) ab.setBackgroundDrawable(new ColorDrawable(toolbarColor));
                    } else if (activity.getActionBar() != null) {
                        activity.getActionBar().setBackgroundDrawable(new ColorDrawable(toolbarColor));
                    }
                }
            }
            return;
        }

        if (menu == null)
            menu = toolbar.getMenu();
        final int tintColor = Config.getToolbarTitleColor(context, toolbar, key, toolbarColor);

        CollapsingToolbarLayout collapsingToolbar = null;
        if (toolbar.getParent() instanceof CollapsingToolbarLayout) {
            // Reset support action bar background to transparent in case it was set to something else previously
            ATEUtil.setBackgroundCompat(toolbar, new ColorDrawable(Color.TRANSPARENT));
            if (context instanceof AppCompatActivity) {
                final ActionBar ab = ((AppCompatActivity) context).getSupportActionBar();
                if (ab != null)
                    ab.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            collapsingToolbar = (CollapsingToolbarLayout) toolbar.getParent();
            collapsingToolbar.setStatusBarScrimColor(Config.statusBarColor(context, key));
            collapsingToolbar.setContentScrim(new ColorDrawable(toolbarColor));

            if (collapsingToolbar.getParent() instanceof AppBarLayout) {
                AppBarLayout appbarLayout = (AppBarLayout) collapsingToolbar.getParent();
                try {
                    appbarLayout.addOnOffsetChangedListener(new ScrimsOffsetListener(context, key, toolbar, collapsingToolbar, menu));
                } catch (Exception e) {
                    throw new RuntimeException("An error occurred while setting up the AppBarLayout offset listener.", e);
                }
            }
        } else if (toolbar.getParent() instanceof AppBarLayout) {
            ATEUtil.setBackgroundCompat((View) toolbar.getParent(), new ColorDrawable(toolbarColor));
        } else {
            ATEUtil.setBackgroundCompat(toolbar, new ColorDrawable(toolbarColor));
        }

        // Tint the toolbar title
        if (collapsingToolbar != null)
            collapsingToolbar.setCollapsedTitleTextColor(tintColor);
        else toolbar.setTitleTextColor(tintColor);
        toolbar.setSubtitleTextColor(Config.getToolbarSubtitleColor(context, toolbar, key, toolbarColor));

        // Tint the toolbar navigation icon (e.g. back, drawer, etc.), otherwise handled by CollapsingToolbarLayout listener above
        if (collapsingToolbar == null && toolbar.getNavigationIcon() != null)
            toolbar.setNavigationIcon(TintHelper.createTintedDrawable(toolbar.getNavigationIcon(), tintColor));
    }

    public static class ScrimsOffsetListener implements AppBarLayout.OnOffsetChangedListener {

        private Object mCollapsingTextHelper;

        private final Field mExpandedTextColorField;
        private final Field mCollapsedTextColorField;
        private final Field mLastInsetsField;

        @NonNull
        private final Context mContext;
        private Toolbar mToolbar;
        private final CollapsingToolbarLayout mCollapsingToolbar;
        private Menu mMenu;
        private AppCompatImageView mOverflowView;

        private Drawable mOriginalNavIcon;
        private Drawable mOriginalOverflowIcon;

        private int mCollapsedColor;
        private int mExpandedColor;
        private int mLastVerticalOffset = 0;

        public ScrimsOffsetListener(@NonNull Context context, @Nullable String key, Toolbar toolbar,
                                    CollapsingToolbarLayout toolbarLayout, Menu menu) throws Exception {
            mContext = context;
            mToolbar = toolbar;
            mCollapsingToolbar = toolbarLayout;
            mMenu = menu;

            try {
                final Field textHelperField = CollapsingToolbarLayout.class.getDeclaredField("mCollapsingTextHelper");
                textHelperField.setAccessible(true);
                mCollapsingTextHelper = textHelperField.get(mCollapsingToolbar);
                final Class<?> textHelperCls = mCollapsingTextHelper.getClass();
                mExpandedTextColorField = textHelperCls.getDeclaredField("mExpandedTextColor");
                mExpandedTextColorField.setAccessible(true);
                mCollapsedTextColorField = textHelperCls.getDeclaredField("mCollapsedTextColor");
                mCollapsedTextColorField.setAccessible(true);

                mLastInsetsField = CollapsingToolbarLayout.class.getDeclaredField("mLastInsets");
                mLastInsetsField.setAccessible(true);

            } catch (Exception e) {
                throw new RuntimeException("Failed to get expanded text color or collapsed text color fields.", e);
            }

            if (context instanceof ATECollapsingTbCustomizer) {
                final ATECollapsingTbCustomizer customizer = (ATECollapsingTbCustomizer) mContext;
                mCollapsedColor = customizer.getCollapsedTintColor();
                mExpandedColor = customizer.getExpandedTintColor();

                if (mCollapsedColor == ATE.USE_DEFAULT || mExpandedColor == ATE.USE_DEFAULT) {
                    final int tintColor = Config.getToolbarTitleColor(context, toolbar, key);
                    if (mCollapsedColor == ATE.USE_DEFAULT) mCollapsedColor = tintColor;
                    if (mExpandedColor == ATE.USE_DEFAULT) mExpandedColor = tintColor;
                }

                mToolbar.setTitleTextColor(mCollapsedColor);
                mCollapsingToolbar.setCollapsedTitleTextColor(mCollapsedColor);
                mCollapsingToolbar.setExpandedTitleColor(mExpandedColor);
            }


            ATEUtil.waitForLayout(mCollapsingToolbar, new ATEUtil.LayoutCallback() {
                @Override
                public void onLayout(View view) {
                    invalidateMenu();
                }
            });
        }

        private int getExpandedTextColor() {
            if (mContext instanceof ATECollapsingTbCustomizer)
                return mExpandedColor;
            try {
                return mExpandedTextColorField.getInt(mCollapsingTextHelper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return Color.WHITE;
            }
        }

        private int getCollapsedTextColor() {
            if (mContext instanceof ATECollapsingTbCustomizer)
                return mCollapsedColor;
            try {
                return mCollapsedTextColorField.getInt(mCollapsingTextHelper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return Color.WHITE;
            }
        }

        private WindowInsetsCompat getLastInsets() {
            try {
                return (WindowInsetsCompat) mLastInsetsField.get(mCollapsingToolbar);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void invalidateMenu() {
            // Mimic CollapsingToolbarLayout's CollapsingTextHelper
            final WindowInsetsCompat mLastInsets = getLastInsets();
            final int insetTop = mLastInsets != null ? mLastInsets.getSystemWindowInsetTop() : 0;
            final int expandRange = mCollapsingToolbar.getHeight() - ViewCompat.getMinimumHeight(
                    mCollapsingToolbar) - insetTop;
            final float expansionFraction = Math.abs(mLastVerticalOffset) / (float) expandRange;

            int tintColor;
            if (getExpandedTextColor() == getCollapsedTextColor())
                tintColor = getExpandedTextColor();
            else
                tintColor = ATEUtil.blendColors(getExpandedTextColor(), getCollapsedTextColor(), expansionFraction);
            if (tintColor == Color.TRANSPARENT)
                tintColor = getExpandedTextColor();

            mToolbar.setTitleTextColor(tintColor);

            // Tint navigation icon, if any
            if (mOriginalNavIcon == null)
                mOriginalNavIcon = mToolbar.getNavigationIcon();
            if (mOriginalNavIcon != null)
                mToolbar.setNavigationIcon(TintHelper.createTintedDrawable(mOriginalNavIcon, tintColor));

            // Tint action buttons
            tintMenu(mToolbar, mMenu, tintColor);

            // Tint overflow
            if (mOriginalOverflowIcon == null) {
                final ArrayList<View> overflows = new ArrayList<>();
                @SuppressLint("PrivateResource")
                final String overflowDescription = mContext.getString(R.string.abc_action_menu_overflow_description);
                mCollapsingToolbar.findViewsWithText(overflows, overflowDescription,
                        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (!overflows.isEmpty()) {
                    mOverflowView = (AppCompatImageView) overflows.get(0);
                    mOriginalOverflowIcon = mOverflowView.getDrawable();
                }
            }
            if (mOverflowView != null)
                mOverflowView.setImageDrawable(TintHelper.createTintedDrawable(mOriginalOverflowIcon, tintColor));
        }

        // Some things done within this method might look weird, but they are all needed.
        @SuppressWarnings("unchecked")
        public static void tintMenu(@NonNull Toolbar toolbar, @Nullable Menu menu, final @ColorInt int color) {
            try {
                final Field field = Toolbar.class.getDeclaredField("mCollapseIcon");
                field.setAccessible(true);
                Drawable collapseIcon = (Drawable) field.get(toolbar);
                if (collapseIcon != null) {
                    field.set(toolbar, TintHelper.createTintedDrawable(collapseIcon, color));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // credits: https://snow.dog/blog/how-to-dynamicaly-change-android-toolbar-icons-color/
            final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
            for (int i = 0; i < toolbar.getChildCount(); i++) {
                final View v = toolbar.getChildAt(i);
                // We can't iterate through the toolbar.getMenu() here, because we need the ActionMenuItemView. ATEActionMenuItemView is overriding the item icon tint color.
                if (v instanceof ActionMenuView) {
                    for (int j = 0; j < ((ActionMenuView) v).getChildCount(); j++) {
                        final View innerView = ((ActionMenuView) v).getChildAt(j);
                        if (innerView instanceof ActionMenuItemView) {
                            int drawablesCount = ((ActionMenuItemView) innerView).getCompoundDrawables().length;
                            for (int k = 0; k < drawablesCount; k++) {
                                if (((ActionMenuItemView) innerView).getCompoundDrawables()[k] != null) {
                                    ((ActionMenuItemView) innerView).getCompoundDrawables()[k].setColorFilter(colorFilter);
                                }
                            }
                        }
                    }
                }
            }

            if (menu == null)
                menu = toolbar.getMenu();
            if (menu != null && menu.size() > 0) {
                for (int i = 0; i < menu.size(); i++) {
                    final MenuItem item = menu.getItem(i);
                    // We must iterate through the toolbar.getMenu() too, to keep the tint when resuming the paused activity.
                    if (item.getIcon() != null) {
                        item.setIcon(TintHelper.createTintedDrawable(item.getIcon(), color));
                    }
                    // Search view theming
                    if (item.getActionView() != null && (item.getActionView() instanceof android.widget.SearchView || item.getActionView() instanceof android.support.v7.widget.SearchView)) {
                        SearchViewTintUtil.setSearchViewContentColor(item.getActionView(), color);
                    }
                }
            }
        }

        private static final class SearchViewTintUtil {
            private static void tintImageView(Object target, Field field, final @ColorInt int color) throws Exception {
                field.setAccessible(true);
                final ImageView imageView = (ImageView) field.get(target);
                if (imageView.getDrawable() != null)
                    imageView.setImageDrawable(TintHelper.createTintedDrawable(imageView.getDrawable(), color));
            }

            public static void setSearchViewContentColor(View searchView, final @ColorInt int color) {
                if (searchView == null) return;
                final Class<?> cls = searchView.getClass();
                try {
                    final Field mSearchSrcTextViewField = cls.getDeclaredField("mSearchSrcTextView");
                    mSearchSrcTextViewField.setAccessible(true);
                    final EditText mSearchSrcTextView = (EditText) mSearchSrcTextViewField.get(searchView);
                    mSearchSrcTextView.setTextColor(color);
                    mSearchSrcTextView.setHintTextColor(ATEUtil.adjustAlpha(color, 0.5f));
                    TintHelper.setCursorTint(mSearchSrcTextView, color);

                    Field field = cls.getDeclaredField("mSearchButton");
                    tintImageView(searchView, field, color);
                    field = cls.getDeclaredField("mGoButton");
                    tintImageView(searchView, field, color);
                    field = cls.getDeclaredField("mCloseButton");
                    tintImageView(searchView, field, color);
                    field = cls.getDeclaredField("mVoiceButton");
                    tintImageView(searchView, field, color);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (mLastVerticalOffset != verticalOffset) {
                mLastVerticalOffset = verticalOffset;
                invalidateMenu();
            }
        }
    }
}