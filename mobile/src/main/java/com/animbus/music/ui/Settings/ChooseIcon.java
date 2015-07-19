package com.animbus.music.ui.Settings;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.animbus.music.R;
import com.animbus.music.ui.MainScreen.MainScreen;
import com.animbus.music.SettingsManager;

public class ChooseIcon extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    Toolbar toolbar;
    SwitchCompat lightBackground;
    ImageView preview;
    SettingsManager settings;
    Drawable currentIcon;
    RadioGroup selections;
    int ICON_FALSE, ICON_TRUE;
    ComponentName icon, iconOld;
    int checkA, checkB;
    int iconAlt;
    Intent shortcutClickedIntent, addShortcutIntent, removeShortcutIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = new SettingsManager(this);

        //Themeing
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
            setTheme(R.style.AppTheme_Light);
        } else {
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.activity_settings_choose_icon);

        toolbar = (Toolbar) findViewById(R.id.choose_icon_app_bar);
        setSupportActionBar(toolbar);

        //More Themeing
        LinearLayout background = (LinearLayout) findViewById(R.id.choose_icon_root_view);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
            background.setBackgroundColor(getResources().getColor(R.color.primaryLight));
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_exit_light);
            } else {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_exit_light);
            }
        } else {
            background.setBackgroundColor(getResources().getColor(R.color.primaryDark));
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_exit);
            } else {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_exit);
            }
        }
        preview = (ImageView) findViewById(R.id.icon_preview_view);
        lightBackground = (SwitchCompat) findViewById(R.id.icon_use_light_preview);
        currentIcon = getDrawable(settings.getIntSetting(SettingsManager.KEY_ICON, R.mipmap.ic_launcher_new_dark));
        selections = (RadioGroup) findViewById(R.id.icon_choices_radiogroup);
        selections.setOnCheckedChangeListener(this);
        selections.check(settings.getIntSetting(SettingsManager.KEY_SELECTED_ICON_RADIOBUTTON, R.id.settings_radio_new_dark));
        checkA = selections.getCheckedRadioButtonId();
        ICON_FALSE = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        ICON_TRUE = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings_choose_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fix_icons) {
            resetIcons();
            return true;
        }

        if (id == R.id.action_add_to_homescreen) {
            Snackbar.make(findViewById(R.id.choose_icon_root_view), "Added Icon", Snackbar.LENGTH_LONG).show();
            addShortcut();
        }

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addShortcut() {
        addShortcutIntent = new Intent();
        shortcutClickedIntent = new Intent(getApplicationContext(), MainScreen.class);
        shortcutClickedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutClickedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutClickedIntent);
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, getAltIcon());
        addShortcutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(addShortcutIntent);
    }

    private void removeShortcut() {
        removeShortcutIntent = new Intent();
        removeShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        removeShortcutIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
    }

    public void onOptionChanged(View v) {
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
            if (lightBackground.isChecked()) {
                preview.setBackgroundColor(getResources().getColor(R.color.primaryLight));
                toolbar.setBackground(null);
            } else {
                preview.setBackgroundColor(getResources().getColor(R.color.primaryGreyDark));
                toolbar.setBackground(null);
            }
        } else {
            if (lightBackground.isChecked()) {
                preview.setBackgroundColor(getResources().getColor(R.color.primaryLight));
                toolbar.setBackground(getDrawable(R.drawable.gradient_black_ontop));
            } else {
                preview.setBackgroundColor(getResources().getColor(R.color.primaryGreyDark));
                toolbar.setBackground(null);
            }
        }
        preview.setImageDrawable(currentIcon);
    }

    public Bitmap getAltIcon() {
        return BitmapFactory.decodeResource(getResources(), iconAlt);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        settings.setIntSetting(SettingsManager.KEY_SELECTED_ICON_RADIOBUTTON, checkedId);
        switch (checkedId) {
            case R.id.settings_radio_new_dark:
                currentIcon = getDrawable(R.mipmap.ic_launcher_new_dark);
                iconAlt = R.mipmap.ic_launcher_new_light;
                icon = new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.new.dark");
                lightBackground.setChecked(true);
                break;

            case R.id.settings_radio_new_light:
                currentIcon = getDrawable(R.mipmap.ic_launcher_new_light);
                iconAlt = R.mipmap.ic_launcher_new_dark;
                icon = new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.new.light");
                lightBackground.setChecked(false);
                break;

            case R.id.settings_radio_new_color:
                currentIcon = getDrawable(R.mipmap.ic_launcher_new_color);
                iconAlt = R.mipmap.ic_launcher_new_color;
                icon = new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.new.color");
                lightBackground.setChecked(false);
                break;

            case R.id.settings_radio_old_dark:
                currentIcon = getDrawable(R.mipmap.ic_launcher_old_dark_swoundwaves);
                iconAlt = R.mipmap.ic_launcher_old_light_soundwaves;
                icon = new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.old.dark");
                lightBackground.setChecked(true);
                break;

            case R.id.settings_radio_old_light:
                currentIcon = getDrawable(R.mipmap.ic_launcher_old_light_soundwaves);
                iconAlt = R.mipmap.ic_launcher_old_dark_swoundwaves;
                icon = new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.old.light");
                lightBackground.setChecked(false);
                break;

        }
        checkB = checkedId;
        onOptionChanged(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (checkA != checkB) {
            switch (checkA) {
                case R.id.settings_radio_new_dark:
                    iconOld = new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.new.dark");
                    break;
                case R.id.settings_radio_new_light:
                    iconOld = new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.new.light");
                    break;
                case R.id.settings_radio_new_color:
                    iconOld = new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.new.color");
                    break;
                case R.id.settings_radio_old_dark:
                    iconOld = new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.old.dark");
                    break;
                case R.id.settings_radio_old_light:
                    iconOld = new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.old.light");
                    break;
            }
            getPackageManager().setComponentEnabledSetting(icon, ICON_TRUE, PackageManager.DONT_KILL_APP);
            getPackageManager().setComponentEnabledSetting(iconOld, ICON_FALSE, PackageManager.DONT_KILL_APP);
        }
    }

    private void resetIcons() {
        getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.new.dark"), ICON_TRUE, PackageManager.DONT_KILL_APP);
        getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.new.light"), ICON_FALSE, PackageManager.DONT_KILL_APP);
        getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.new.color"), ICON_FALSE, PackageManager.DONT_KILL_APP);
        getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.old.dark"), ICON_FALSE, PackageManager.DONT_KILL_APP);
        getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(), "com.animbus.music.activities.app.icon.old.light"), ICON_FALSE, PackageManager.DONT_KILL_APP);
    }
}
