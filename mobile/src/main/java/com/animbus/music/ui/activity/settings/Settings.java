/*
 * Copyright (C) 2016 Substance Mobile
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
 */

package com.animbus.music.ui.activity.settings;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.prefs.ATEColorPreference;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.android.vending.billing.IInAppBillingService;
import com.animbus.music.R;
import com.animbus.music.ui.custom.activity.ThemeActivity;
import com.animbus.music.util.Options;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Settings extends ThemeActivity implements ColorChooserDialog.ColorCallback {

    @Override
    protected int getLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void setUp() {
        getFragmentManager().beginTransaction().replace(R.id.prefs, new PrefsFragment()).commit();
    }

    @Override
    protected int getOptionsMenu() {
        return R.menu.menu_settings;
    }

    @Override
    protected boolean processMenuItem(int id) {
        switch (id) {
            case R.id.action_reset:
                Options.resetPrefs();
                Options.restartApp();
                return true;
        }
        return super.processMenuItem(id);
    }

    @Override
    protected void onDestroy() {
        if (mService != null) unbindService(mPlayConnection);
        super.onDestroy();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Settings
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog colorChooserDialog, @ColorInt int i) {
        if (!colorChooserDialog.isAccentMode())
            ATE.config(this, getATEKey()).primaryColor(i).apply(this);
        else
            ATE.config(this, getATEKey()).accentColor(i).navigationViewSelectedIcon(i)
                    .navigationViewSelectedText(i).apply(this);
        ((PrefsFragment) getFragmentManager().findFragmentById(R.id.prefs)).configure();
        recreate();
    }

    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        public PrefsFragment() {

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            configure();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (!key.equals("last_update_time")) Options.markChanged();
        }

        public void configure() {
            findPreference("base_theme").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    ATE.config(getActivity(), ((Settings) getActivity()).getATEKey())
                            .activityTheme(getStyleFromPos(Integer.parseInt((String) newValue))).commit();
                    Options.setLightTheme(Integer.parseInt((String) newValue) == 2);
                    getActivity().recreate();
                    return true;
                }
            });

            final ATEColorPreference primaryColor = (ATEColorPreference) findPreference("primary");
            final int primary = Config.primaryColor(getActivity(), ((Settings) getActivity()).getATEKey());
            primaryColor.setColor(primary, Color.BLACK);
            primaryColor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new ColorChooserDialog.Builder((Settings) getActivity(), R.string.settings_primary)
                            .accentMode(false)
                            .preselect(primary)
                            .allowUserColorInputAlpha(false)
                            .show();
                    return true;
                }
            });

            ATEColorPreference accentColor = (ATEColorPreference) findPreference("accent");
            final int accent = Config.accentColor(getActivity(), ((Settings) getActivity()).getATEKey());
            accentColor.setColor(accent, Color.BLACK);
            accentColor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new ColorChooserDialog.Builder((Settings) getActivity(), R.string.settings_accent)
                            .accentMode(true)
                            .preselect(accent)
                            .allowUserColorInputAlpha(false)
                            .show();
                    return true;
                }
            });

            findPreference("reset_primary").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ATE.config(getActivity(), ((Settings) getActivity()).getATEKey())
                            .primaryColor(((Settings) getActivity()).resolveColorAttr(android.R.attr.colorBackground))
                            .commit();
                    getActivity().recreate();
                    return true;
                }
            });

            findPreference("color_navbar").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    ATE.config(getActivity(), ((Settings) getActivity()).getATEKey()).coloredNavigationBar((Boolean) newValue).apply(getActivity());
                    return true;
                }
            });

            findPreference("donate").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ((Settings) getActivity()).showDonation();
                    return true;
                }
            });
        }

        private int getStyleFromPos(int pos) {
            switch (pos) {
                case 0:
                    return R.style.AppTheme_Dark;
                case 1:
                    return R.style.AppTheme_Faithful;
                case 2:
                    return R.style.AppTheme_Light;
            }
            return 0;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Donations
    ///////////////////////////////////////////////////////////////////////////

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

    public void showDonation() {
        new MaterialDialog.Builder(this)
                .title(R.string.settings_donate_disambiguation_title)
                .items(getResources().getString(R.string.settings_donate_disambiguation_play),
                        getResources().getString(R.string.settings_donate_disambiguation_paypal))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                /*Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND").setPackage("com.android.vending");
                                Boolean isConnected = bindService(serviceIntent, mPlayConnection, Context.BIND_AUTO_CREATE);
                                Log.d("Donations", String.valueOf(isConnected));
                                induceDonatePrices(true);*/
                                Snackbar.make(mRoot, R.string.msg_coming_soon, Snackbar.LENGTH_SHORT).show();
                                break;
                            case 1:
                                donate(0, false);
                                break;
                        }
                    }
                }).show();
    }

    private void induceDonatePrices(final Boolean useGooglePlay) {
        new MaterialDialog.Builder(this).title(R.string.settings_donate_price_title)
                .items(getResources().getString(R.string.settings_donate_price_1),
                        getResources().getString(R.string.settings_donate_price_5),
                        getResources().getString(R.string.settings_donate_price_10),
                        getResources().getString(R.string.settings_donate_price_25),
                        getResources().getString(R.string.settings_donate_price_50))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
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
                }).show();
    }

    public void donate(int amount, boolean useGooglePlay) {
        if (useGooglePlay) {
            sendPlayBroadcast(amount);
        } else {
            new MaterialDialog.Builder(this).title(R.string.settings_donate_terms_popup_title)
                    .content(R.string.settings_donate_terms_popup_message)
                    .positiveText(android.R.string.yes).negativeText(android.R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://plus.google.com/+AdrianVovkDev/posts/PUiDmRFzPLw")));
                }
            }).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3672) {
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
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
    }
}