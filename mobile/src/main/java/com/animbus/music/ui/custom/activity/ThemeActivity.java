package com.animbus.music.ui.custom.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.animbus.music.R;
import com.animbus.music.util.ColorUtil;
import com.animbus.music.util.IconManager;
import com.animbus.music.util.Options;

/**
 * Created by Adrian on 8/5/2015.
 */
public abstract class ThemeActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    protected AppBarLayout mAppBar;
    protected CoordinatorLayout mRoot;

    private int primary, accent, baseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getBaseTheme());
        super.onCreate(savedInstanceState);
        sequence();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        sequence();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Recreate if the theme changed
        if (primary != Options.getPrimaryColor() || accent != Options.getAccentColor() || baseId != Options.getBaseTheme())
            recreate();
    }

    /**
     * call this when adding code to the sequence of default methods. The default sequence can be triggered by
     * <code>super.sequence(savedInstanceState)</code>.
     * Everything before triggering will happen before, everything after will happen after. The default sequence is:
     * <code>init(savedInstanceState)</code>,
     * <code>setVariables()</code>,
     * <code>setSupportActionBar()</code>,
     * <code>setUp()</code>,
     * <code>setUpTheme()</code>
     */
    protected void sequence() {
        init();
        setVariables();
        setInternalVariables();
        setSupportActionBar(mToolbar);
        setUp();
        setUpTheme();
    }

    protected abstract void init();

    protected abstract void setVariables();

    private void setInternalVariables() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mAppBar = (AppBarLayout) findViewById(R.id.appbar);
        mRoot = (CoordinatorLayout) findViewById(R.id.root);
    }

    protected abstract void setUp();

    protected void setUpTheme() {
        primary = Options.getPrimaryColor();
        accent = Options.getAccentColor();
        baseId = Options.getBaseTheme();

        themeAppBar();
        setStatusBarColor(getPrimaryDarkColor());
        themeNavBar();
        themeBackground();
        configureTaskDescription(getPrimaryColor(), null);
    }

    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(color);
            if (Build.VERSION.SDK_INT >= 23 && ColorUtil.isLightColor(color))
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_LAYOUT_FLAGS | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            else
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_LAYOUT_FLAGS);
        } else if (Build.VERSION.SDK_INT == 19) getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    private void themeNavBar() {
        //Do nothing for now
    }

    protected void themeAppBar() {
        mAppBar.setBackgroundColor(getPrimaryColor());
        if (getPrimaryColor() == resolveColorAttr(android.R.attr.colorBackground) && !shouldKeepAppBarShadow()) ViewCompat.setElevation(mAppBar, 0.0f);
    }

    protected boolean shouldKeepAppBarShadow() {
        return false;
    }

    private void themeBackground() {
        mRoot.setBackgroundColor(resolveColorAttr(android.R.attr.windowBackground));
    }

    public void configureTaskDescription(@ColorInt int color, String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IconManager iconM = IconManager.get().setContext(this);
            Bitmap bm = BitmapFactory.decodeResource(getResources(), iconM.getDrawable(iconM.getOverviewIcon(iconM.getIcon()).getId()));
            setTaskDescription(new ActivityManager.TaskDescription(title, bm, color));
            bm.recycle();
        }
    }

    private int getBaseTheme() {
        switch (Options.getBaseTheme()) {
            case 0:
                return (!ColorUtil.isLightColor(getPrimaryColor()) ? R.style.Base : R.style.Base_LightActionBar);
            case 1:
                return (!ColorUtil.isLightColor(getPrimaryColor()) ? R.style.Base_Faithful : R.style.Base_Faithful_LightActionBar);
            case 2:
                return (!ColorUtil.isLightColor(getPrimaryColor()) ? R.style.Base_Light_DarkActionBar : R.style.Base_Light);
            default:
                return -1;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////

    @ColorInt
    public int getPrimaryColor() {
        return Options.getPrimaryColor();
    }

    @ColorInt
    public int getPrimaryDarkColor() {
        return ColorUtil.getDarkerColor(getPrimaryColor());
    }

    @ColorInt
    public int getAccentColor() {
        return Options.getAccentColor();
    }

    @ColorInt
    public int getPrimaryTextColor() {
        return Options.isLightTheme() ?
                ContextCompat.getColor(this, android.support.v7.appcompat.R.color.abc_primary_text_material_light) :
                ContextCompat.getColor(this, android.support.v7.appcompat.R.color.abc_primary_text_material_dark);
    }

    @ColorInt
    public int getPrimaryTextColor(@ColorInt int color) {
        return ColorUtil.isLightColor(color) ?
                ContextCompat.getColor(this, android.support.v7.appcompat.R.color.abc_primary_text_material_light) :
                ContextCompat.getColor(this, android.support.v7.appcompat.R.color.abc_primary_text_material_dark);
    }

    @ColorInt
    public int getSecondaryTextColor() {
        return Options.isLightTheme() ?
                ContextCompat.getColor(this, android.support.v7.appcompat.R.color.abc_secondary_text_material_light) :
                ContextCompat.getColor(this, android.support.v7.appcompat.R.color.abc_secondary_text_material_dark);
    }

    @ColorInt
    public int getSecondaryTextColor(@ColorInt int color) {
        return ColorUtil.isLightColor(color) ?
                ContextCompat.getColor(this, android.support.v7.appcompat.R.color.abc_secondary_text_material_light) :
                ContextCompat.getColor(this, android.support.v7.appcompat.R.color.abc_secondary_text_material_dark);
    }

    @ColorInt
    public int resolveColorAttr(@AttrRes int resId) {
        final TypedValue value = new TypedValue();
        getTheme().resolveAttribute(resId, value, true);
        return value.data;
    }

    public float resolveFloatAttr(@AttrRes int resId) {
        final TypedValue value = new TypedValue();
        getTheme().resolveAttribute(resId, value, true);
        return value.getFloat();
    }

    public ColorStateList resolveColorStateListAttr(@AttrRes int resId) {
        TypedArray a = obtainStyledAttributes(null, new int[]{resId});
        try {
            return a.getColorStateList(0);
        } finally {
            a.recycle();
        }
    }
}
