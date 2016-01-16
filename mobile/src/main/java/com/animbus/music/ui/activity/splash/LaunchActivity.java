package com.animbus.music.ui.activity.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.animbus.music.R;
import com.animbus.music.media.Library;
import com.animbus.music.media.PlaybackRemote;
import com.animbus.music.ui.activity.mainScreen.MainScreen;
import com.animbus.music.ui.activity.setup.SetupActivity;
import com.animbus.music.ui.custom.activity.ThemeActivity;
import com.animbus.music.util.LoadedFuse;
import com.animbus.music.util.Options;

import static com.animbus.music.media.PlaybackRemote.LOCAL;


public class LaunchActivity extends ThemeActivity {
    private static int SETUP_REQ_CODE = 8;
    TabLayout tabs;

    @Override
    protected void init() {
        setContentView(R.layout.activity_launch);

        if (getIntent().getAction().equals(Intent.ACTION_VIEW))
            PlaybackRemote.play(getIntent().getData());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContexts();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setVariables() {
        tabs = (TabLayout) findViewById(R.id.loading_tab_extention);
    }

    @Override
    protected void setUp() {
        tabs.setVisibility(Options.usingTabs() ? View.VISIBLE : View.GONE);
        requestPermissions();
    }

    @Override
    protected boolean shouldKeepAppBarShadow() {
        return Options.usingTabs();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Management for the setup activity
    ///////////////////////////////////////////////////////////////////////////

    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23 && (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            startActivityForResult(new Intent(this, SetupActivity.class), SETUP_REQ_CODE);
        } else complete();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETUP_REQ_CODE) recreate();
    }

    private void setContexts() {
        if (!LoadedFuse.isActivated()) {
            Options.init(this);
            Library.setContext(this);

            //Initiates the process of setting up all of the media objects to be triggered instantly
            PlaybackRemote.setUp(this);
            PlaybackRemote.inject(LOCAL);

            //Sets up theme engine
            configThemeIfNeeded();

            //Notifies app that it has activated
            LoadedFuse.trip();
        }
    }

    private void configThemeIfNeeded() {
        if (ATE.config(this, getATEKey()).isConfigured()) {
            ATE.config(this, getATEKey())
                    .activityTheme(R.style.AppTheme_Faithful)

                    .coloredActionBar(true)
                    .autoGeneratePrimaryDark(true)
                    .coloredStatusBar(true)
                    .lightStatusBarMode(Config.LIGHT_STATUS_BAR_AUTO)
                    .lightToolbarMode(Config.LIGHT_TOOLBAR_AUTO);
        }
    }

    public void complete() {
        //Loads Songs
        if (!Library.isBuilt()) Library.build();

        Intent i = new Intent(this, MainScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
        overridePendingTransition(0, 0);
    }
}
