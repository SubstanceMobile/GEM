package com.animbus.music.ui.custom.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.appthemeengine.ATEActivity;
import com.afollestad.appthemeengine.Config;
import com.animbus.music.GEMApp;
import com.animbus.music.R;
import com.animbus.music.ui.activity.search.SearchActivity;
import com.animbus.music.ui.activity.settings.Settings;
import com.animbus.music.util.GEMUtil;
import com.animbus.music.util.IconManager;
import com.animbus.music.util.Options;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class ThemeActivity extends ATEActivity {
    @Nullable @Bind(R.id.toolbar) public Toolbar mToolbar;
    @Nullable @Bind(R.id.appbar) public AppBarLayout mAppBar;
    public View mRoot;
    public long lastSettingsUpdate = System.currentTimeMillis();

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
     * {@link #setContentView(int)} or {@link #inflate()},
     * {@link ButterKnife#bind(Activity)},
     * {@link AppCompatActivity#setSupportActionBar(Toolbar)},
     * Setting {@link #mRoot},
     * {@link #setVariables()},
     * {@link #setUp()},
     * {@link #setUpTheme()}
     */
    protected void sequence() {
        if (!useInflate()) setContentView(getLayout());
        else inflate();
        ButterKnife.bind(this);
        try {
            setSupportActionBar(mToolbar);
        } catch (Exception e) {
            Log.i(getClass().getSimpleName(), "This activity has no toolbar");
        }
        mRoot = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        setVariables();
        setUp();
        setUpTheme();
    }

    protected boolean useInflate() {
        return false;
    }

    protected void inflate() {
        //Do nothing.
    }

    protected abstract int getLayout();

    protected void setVariables() {
        //Do nothing
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
    // Lifecycle
    ///////////////////////////////////////////////////////////////////////////


    @Override
    protected void onStart() {
        super.onStart();
        lastSettingsUpdate = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Options.invalidateActivity(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Menu boilerplate
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
     *
     * @param url The url to launch
     */
    protected void startUrl(String url) {
        GEMUtil.startUrl(this, url);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (mToolbar != null) mToolbar.setTitle(title);
        if (GEMUtil.isLollipop()) {
            IconManager iconM = IconManager.get().setContext(this);
            Bitmap bm = BitmapFactory.decodeResource(getResources(), iconM.getDrawable(iconM.getOverviewIcon(iconM.getIcon(), getPrimaryColor()).getId()));
            setTaskDescription(new ActivityManager.TaskDescription(title.toString(), bm, getPrimaryColor()));
            bm.recycle();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Proper ATE key
    ///////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public String getATEKey() {
        return ((GEMApp) getApplication()).getATEKey();
    }
}
