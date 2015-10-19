package com.animbus.music.ui.settings;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.animbus.music.BuildConfig;
import com.animbus.music.R;
import com.animbus.music.SettingsManager;
import com.animbus.music.UiTweaker;
import com.animbus.music.customImpls.ThemableActivity;
import com.animbus.music.data.VariablesSingleton;
import com.animbus.music.ui.mainScreen.MainScreen;
import com.animbus.music.ui.settings.chooseIcon.ChooseIcon;
import com.animbus.music.ui.settings.chooseIcon.IconManager;
import com.animbus.music.ui.theme.Theme;
import com.animbus.music.ui.theme.ThemeManager;

import org.json.JSONException;
import org.json.JSONObject;

public class Settings extends ThemableActivity {
    private static final boolean GOOGLE_PLAY = true, PAYPAL = false;
    Toolbar toolbar;
    SwitchCompat
            pageNamesSwitch,
            myLibraryPaletteSwitch,
            tabsSwitch,
            scrollableTabsSwitch,
            tabsIconsSwitch;
    SettingsManager manager;
    ThemeManager themeManager;
    TextView versionTextView;
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
    protected void sequence(Bundle savedInstanceState) {
        super.sequence(savedInstanceState);
        setUpVersionNumberClicks();
        loadSettings();
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void setVariables() {
        manager = SettingsManager.get();
        themeManager = ThemeManager.get();
        toolbar = (Toolbar) findViewById(R.id.SettingsAppbar);
        versionTextView = (TextView) findViewById(R.id.settings_about_version_value);

        pageNamesSwitch = (SwitchCompat) findViewById(R.id.settings_old_page_names_switch);
        myLibraryPaletteSwitch = (SwitchCompat) findViewById(R.id.settings_old_palette_switch);
        tabsSwitch = (SwitchCompat) findViewById(R.id.settings_old_tabs_switch);
        scrollableTabsSwitch = (SwitchCompat) findViewById(R.id.settings_old_tab_scrollable_switch);
        tabsIconsSwitch = (SwitchCompat) findViewById(R.id.settings_old_tabs_icons);
    }

    @Override
    protected void setUp() {
        //Setting Toolbar as actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewCompat.setElevation(findViewById(R.id.settings_app_bar_layout), 0.0f);

        //Version Number
        String type;
        if (BuildConfig.BUILD_TYPE.equals("internal")) {
            type = getResources().getString(R.string.settings_about_version_type_internal);
        } else if (BuildConfig.BUILD_TYPE.equals("debug")) {
            type = getResources().getString(R.string.settings_about_version_type_debug);
        } else {
            type = "";
        }
        versionTextView.setText(BuildConfig.VERSION_NAME + " " + type);


        //Sets Window description in Multitasking menu
        IconManager iconM = IconManager.get().setContext(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!manager.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), iconM.getDrawable(iconM.getOverviewIcon(iconM.getIcon()).getId()));
                setTaskDescription(new ActivityManager.TaskDescription(null, bm, getResources().getColor(R.color.primaryDark)));
                bm.recycle();
            } else {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), iconM.getDrawable(iconM.getOverviewIcon(iconM.getIcon()).getId()));
                setTaskDescription(new ActivityManager.TaskDescription(null, bm, getResources().getColor(R.color.primaryLight)));
                bm.recycle();
            }
        }
        if (manager.getBooleanSetting(SettingsManager.KEY_INTERNAL_TESTER_REGISTERED, false)) {
            findViewById(R.id.settings_section_testers).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.settings_section_testers).setVisibility(View.GONE);
        }
    }

    @Override
    protected void setUpTheme(Theme theme) {

    }

    private void loadSettings() {
        pageNamesSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false));
        myLibraryPaletteSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_USE_PALETTE_IN_GRID, true));
        tabsSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_USE_TABS, false));
        scrollableTabsSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_SCROLLABLE_TABS, true));
        tabsIconsSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_USE_TAB_ICONS, false));
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
        findViewById(R.id.settings_about_version_root).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (manager.getBooleanSetting(SettingsManager.KEY_INTERNAL_TESTER_REGISTERED, false)) {

                }
                return true;
            }
        });
        findViewById(R.id.settings_about_version_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manager.getBooleanSetting(SettingsManager.KEY_INTERNAL_TESTER_REGISTERED, false)) {
                    Toast.makeText(Settings.this, R.string.settings_tester_already_registered, Toast.LENGTH_SHORT).show();
                } else if (clickedAmount < 4 && clickedAmount >= 0) {
                    clickedAmount++;
                } else if (clickedAmount == 4) {
                    Toast.makeText(Settings.this, R.string.settings_tester_regester_confirmation, Toast.LENGTH_SHORT).show();
                    manager.setBooleanSetting(SettingsManager.KEY_INTERNAL_TESTER_REGISTERED, true);
                    clickedAmount = -500;
                    Settings.this.recreate();
                } else {
                    Toast.makeText(Settings.this, "Logic Error" + String.valueOf(clickedAmount), Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainScreen mainScreen = VariablesSingleton.get().settingsMyLib;
        if (mainScreen != null) {
            mainScreen.recreate();
        }
    }

    private void saveSettings() {
        manager.setBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, pageNamesSwitch.isChecked());
        manager.setBooleanSetting(SettingsManager.KEY_USE_PALETTE_IN_GRID, myLibraryPaletteSwitch.isChecked());
        manager.setBooleanSetting(SettingsManager.KEY_USE_NOW_PLAYING_PEEK, false);
        manager.setBooleanSetting(SettingsManager.KEY_USE_NEW_NOW_PLAYING, false);
        manager.setBooleanSetting(SettingsManager.KEY_USE_TABS, tabsSwitch.isChecked());
        manager.setBooleanSetting(SettingsManager.KEY_SCROLLABLE_TABS, scrollableTabsSwitch.isChecked());
        manager.setBooleanSetting(SettingsManager.KEY_USE_TAB_ICONS, tabsIconsSwitch.isChecked());
    }

    public void settingChanged(View v) {
        //This is where you add dependancies
        manager.switchDependancy(tabsSwitch, true, pageNamesSwitch, false);
        manager.switchDependancy(tabsSwitch, false, tabsIconsSwitch, false);
        manager.doubleSwitchDependancy(tabsSwitch, tabsIconsSwitch, scrollableTabsSwitch, false, true, false);

        //Saves the settings
        saveSettings();
    }

    public void openSourceCode(View v) {
        String url = "https://github.com/Substance-Project/GEM";
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
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
        /*switch (v.getId()) {
            case R.id.settings_ui_tweaker_general:
                showUiTweaker(UiTweaker.TYPE_GENERAL);
                break;
            case R.id.settings_ui_tweaker_home:
                showUiTweaker(UiTweaker.TYPE_HOME);
                break;
            case R.id.settings_ui_tweaker_albums:
                showUiTweaker(UiTweaker.TYPE_ALBUMS);
                break;
            case R.id.settings_ui_tweaker_now_playing:
                showUiTweaker(UiTweaker.TYPE_NOWPLAYING);
                break;
        }*/
    }

    public void showUiTweaker(int type) {
        startActivity(new Intent(this, UiTweaker.class).putExtra("ui_type", type));
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