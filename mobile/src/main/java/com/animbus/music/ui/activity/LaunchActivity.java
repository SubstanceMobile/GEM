package com.animbus.music.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.animbus.music.BuildConfig;
import com.animbus.music.R;
import com.animbus.music.ui.custom.activity.ThemableActivity;
import com.animbus.music.media.Library;
import com.animbus.music.media.stable.PlaybackManager;
import com.animbus.music.media.stable.ServiceHelper;
import com.animbus.music.ui.activity.mainScreen.MainScreen;
import com.animbus.music.ui.theme.Theme;
import com.animbus.music.ui.theme.ThemeManager;
import com.animbus.music.util.LoadedFuse;
import com.animbus.music.util.SettingsManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


public class LaunchActivity extends ThemableActivity {
    Toolbar toolbar;
    TabLayout tabs;
    AppBarLayout appBar;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_launch);
        if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            //Playing from intent
            PlaybackManager.get().play(
                    Library.findSongById(
                            Long.valueOf(getIntent().getData().getLastPathSegment().substring(6))));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContexts();
        super.onCreate(savedInstanceState);
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
        requestPermissions();
        checkUpdate.run();
    }

    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                complete();
            } else {
                permissionRationale();
                callPermissionRequest();
            }
        } else {
            complete();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void permissionRationale() {
        //Permission Denied
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this).setTitle(R.string.permission_storage_explain_title).setMessage(R.string.permission_storage_explain_message)
                    .setPositiveButton(android.R.string.ok, null).setCancelable(false).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    callPermissionRequest();
                }
            }).create().show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void callPermissionRequest() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 159);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 159) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) complete();
            else {
                permissionRationale();
            }
        }
    }


    private void setContexts() {
        if (!LoadedFuse.isActivated()) {

            //Sets Contexts
            SettingsManager.setContext(this);
            ThemeManager.get().setContext(this);

            //Loads Songs
            Library.get(this);
            if (!Library.get().isBuilt()) Library.get().build();

            //Starts Music Service
            ServiceHelper.get(this).initService();

            //Notifies app that it has activated
            LoadedFuse.trip();
        }
    }

    public void complete() {
        final Intent i = new Intent(this, MainScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        boolean internal = BuildConfig.BUILD_TYPE.equals("debug") || BuildConfig.BUILD_TYPE.equals("internal");
        if (internal && !SettingsManager.get().getBooleanSetting("doNotShowAgain_INTERNAL_TESTER", false)) {
            try {
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
                                showNextActivity();
                            }
                        })
                        .create().show();
            } catch (Exception e) {
                Snackbar.make(findViewById(R.id.loading_root_view), R.string.internal_tester_error, Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        Snackbar.make(findViewById(R.id.loading_root_view), R.string.internal_tester_error, Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                showNextActivity();
                            }
                        }).show();
                    }
                }).show();
            }
        } else {
            showNextActivity();
        }
    }

    private void showNextActivity() {
        final Intent i = new Intent(this, MainScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void setUpTheme(Theme theme) {

    }

    /* This Thread checks for Updates in the Background */
    private Thread checkUpdate = new Thread() {
        public void run() {
            try {
                URL updateURL = new URL("https://raw.githubusercontent.com/Substance-Project/GEM/indev/mobile/src/main/res/raw/update.txt");
                BufferedReader in = new BufferedReader(new InputStreamReader(updateURL.openStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    // str is one line of text; readLine() strips the newline character(s)
                    int curVersion = BuildConfig.VERSION_CODE;
                    int newVersion = Integer.valueOf(str);

                    /* Is a higher version than the current already out? */
                    if (newVersion > curVersion) {
                        new MaterialDialog.Builder(LaunchActivity.this)
                                .title(R.string.msg_update_available)
                                .content(R.string.msg_update_content)
                                .positiveText(android.R.string.yes)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.animbus.music")));
                                    }
                                })
                                .negativeText(android.R.string.no)
                                .build().show();
                    }
                }
                in.close();
            } catch (Exception ignored) {
            }
        }

    };


}
