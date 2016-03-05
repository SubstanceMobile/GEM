package com.animbus.music.ui.custom.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.MenuRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.ATEActivity;
import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.util.ATEUtil;
import com.animbus.music.R;
import com.animbus.music.ui.activity.search.SearchActivity;
import com.animbus.music.ui.activity.settings.Settings;
import com.animbus.music.util.GEMUtil;
import com.animbus.music.util.IconManager;

import butterknife.ButterKnife;

public abstract class ThemeActivity extends ATEActivity {
    public Toolbar mToolbar;
    public AppBarLayout mAppBar;
    public View mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sequence();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        sequence();
    }

    /**
     * call this when adding code to the sequence of default methods. The default sequence can be triggered by
     * <code>super.sequence()</code>.
     * Everything before triggering will happen before, everything after will happen after. The default sequence is:
     * {@link #init()},
     * {@link #setInternalVariables()},
     * {@link #setVariables()},
     * {@link ButterKnife#bind(Activity)},
     * {@link AppCompatActivity#setSupportActionBar(Toolbar)},
     * {@link #setUp()},
     * {@link #setUpTheme()}
     */
    protected void sequence() {
        init();
        setInternalVariables();
        setVariables();
        ButterKnife.bind(this);
        setUp();
        setUpTheme();
    }

    protected abstract void init();

    protected abstract void setVariables();

    private void setInternalVariables() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "No toolbar found");
        }
        try {
            mAppBar = (AppBarLayout) findViewById(R.id.appbar);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "No AppBarLayout found");
        }
        mRoot = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    protected abstract void setUp();

    protected void setUpTheme() {
        //Removes shadow if the color matches background
        if (getPrimaryColor() == resolveColorAttr(android.R.attr.colorBackground) && !shouldKeepAppBarShadow())
            ViewCompat.setElevation(mAppBar, 0.0f);

        //Changes background color of the root view
        mRoot.setBackgroundColor(resolveColorAttr(android.R.attr.windowBackground));
    }

    protected boolean shouldKeepAppBarShadow() {
        return false;
    }

    public void configureTaskDescription(@ColorInt int color, String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IconManager iconM = IconManager.get().setContext(this);
            Bitmap bm = BitmapFactory.decodeResource(getResources(), iconM.getDrawable(iconM.getOverviewIcon(iconM.getIcon(), getPrimaryColor()).getId()));
            setTaskDescription(new ActivityManager.TaskDescription(title, bm, color));
            bm.recycle();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Color Getters
    ///////////////////////////////////////////////////////////////////////////

    @ColorInt
    public int getPrimaryColor() {
        return Config.primaryColor(this, getATEKey());
    }

    @ColorInt
    public int getPrimaryDarkColor() {
        return Config.primaryColorDark(this, getATEKey());
    }

    @ColorInt
    public int getAccentColor() {
        return Config.accentColor(this, getATEKey());
    }

    @ColorInt
    public int getPrimaryTextColor() {
        return Config.textColorPrimary(this, getATEKey());
    }

    @ColorInt
    public int getSecondaryTextColor() {
        return Config.textColorSecondary(this, getATEKey());
    }

    @ColorInt
    public int resolveColorAttr(@AttrRes int resId) {
        final TypedValue value = new TypedValue();
        getTheme().resolveAttribute(resId, value, true);
        return value.data;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Simplifies Menus
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!overrideUpBehavior()) {
                    supportFinishAfterTransition();
                    return true;
                }
                return processMenuItem(item.getItemId()) || super.onOptionsItemSelected(item);
            case R.id.action_settings:
                startActivity(new Intent(this, Settings.class));
                return true;
            default:
                return processMenuItem(item.getItemId()) || super.onOptionsItemSelected(item);
        }
    }

    protected boolean overrideUpBehavior() {
        return false;
    }

    protected boolean processMenuItem(int id) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean inflate = (getOptionsMenu() != 0);
        if (inflate) {
            getMenuInflater().inflate(getOptionsMenu(), menu);
            return super.onCreateOptionsMenu(menu);
        }
        return false;
    }

    @MenuRes
    protected int getOptionsMenu() {
        return 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Makes sure "Search" button works on any screen
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onSearchRequested() {
        startActivity(new Intent(this, SearchActivity.class));
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Convenience
    ///////////////////////////////////////////////////////////////////////////

    /**
     * A convenience method to call {@link GEMUtil#startUrl(Context, String)}
     * @param url The url to launch
     */
    protected void startUrl(String url) {
        GEMUtil.startUrl(this, url);
    }

}
