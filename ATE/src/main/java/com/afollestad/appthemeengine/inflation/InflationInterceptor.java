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

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.ATEActivity;
import com.afollestad.appthemeengine.tagprocessors.EdgeGlowTagProcessor;
import com.afollestad.appthemeengine.tagprocessors.TabLayoutTagProcessor;
import com.afollestad.appthemeengine.viewprocessors.NavigationViewProcessor;
import com.afollestad.appthemeengine.viewprocessors.ToolbarProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class InflationInterceptor implements LayoutInflaterFactory {

    private static final boolean LOGGING_ENABLED = true;

    private static void LOG(String msg, Object... args) {
        //noinspection PointlessBooleanExpression
        if (!LOGGING_ENABLED)
            return;
        if (args != null) {
            Log.d("InflationInterceptor", String.format(msg, args));
        } else {
            Log.d("InflationInterceptor", msg);
        }
    }

    @Nullable
    private final ATEActivity mKeyContext;
    @NonNull
    private final LayoutInflater mLi;
    @Nullable
    private AppCompatDelegate mDelegate;
    private static Method mOnCreateViewMethod;
    private static Method mCreateViewMethod;
    private static Field mConstructorArgsField;
    private static int[] ATTRS_THEME;

    public InflationInterceptor(@Nullable Activity keyContext, @NonNull LayoutInflater li, @Nullable AppCompatDelegate delegate) {
        if (keyContext instanceof ATEActivity)
            mKeyContext = (ATEActivity) keyContext;
        else mKeyContext = null;

        mLi = li;
        mDelegate = delegate;
        if (mOnCreateViewMethod == null) {
            try {
                mOnCreateViewMethod = LayoutInflater.class.getDeclaredMethod("onCreateView",
                        View.class, String.class, AttributeSet.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Failed to retrieve the onCreateView method.", e);
            }
        }
        if (mCreateViewMethod == null) {
            try {
                mCreateViewMethod = LayoutInflater.class.getDeclaredMethod("createView",
                        String.class, String.class, AttributeSet.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Failed to retrieve the createView method.", e);
            }
        }
        if (mConstructorArgsField == null) {
            try {
                mConstructorArgsField = LayoutInflater.class.getDeclaredField("mConstructorArgs");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Failed to retrieve the mConstructorArgs field.", e);
            }
        }
        if (ATTRS_THEME == null) {
            try {
                final Field attrsThemeField = LayoutInflater.class.getDeclaredField("ATTRS_THEME");
                attrsThemeField.setAccessible(true);
                ATTRS_THEME = (int[]) attrsThemeField.get(null);
            } catch (Throwable t) {
                t.printStackTrace();
                Log.d("InflationInterceptor", "Failed to get the value of static field ATTRS_THEME: " + t.getMessage());
            }
        }
        mOnCreateViewMethod.setAccessible(true);
        mCreateViewMethod.setAccessible(true);
        mConstructorArgsField.setAccessible(true);
    }

    private boolean isBlackListedForApply(String name) {
        return name.equals("android.support.design.internal.NavigationMenuItemView") ||
                name.equals("ViewStub") ||
                name.equals("fragment") ||
                name.equals("include") ||
                name.equals("android.support.design.internal.NavigationMenuItemView");
    }

    private boolean skipTheming(@Nullable View parent) {
        return parent != null && (ATE.IGNORE_TAG.equals(parent.getTag()) ||
                parent.getParent() != null && ATE.IGNORE_TAG.equals(((View) parent.getParent()).getTag()));
    }

    @Override
    public View onCreateView(View parent, final String name, Context context, AttributeSet attrs) {
        View view = null;

        if (!skipTheming(parent)) {
            switch (name) {
                case "android.support.v7.widget.AppCompatTextView":
                case "TextView":
                    view = new ATETextView(context, attrs, mKeyContext);
                    break;
                case "android.support.v7.widget.AppCompatEditText":
                case "EditText":
                    view = new ATEEditText(context, attrs, mKeyContext,
                            parent != null && parent instanceof TextInputLayout);
                    break;
                case "android.support.v7.widget.AppCompatAutoCompleteTextView":
                case "AutoCompleteTextView":
                    view = new ATEAutoCompleteTextView(context, attrs, mKeyContext,
                            parent != null && parent instanceof TextInputLayout);
                    break;
                case "android.support.v7.widget.AppCompatMultiAutoCompleteTextView":
                case "MultiAutoCompleteTextView":
                    view = new ATEMultiAutoCompleteTextView(context, attrs, mKeyContext,
                            parent != null && parent instanceof TextInputLayout);
                    break;
                case "android.support.v7.widget.AppCompatCheckBox":
                case "CheckBox":
                    view = new ATECheckBox(context, attrs, mKeyContext);
                    break;
                case "android.support.v7.widget.AppCompatRadioButton":
                case "RadioButton":
                    view = new ATERadioButton(context, attrs, mKeyContext);
                    break;
                case "Switch":
                    view = new ATEStockSwitch(context, attrs, mKeyContext);
                    break;
                case "android.support.v7.widget.SwitchCompat":
                    view = new ATESwitch(context, attrs, mKeyContext);
                    break;
                case "android.support.v7.widget.AppCompatSeekBar":
                case "SeekBar":
                    view = new ATESeekBar(context, attrs, mKeyContext);
                    break;
                case "ProgressBar":
                    view = new ATEProgressBar(context, attrs, mKeyContext);
                    break;
                case ToolbarProcessor.MAIN_CLASS:
                    ATEToolbar toolbar = new ATEToolbar(context, attrs, mKeyContext);
                    ATE.addPostInflationView(toolbar);
                    view = toolbar;
                    break;
                case "ListView":
                    view = new ATEListView(context, attrs, mKeyContext);
                    break;
                case "ScrollView":
                    view = new ATEScrollView(context, attrs, mKeyContext);
                    break;
                case "Spinner":
                    view = new ATEStockSpinner(context, attrs, mKeyContext);
                    break;
                case "android.support.v7.widget.AppCompatSpinner":
                    view = new ATESpinner(context, attrs, mKeyContext);
                    break;
                case "android.support.design.widget.FloatingActionButton":
                    view = new ATEFloatingActionButton(context, attrs, mKeyContext);
                    break;
                case EdgeGlowTagProcessor.RECYCLERVIEW_CLASS:
                    view = new ATERecyclerView(context, attrs, mKeyContext);
                    break;
                case EdgeGlowTagProcessor.NESTEDSCROLLVIEW_CLASS:
                    view = new ATENestedScrollView(context, attrs, mKeyContext);
                    break;
                case "android.support.v4.widget.DrawerLayout":
                    view = new ATEDrawerLayout(context, attrs, mKeyContext);
                    break;
                case NavigationViewProcessor.MAIN_CLASS:
                    view = new ATENavigationView(context, attrs, mKeyContext);
                    break;
                case TabLayoutTagProcessor.MAIN_CLASS:
                    view = new ATETabLayout(context, attrs, mKeyContext);
                    break;
                case EdgeGlowTagProcessor.VIEWPAGER_CLASS:
                    view = new ATEViewPager(context, attrs, mKeyContext);
                    break;
                case "android.support.design.widget.CoordinatorLayout":
                    view = new ATECoordinatorLayout(context, attrs, mKeyContext);
                    break;
                case "android.support.v7.view.menu.ActionMenuItemView":
                    view = new ATEActionMenuItemView(context, attrs, mKeyContext);
                    break;
                case "android.support.v7.widget.SearchView$SearchAutoComplete":
                    view = new ATESearchViewAutoComplete(context, attrs, mKeyContext);
                    break;
                case "CheckedTextView":
                    view = new ATECheckedTextView(context, attrs, mKeyContext);
                    break;
            }
        } else {
            LOG("Parent of " + name + " had the ate_ignore tag set, inflating default.");
        }

        if (view == null) {
            // First, check if the AppCompatDelegate will give us a view, usually (maybe always) null.
            if (mDelegate != null) {
                view = mDelegate.createView(parent, name, context, attrs);
                if (view == null && mKeyContext != null)
                    view = mKeyContext.onCreateView(parent, name, context, attrs);
                else view = null;
            } else {
                view = null;
            }

            if (isBlackListedForApply(name))
                return view;

            // Mimic code of LayoutInflater using reflection tricks (this would normally be run when this factory returns null).
            // We need to intercept the default behavior rather than allowing the LayoutInflater to handle it after this method returns.
            if (view == null) {
                try {
                    Context viewContext;
                    final boolean inheritContext = false; // TODO will this ever need to be true?
                    //noinspection PointlessBooleanExpression,ConstantConditions
                    if (parent != null && inheritContext) {
                        viewContext = parent.getContext();
                    } else {
                        viewContext = mLi.getContext();
                    }
                    // Apply a theme wrapper, if requested.
                    if (ATTRS_THEME != null) {
                        final TypedArray ta = viewContext.obtainStyledAttributes(attrs, ATTRS_THEME);
                        final int themeResId = ta.getResourceId(0, 0);
                        if (themeResId != 0) {
                            viewContext = new ContextThemeWrapper(viewContext, themeResId);
                        }
                        ta.recycle();
                    }

                    Object[] mConstructorArgs;
                    try {
                        mConstructorArgs = (Object[]) mConstructorArgsField.get(mLi);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to retrieve the mConstructorArgsField field.", e);
                    }

                    final Object lastContext = mConstructorArgs[0];
                    mConstructorArgs[0] = viewContext;
                    try {
                        if (-1 == name.indexOf('.')) {
                            view = (View) mOnCreateViewMethod.invoke(mLi, parent, name, attrs);
                        } else {
                            view = (View) mCreateViewMethod.invoke(mLi, name, null, attrs);
                        }
                    } catch (Exception e) {
                        LOG("Failed to inflate %s: %s", name, e.getMessage());
                        e.printStackTrace();
                    } finally {
                        mConstructorArgs[0] = lastContext;
                    }
                } catch (Throwable t) {
                    throw new RuntimeException(String.format("An error occurred while inflating View %s: %s", name, t.getMessage()), t);
                }
            }

            if (view != null && !skipTheming(parent)) {
                if (view.getClass().getSimpleName().startsWith("ATE"))
                    return view;
                String key = null;
                if (context instanceof ATEActivity)
                    key = ((ATEActivity) context).getATEKey();
                ATE.themeView(view, key);
            }
        }

        LOG("%s inflated to -> %s", name, view != null ? view.getClass().getName() : "(null)");
        return view;
    }
}