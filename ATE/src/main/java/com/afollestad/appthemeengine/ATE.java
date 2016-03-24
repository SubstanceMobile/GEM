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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.afollestad.appthemeengine.customizers.ATETaskDescriptionCustomizer;
import com.afollestad.appthemeengine.inflation.PostInflationApplier;
import com.afollestad.appthemeengine.inflation.ViewInterface;
import com.afollestad.appthemeengine.util.ATEUtil;
import com.afollestad.appthemeengine.util.MDUtil;
import com.afollestad.appthemeengine.util.TintHelper;
import com.afollestad.appthemeengine.viewprocessors.ViewProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class ATE extends ATEBase {

    public static final String IGNORE_TAG = "ate_ignore";
    public static final int USE_DEFAULT = Integer.MAX_VALUE;

    /**
     * @hide
     */
    public static <T extends View> void addPostInflationView(T view) {
        if (mPostInflationApply == null)
            mPostInflationApply = new ArrayList<>();
        mPostInflationApply.add(view);
    }

    private static ArrayList<View> mPostInflationApply;

    @SuppressWarnings("unchecked")
    private static void performDefaultProcessing(@NonNull Context context, @NonNull View current, @Nullable String key) {
        if (current.getTag() != null && current.getTag() instanceof String) {
            // Apply default processor to view if view's tag is a String
            ViewProcessor viewProcessor = getViewProcessor(null); // gets default viewProcessor
            if (viewProcessor != null)
                viewProcessor.process(context, key, current, null);
        }
    }

    public static void cleanup() {
        if (mPostInflationApply != null) {
            mPostInflationApply.clear();
            mPostInflationApply = null;
        }
    }

    public static Config config(@NonNull Context context, @Nullable String key) {
        return new Config(context, key);
    }

    @SuppressLint("CommitPrefEdits")
    private static boolean didValuesChange(@NonNull Context context, long updateTime, @Nullable String key) {
        return ATE.config(context, key).isConfigured() && Config.prefs(context, key).getLong(Config.VALUES_CHANGED, -1) > updateTime;
    }

    public static boolean invalidateActivity(final @NonNull Activity activity, long updateTime, @Nullable String ateKey) {
        if (ATE.didValuesChange(activity, updateTime, ateKey)) {
            // hack to prevent java.lang.RuntimeException: Performing pause of activity that is not resumed
            // makes sure recreate() is called right after and not in onResume()
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    activity.recreate();
                }
            });
            return true;
        }
        return false;
    }

    public static void preApply(@NonNull Activity activity, @Nullable String key) {
        didPreApply = activity.getClass();
        synchronized (IGNORE_TAG) {
            if (mPostInflationApply != null) {
                mPostInflationApply.clear();
                mPostInflationApply = null;
            }
        }
        int activityTheme = activity instanceof ATEActivityThemeCustomizer ?
                ((ATEActivityThemeCustomizer) activity).getActivityTheme() : Config.activityTheme(activity, key);
        if (activityTheme != 0) activity.setTheme(activityTheme);

        final LayoutInflater li = activity.getLayoutInflater();
        ATEUtil.setInflaterFactory(li, activity);
    }

    public static View getRootView(Activity activity) {
        return ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
    }

    @SuppressWarnings("unchecked")
    private static void performMainTheming(@NonNull Activity activity, @Nullable String key) {
        final View rootView = getRootView(activity);
        final boolean rootSetsStatusBarColor = rootView != null && rootView instanceof ViewInterface &&
                ((ViewInterface) rootView).setsStatusBarColor();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = activity.getWindow();
            if (!rootSetsStatusBarColor) {
                if (Config.coloredStatusBar(activity, key))
                    window.setStatusBarColor(Config.statusBarColor(activity, key));
                else window.setStatusBarColor(Color.BLACK);
                invalidateLightStatusBar(activity, key);
            }
            if (Config.coloredNavigationBar(activity, key))
                window.setNavigationBarColor(Config.navigationBarColor(activity, key));
            else window.setNavigationBarColor(Color.BLACK);
            applyTaskDescription(activity, key);
        }
    }

    public static void invalidateLightStatusBar(@NonNull Activity activity, @Nullable String key) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final View decorView = activity.getWindow().getDecorView();
            boolean lightStatusEnabled = false;
            if (Config.coloredStatusBar(activity, key)) {
                final int lightStatusMode = Config.lightStatusBarMode(activity, key);
                switch (lightStatusMode) {
                    case Config.LIGHT_STATUS_BAR_OFF:
                    default:
                        break;
                    case Config.LIGHT_STATUS_BAR_ON:
                        lightStatusEnabled = true;
                        break;
                    case Config.LIGHT_STATUS_BAR_AUTO:
                        lightStatusEnabled = ATEUtil.isColorLight(Config.primaryColor(activity, key));
                        break;
                }
            }

            final int systemUiVisibility = decorView.getSystemUiVisibility();
            if (lightStatusEnabled) {
                decorView.setSystemUiVisibility(systemUiVisibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decorView.setSystemUiVisibility(systemUiVisibility & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    public static void themeView(@NonNull View view, @Nullable String key) {
        if (view.getContext() == null)
            throw new IllegalStateException("View has no Context, use apply(Context, View, String) instead.");
        themeView(view.getContext(), view, key);
    }

    @SuppressWarnings("unchecked")
    public static void themeView(@NonNull Context context, @NonNull View view, @Nullable String key) {
        if (IGNORE_TAG.equals(view.getTag())) return;
        performDefaultProcessing(context, view, key);

        final ViewProcessor viewProcessor = getViewProcessor(view.getClass());
        if (viewProcessor != null) {
            // Apply view theming using processors, if any match
            viewProcessor.process(context, key, view, null);
        }
    }

    /**
     * @deprecated use postApply() instead. This method will throw an Exception.
     */
    @Deprecated
    public static void apply(@NonNull Activity activity, @Nullable String key) throws IllegalStateException {
        throw new IllegalStateException("ATE.apply() is no longer used, ATE intercepts views at inflation time. Use postApply() here instead.");
    }

    @SuppressWarnings("unchecked")
    public static void postApply(@NonNull Activity activity, @Nullable String key) {
        if (didPreApply == null)
            preApply(activity, key);
        performMainTheming(activity, key);

        final View rootView = getRootView(activity);
        final boolean rootSetsToolbarColor = rootView != null && rootView instanceof ViewInterface &&
                ((ViewInterface) rootView).setsToolbarColor();

        if (!rootSetsToolbarColor && Config.coloredActionBar(activity, key)) {
            if (activity instanceof AppCompatActivity) {
                final AppCompatActivity aca = (AppCompatActivity) activity;
                if (aca.getSupportActionBar() != null) {
                    ViewProcessor toolbarViewProcessor = getViewProcessor(Toolbar.class);
                    if (toolbarViewProcessor != null) {
                        // The processor handles retrieving the toolbar from the support action bar
                        toolbarViewProcessor.process(activity, key, null, null);
                    }
                }
            } else if (activity.getActionBar() != null) {
                activity.getActionBar().setBackgroundDrawable(new ColorDrawable(Config.toolbarColor(activity, key, null)));
            }
        }

        if (mPostInflationApply != null) {
            synchronized (IGNORE_TAG) {
                for (View view : mPostInflationApply) {
                    if (view instanceof PostInflationApplier)
                        ((PostInflationApplier) view).postApply();
                    else ATE.themeView(activity, view, key);
                }
            }
        }

        if (ATEUtil.isInClassPath(MDUtil.MAIN_CLASS))
            MDUtil.initMdSupport(activity, key);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void applyTaskDescription(@NonNull Activity activity, @Nullable String key) {
        int color = 0;
        Bitmap icon = null;
        if (activity instanceof ATETaskDescriptionCustomizer) {
            final ATETaskDescriptionCustomizer customizer = (ATETaskDescriptionCustomizer) activity;
            color = customizer.getTaskDescriptionColor();
            icon = customizer.getTaskDescriptionIcon();
            if (color == ATE.USE_DEFAULT)
                color = Config.primaryColor(activity, key);
        } else {
            color = Config.primaryColor(activity, key);
        }

        // Task description requires fully opaque color
        color = ATEUtil.stripAlpha(color);
        // Default is app's launcher icon
        if (icon == null)
            icon = ((BitmapDrawable) activity.getApplicationInfo().loadIcon(activity.getPackageManager())).getBitmap();

        // Sets color of entry in the system recents page
        ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(
                (String) activity.getTitle(), icon, color);
        activity.setTaskDescription(td);
    }

    @Nullable
    private static Toolbar getPostInflationToolbar() {
        synchronized (IGNORE_TAG) {
            if (mPostInflationApply == null) return null;
            for (View view : mPostInflationApply) {
                if (view instanceof Toolbar)
                    return (Toolbar) view;
            }
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static void themeOverflow(@NonNull Activity activity, @Nullable String key) {
        final Toolbar toolbar = getPostInflationToolbar();
        final int toolbarColor = Config.toolbarColor(activity, key, toolbar);
        final int tintColor = Config.getToolbarTitleColor(activity, toolbar, key, toolbarColor);

        // Make sure toolbar is valid
        if(toolbar == null) return;

        // The collapse icon displays when action views are expanded (e.g. SearchView)
        try {
            final Field field = Toolbar.class.getDeclaredField("mCollapseIcon");
            field.setAccessible(true);
            Drawable collapseIcon = (Drawable) field.get(toolbar);
            if (collapseIcon != null)
                field.set(toolbar, TintHelper.createTintedDrawable(collapseIcon, tintColor));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (toolbar != null && toolbar.getParent() instanceof CollapsingToolbarLayout)
            return; // collapsing toolbar handles the overflow color
        ATEUtil.setOverflowButtonColor(activity, toolbar, tintColor);
    }

    private ATE() {
    }
}