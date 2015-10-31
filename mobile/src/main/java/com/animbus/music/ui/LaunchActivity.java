package com.animbus.music.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.animbus.music.BuildConfig;
import com.animbus.music.R;
import com.animbus.music.SettingsManager;
import com.animbus.music.customImpls.ThemableActivity;
import com.animbus.music.data.VariablesSingleton;
import com.animbus.music.media.Library;
import com.animbus.music.media.PlaybackManager;
import com.animbus.music.media.ServiceHelper;
import com.animbus.music.ui.mainScreen.MainScreen;
import com.animbus.music.ui.theme.Theme;
import com.animbus.music.ui.theme.ThemeManager;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

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
                    Library.get().findSongById(
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
        if (!VariablesSingleton.get().activated) {

            try {
                Picasso.Builder builder = new Picasso.Builder(this);
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                builder.memoryCache(new LruCache(1024 * 1024 * am.getMemoryClass() / 5));
                builder.loggingEnabled(false);
                builder.indicatorsEnabled(false);
                Picasso.setSingletonInstance(builder.build());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Sets Contexts
            SettingsManager.get().setContext(this);
            ThemeManager.get().setContext(this);

            //Loads Songs
            Library.get(this);
            if (!Library.get().isBuilt()) Library.get().build();

            //Starts Music Service
            ServiceHelper.get(this).initService();

            //Notifies app that it has activated
            VariablesSingleton.get().activated = true;
        }
    }

    public void complete() {
        final Intent i = new Intent(this, MainScreen.class);
        if (VariablesSingleton.get().activated) i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
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
        if (VariablesSingleton.get().activated) overridePendingTransition(0, 0);
    }

    @Override
    protected void setUpTheme(Theme theme) {

    }

}
