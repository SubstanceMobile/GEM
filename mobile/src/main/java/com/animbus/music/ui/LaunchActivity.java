package com.animbus.music.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.animbus.music.BuildConfig;
import com.animbus.music.R;
import com.animbus.music.SettingsManager;
import com.animbus.music.customImpls.ThemableActivity;
import com.animbus.music.media.MediaData;
import com.animbus.music.media.ServiceHelper;
import com.animbus.music.ui.mainScreen.BackupHub;
import com.animbus.music.ui.mainScreen.MainScreen;
import com.animbus.music.ui.theme.Theme;
import com.animbus.music.ui.theme.ThemeManager;

public class LaunchActivity extends ThemableActivity {
    Toolbar toolbar;
    TabLayout tabs;
    AppBarLayout appBar;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_launch);
    }

    @Override
    protected void setVariables() {
        toolbar = (Toolbar) findViewById(R.id.loading_toolbar);
        tabs = (TabLayout) findViewById(R.id.loading_tab_extention);
        appBar = (AppBarLayout) findViewById(R.id.loading_app_bar);
    }

    @Override
    protected void setUp() {
        boolean showTabs = SettingsManager.get().getBooleanSetting(SettingsManager.KEY_USE_TABS, false);
        tabs.setVisibility(showTabs ? View.VISIBLE : View.GONE);
        if (!showTabs) ViewCompat.setElevation(appBar, 0.0f);
        if (BackupHub.get().activated) {
            SettingsManager.get().setContext(this);
            ThemeManager.get().setContext(this);
            MediaData.get(this);
            BackupHub.get().activated = true;
        }
        ServiceHelper.get(this).initService();
        if (!MediaData.get().isBuilt()){
            MediaData.get().build();
            MediaData.get().setListener(new MediaData.AlbumArtsGeneratedListener() {
                @Override
                public void albumArtsGenerated() {
                    complete();
                }
            });
        } else {
            complete();
        }
    }

    public void complete(){
        final Intent i = new Intent(this, MainScreen.class);
        if (BackupHub.get().activated) i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        boolean internal = BuildConfig.BUILD_TYPE.equals("debug") || BuildConfig.BUILD_TYPE.equals("internal");
        if (internal && !SettingsManager.get().getBooleanSetting("doNotShowAgain_INTERNAL_TESTER", false)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.internal_tester_warning_title)
                    .setMessage(R.string.internal_tester_warning)
                    .setCancelable(false)
                    .setPositiveButton(R.string.internal_tester_accept, null)
                    .setNegativeButton(R.string.internal_tester_dont_show_again, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SettingsManager.get().setBooleanSetting("doNotShowAgain_INTERNAL_TESTER", true);
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (BackupHub.get().activated) overridePendingTransition(-1, -1);
                            startActivity(i);
                            finish();
                        }
                    })
                    .create().show();
        } else {
            if (BackupHub.get().activated) overridePendingTransition(-1, -1);
            startActivity(i);
            finish();
        }

    }

    @Override
    protected void setUpTheme(Theme theme) {

    }

}
