package com.animbus.music.customImpls;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.StyleRes;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.animbus.music.SettingsManager;
import com.animbus.music.media.MediaData;
import com.animbus.music.media.MediaService;
import com.animbus.music.media.PlaybackManager;
import com.animbus.music.ui.theme.Theme;
import com.animbus.music.ui.theme.ThemeManager;

/**
 * Created by Adrian on 8/5/2015.
 */
public abstract class ThemableActivity extends AppCompatActivity implements ThemeManager.OnThemeChangedListener {

    private void setContexts() {
        if (SettingsManager.get().context == null) {
            SettingsManager.get().setContext(this);
        }
        if (ThemeManager.get().cxt == null) {
            ThemeManager.get().setContext(this);
        }
        if (MediaData.get().context == null){
            MediaData.get(this);
        }
        if (PlaybackManager.get().mContext == null){
            initService();
        }
    }

    MediaService mService;
    public void initService(){
        Intent i = new Intent(this, MediaService.class);
        ServiceConnection conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MediaService.MusicBinder binder = (MediaService.MusicBinder) service;
                mService = binder.getService();
                mService.setUp();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };
        startService(i);
        boolean bound = bindService(i, conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContexts();
        if (!MediaData.get().isBuilt()){
            MediaData.get().build();
        }
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
