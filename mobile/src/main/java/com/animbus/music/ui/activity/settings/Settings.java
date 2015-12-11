package com.animbus.music.ui.activity.settings;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.android.vending.billing.IInAppBillingService;
import com.animbus.music.BuildConfig;
import com.animbus.music.R;
import com.animbus.music.ui.activity.settings.chooseIcon.ChooseIcon;
import com.animbus.music.ui.custom.activity.ThemeActivity;
import com.animbus.music.ui.theme.ThemeManager;
import com.animbus.music.util.Options;
import com.animbus.music.util.SettingsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Settings extends ThemeActivity implements ColorChooserDialog.ColorCallback {
    SwitchCompat
            pageNamesSwitch,
            paletteSwitch,
            tabsSwitch,
            scrollableTabsSwitch,
            tabsIconsSwitch;
    SettingsManager manager;
    ThemeManager themeManager;
    int clickedAmount = 0;

    IInAppBillingService mService;
    ServiceConnection mPlayConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            Log.d("Donations", "CONNECTED");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Log.d("Donations", "DISCONNECTED");
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void sequence() {
        super.sequence();
        setUpVersionNumberClicks();
        loadSettings();
    }

    @Override
    protected void init() {
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void setVariables() {
        manager = SettingsManager.get();
        themeManager = ThemeManager.get();

        pageNamesSwitch = (SwitchCompat) findViewById(R.id.settings_old_page_names_switch);
        paletteSwitch = (SwitchCompat) findViewById(R.id.settings_old_palette_switch);
        tabsSwitch = (SwitchCompat) findViewById(R.id.settings_old_tabs_switch);
        scrollableTabsSwitch = (SwitchCompat) findViewById(R.id.settings_old_tab_scrollable_switch);
        tabsIconsSwitch = (SwitchCompat) findViewById(R.id.settings_old_tabs_icons);
    }

    @Override
    protected void setUp() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewCompat.setElevation(findViewById(R.id.appbar), 0.0f);
    }

    private void loadSettings() {
        pageNamesSwitch.setChecked(Options.usingCategoryNames());
        paletteSwitch.setChecked(Options.usingPalette());
        tabsSwitch.setChecked(Options.usingTabs());
        scrollableTabsSwitch.setChecked(Options.usingScrollableTabs());
        tabsIconsSwitch.setChecked(Options.usingIconTabs());
        settingChanged(null);
    }

    private void setUpVersionNumberClicks() {
        if (BuildConfig.BUILD_TYPE == "internal" || BuildConfig.BUILD_TYPE == "debug") {
            try {
                if (manager.getIntSetting(SettingsManager.KEY_INTERNAL_TESTER_REGISTERED, -500) == -500) {
                    manager.setBooleanSetting(SettingsManager.KEY_INTERNAL_TESTER_REGISTERED, true);
                }
            } catch (ClassCastException ignored) {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_donate:
                showDonation();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mPlayConnection);
        }
        saveSettings();
    }

    private void saveSettings() {
        Options.setUseCategoryNames(pageNamesSwitch.isChecked());
        Options.setUsePalette(paletteSwitch.isChecked());
        Options.setUseTabs(tabsSwitch.isChecked());
        Options.setUseScrollableTabs(scrollableTabsSwitch.isChecked());
        Options.setUseIconTabs(tabsIconsSwitch.isChecked());
    }

    public void settingChanged(View v) {
        //This is where you add dependancies
        manager.switchDependancy(tabsSwitch, true, pageNamesSwitch, false);
        manager.switchDependancy(tabsSwitch, false, tabsIconsSwitch, false);
        manager.doubleSwitchDependancy(tabsSwitch, tabsIconsSwitch, scrollableTabsSwitch, false, true, false);

        //Saves the settings
        saveSettings();
    }

    public void openAbout(View v) {
        startActivity(new Intent(this, About.class));
    }

    public void openIconSelector(View v) {
        startActivity(new Intent(this, ChooseIcon.class));
    }

    public void showComingSoon(View v) {
        Snackbar.make(findViewById(R.id.settings_root_view), getResources().getString(R.string.msg_coming_soon), Snackbar.LENGTH_LONG).show();
    }

    public void showThemePicker(View v) {
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.settings_theme_title_choose))
                .setItems(new String[]{
                        getResources().getString(R.string.settings_theme_blue),
                        getResources().getString(R.string.settings_theme_pink),
                        getResources().getString(R.string.settings_theme_greyscale)
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                new AlertDialog.Builder(Settings.this)
                                        .setTitle(R.string.settings_theme_animbus).setItems(new String[]{
                                        getResources().getString(R.string.settings_theme_variant_dark),
                                        getResources().getString(R.string.settings_theme_variant_light)
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                SettingsManager.get().setBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false);
                                                ThemeManager.get().setBase(R.style.AppTheme_Blue);
                                                Settings.this.recreate();
                                                break;
                                            case 1:
                                                SettingsManager.get().setBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, true);
                                                ThemeManager.get().setBase(R.style.AppTheme_Light_Blue);
                                                Settings.this.recreate();
                                                break;
                                        }
                                    }
                                }).show();
                                break;
                            case 1:
                                new AlertDialog.Builder(Settings.this)
                                        .setTitle(R.string.settings_theme_animbus).setItems(new String[]{
                                        getResources().getString(R.string.settings_theme_variant_dark),
                                        getResources().getString(R.string.settings_theme_variant_light)
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                SettingsManager.get().setBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false);
                                                ThemeManager.get().setBase(R.style.AppTheme_Pink);
                                                Settings.this.recreate();
                                                break;
                                            case 1:
                                                SettingsManager.get().setBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, true);
                                                ThemeManager.get().setBase(R.style.AppTheme_Light_Pink);
                                                Settings.this.recreate();
                                                break;
                                        }
                                    }
                                }).show();
                                break;
                            case 2:
                                new AlertDialog.Builder(Settings.this)
                                        .setTitle(R.string.settings_theme_animbus).setItems(new String[]{
                                        getResources().getString(R.string.settings_theme_variant_dark),
                                        getResources().getString(R.string.settings_theme_variant_light)
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                SettingsManager.get().setBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false);
                                                ThemeManager.get().setBase(R.style.AppTheme);
                                                Settings.this.recreate();
                                                break;
                                            case 1:
                                                SettingsManager.get().setBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, true);
                                                ThemeManager.get().setBase(R.style.AppTheme_Light);
                                                Settings.this.recreate();
                                                break;
                                        }
                                    }
                                }).show();
                                break;
                        }
                    }
                })
                .create().show();
    }

    public void showUiTweaker(View v) {
        showComingSoon(null);
    }

    public void showPrimaryColorDialog(View v) {
        new ColorChooserDialog.Builder(this, R.string.title_color_chooser_primary)
                .accentMode(false)
                .preselect(getPrimaryColor())
                .allowUserColorInputAlpha(false)
                .show();
    }

    public void showAccentColorDialog(View v) {
        new ColorChooserDialog.Builder(this, R.string.title_color_chooser_accent)
                .accentMode(true)
                .preselect(getAccentColor())
                .allowUserColorInputAlpha(false)
                .show();
    }

    public void resetPrimaryColor(View v) {
        Options.setPrimaryColor(ContextCompat.getColor(this, !Options.isLightTheme() ? R.color.primaryDark : R.color.primaryLight));
    }

    @Override
    protected void setUpTheme() {
        super.setUpTheme();
        int colorForeground = resolveColorAttr(android.R.attr.colorForeground);
        ColorStateList thumbStateListNoAccent = resolveColorStateListAttr(android.support.v7.appcompat.R.attr.colorSwitchThumbNormal);
        ColorStateList trackStateList = new ColorStateList(new int[][]{
                {-android.R.attr.state_enabled},
                {android.R.attr.state_checked},
                {}
        }, new int[]{
                ColorUtils.setAlphaComponent(colorForeground, Math.round(Color.alpha(colorForeground) * 0.1f)),
                ColorUtils.setAlphaComponent(getAccentColor(), Math.round(Color.alpha(getAccentColor()) * 0.3f)),
                ColorUtils.setAlphaComponent(colorForeground, Math.round(Color.alpha(colorForeground) * 0.3f))
        }), thumbStateList = new ColorStateList(new int[][]{
                {-android.R.attr.state_enabled},
                {android.R.attr.state_checked},
                {}
        }, new int[]{
                thumbStateListNoAccent.getColorForState(new int[]{-android.R.attr.state_enabled}, 0),
                getAccentColor(),
                thumbStateListNoAccent.getDefaultColor()
        });

        DrawableCompat.setTintList(DrawableCompat.wrap(pageNamesSwitch.getThumbDrawable()), thumbStateList);
        DrawableCompat.setTintList(DrawableCompat.wrap(pageNamesSwitch.getTrackDrawable()), trackStateList);

        DrawableCompat.setTintList(DrawableCompat.wrap(paletteSwitch.getThumbDrawable()), thumbStateList);
        DrawableCompat.setTintList(DrawableCompat.wrap(paletteSwitch.getTrackDrawable()), trackStateList);

        DrawableCompat.setTintList(DrawableCompat.wrap(tabsSwitch.getThumbDrawable()), thumbStateList);
        DrawableCompat.setTintList(DrawableCompat.wrap(tabsSwitch.getTrackDrawable()), trackStateList);

        DrawableCompat.setTintList(DrawableCompat.wrap(scrollableTabsSwitch.getThumbDrawable()), thumbStateList);
        DrawableCompat.setTintList(DrawableCompat.wrap(scrollableTabsSwitch.getTrackDrawable()), trackStateList);

        DrawableCompat.setTintList(DrawableCompat.wrap(tabsIconsSwitch.getThumbDrawable()), thumbStateList);
        DrawableCompat.setTintList(DrawableCompat.wrap(tabsIconsSwitch.getTrackDrawable()), trackStateList);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog colorChooserDialog, @ColorInt int i) {
        if (!colorChooserDialog.isAccentMode()) {
            Options.setPrimaryColor(i);
        } else {
            Options.setAccentColor(i);
        }
        recreate();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void showDonation() {
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.settings_donate_disambiguation_title))
                .setItems(new String[]{getResources().getString(R.string.settings_donate_disambiguation_play),
                        getResources().getString(R.string.settings_donate_disambiguation_paypal)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND").setPackage("com.android.vending");
                                Boolean isConnected = bindService(serviceIntent, mPlayConnection, Context.BIND_AUTO_CREATE);
                                Log.d("Donations", String.valueOf(isConnected));
                                induceDonatePrices(true);
                                break;
                            case 1:
                                donate(0, false);
                                break;
                        }
                    }
                }).create().show();
    }

    private void induceDonatePrices(final Boolean useGooglePlay) {
        DialogInterface.OnClickListener listener;
        listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int amount = 0;
                switch (which) {
                    case 0:
                        amount = 1;
                        break;
                    case 1:
                        amount = 5;
                        break;
                    case 2:
                        amount = 10;
                        break;
                    case 3:
                        amount = 25;
                        break;
                    case 4:
                        amount = 50;
                        break;
                }
                donate(amount, useGooglePlay);
            }
        };
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.settings_donate_price_title))
                .setItems(new String[]{
                        getResources().getString(R.string.settings_donate_price_1),
                        getResources().getString(R.string.settings_donate_price_5),
                        getResources().getString(R.string.settings_donate_price_10),
                        getResources().getString(R.string.settings_donate_price_25),
                        getResources().getString(R.string.settings_donate_price_50)
                }, listener).create().show();
    }

    public void donate(int amount, boolean useGooglePlay) {
        if (useGooglePlay) {
            sendPlayBroadcast(amount);
        } else {
            new AlertDialog.Builder(Settings.this).setTitle(R.string.settings_donate_terms_popup_title)
                    .setMessage(R.string.settings_donate_terms_popup_message)
                    .setPositiveButton(R.string.settings_donate_terms_popup_pos, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://plus.google.com/+AdrianVovkDev/posts/PUiDmRFzPLw")));
                        }
                    }).setNegativeButton(R.string.settings_donate_terms_popup_neg, null).create().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3672) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
            if (resultCode == AppCompatActivity.RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    String token = jo.getString("purchaseToken");
                    Toast.makeText(this, sku, Toast.LENGTH_SHORT).show();
                    try {
                        mService.consumePurchase(3, "com.animbus.music", token);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendPlayBroadcast(int amount) {
        Bundle buyIntentBundle = null;
        try {
            buyIntentBundle = mService.getBuyIntent(3, "com.animbus.music", "donate_" + amount, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
        } catch (RemoteException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR REMOTE", Toast.LENGTH_SHORT).show();
        }
        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
        try {
            Settings.this.startIntentSenderForResult(pendingIntent.getIntentSender(),
                    3672, new Intent(), 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR INTENT", Toast.LENGTH_SHORT).show();
        }
        if (buyIntentBundle == null) {
            Toast.makeText(this, "ERROR NULL", Toast.LENGTH_SHORT).show();
        }
    }
}