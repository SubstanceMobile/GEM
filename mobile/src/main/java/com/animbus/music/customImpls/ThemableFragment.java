package com.animbus.music.customImpls;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.View;

import com.animbus.music.SettingsManager;
import com.animbus.music.ui.theme.Theme;
import com.animbus.music.ui.theme.ThemeManager;

/**
 * Created by Adrian on 8/21/2015.
 */
public abstract class ThemableFragment extends Fragment implements ThemeManager.OnThemeChangedListener {
    Context cxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sequence(savedInstanceState);
    }

    /**
     * call this when adding code to the sequence of default methods. The default sequence can be triggered by
     * <code>super.sequence(savedInstanceState)</code>.
     * Everything before triggering will happen before, everythibg after will happen after. The default sequence is:
     * * <code>setBaseTheme(int style)</code>,
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

    @Override
    public void onThemeChanged(Theme newTheme) {
        setUpTheme(newTheme);
    }
}
