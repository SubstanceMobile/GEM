package com.animbus.music.ui.custom.activity;

import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.animbus.music.ui.activity.theme.Theme;
import com.animbus.music.ui.activity.theme.ThemeManager;

/**
 * Created by Adrian on 8/5/2015.
 */
public abstract class ThemableActivity extends AppCompatActivity implements ThemeManager.OnThemeChangedListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setBaseTheme(ThemeManager.get().getBase(), true);
        super.onCreate(savedInstanceState);
        sequence(savedInstanceState);
    }

    /**
     * call this when adding code to the sequence of default methods. The default sequence can be triggered by
     * <code>super.sequence(savedInstanceState)</code>.
     * Everything before triggering will happen before, everythibg after will happen after. The default sequence is:
     * <code>init(savedInstanceState)</code>,
     * <code>setVariables()</code>,
     * <code>setUp()</code>,
     * <code>setUpTheme()</code>
     */
    protected void sequence(Bundle savedInstanceState) {
        init(savedInstanceState);
        setVariables();
        setUp();
        setUpTheme(ThemeManager.get().getTheme());
    }

    protected abstract void init(Bundle savedInstanceState);

    protected abstract void setVariables();

    protected abstract void setUp();

    protected abstract void setUpTheme(Theme theme);

    protected void setAppBarColor(AppBarLayout appBar, int color) {
        setColor(appBar, color);
    }

    protected void setColor(View view, int color) {
        view.setBackgroundColor(color);
    }

    protected void setBaseTheme(@StyleRes int baseTheme, boolean first) {
        setTheme(baseTheme);
        if (!first) {
            recreate();
        }
    }

    @Override
    public void onThemeChanged(Theme newTheme) {
        setUpTheme(newTheme);
    }
}
