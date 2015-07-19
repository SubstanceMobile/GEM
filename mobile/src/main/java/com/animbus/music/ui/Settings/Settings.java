package com.animbus.music.ui.Settings;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.animbus.music.R;
import com.animbus.music.SettingsManager;
import com.animbus.music.ThemeManager;
import com.animbus.music.ui.MainScreen.MainScreen;

public class Settings extends AppCompatActivity {
    Toolbar toolbar;
    SwitchCompat lightThemeSwitch, pageNamesSwitch, myLibraryPaletteSwitch, tabsSwitch;
    SettingsManager manager;
    Context context;
    ThemeManager themeManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new SettingsManager(this);
        themeManager = new ThemeManager(this, ThemeManager.TYPE_NORMAL);

        //Themeing
        setTheme(themeManager.getCurrentTheme());
        setContentView(R.layout.activity_settings);
        findViewById(R.id.settings_root_view).setBackgroundColor(themeManager.getCurrentBackgroundColor());


        toolbar = (Toolbar) findViewById(R.id.SettingsAppbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewCompat.setElevation(findViewById(R.id.settings_app_bar_layout), 0.0f);

        lightThemeSwitch = (SwitchCompat) findViewById(R.id.settings_old_light_theme_switch);
        pageNamesSwitch = (SwitchCompat) findViewById(R.id.settings_old_page_names_switch);
        myLibraryPaletteSwitch = (SwitchCompat) findViewById(R.id.settings_old_palette_switch);
        tabsSwitch = (SwitchCompat) findViewById(R.id.settings_old_tabs_switch);
        loadSettings();
        //Sets Window description in Multitasking menu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!manager.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_new_light);
                setTaskDescription(new ActivityManager.TaskDescription(null, bm, getResources().getColor(R.color.primaryDark)));
                bm.recycle();
            } else {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_new_dark);
                setTaskDescription(new ActivityManager.TaskDescription(null, bm, getResources().getColor(R.color.primaryLight)));
                bm.recycle();
            }
        }

        context = this;
    }

    private void loadSettings() {
        lightThemeSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false));
        pageNamesSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false));
        myLibraryPaletteSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_USE_PALETTE_IN_GRID, false));
        tabsSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_USE_TABS, false));
        settingChanged(null);
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
                Snackbar.make(findViewById(R.id.settings_root_view), "Thanks for wanting to donate, but this feature isn't fully implimented", Snackbar.LENGTH_LONG).setAction("AWW", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
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
        saveSettings();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainScreen mainScreen = new MainScreen();
        Intent intent = new Intent(this, MainScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mainScreen.end();
        startActivity(intent);
    }

    private void saveSettings() {
        manager.setBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, lightThemeSwitch.isChecked());
        manager.setBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, pageNamesSwitch.isChecked());
        manager.setBooleanSetting(SettingsManager.KEY_USE_PALETTE_IN_GRID, myLibraryPaletteSwitch.isChecked());
        manager.setBooleanSetting(SettingsManager.KEY_USE_NOW_PLAYING_PEEK, false);
        manager.setBooleanSetting(SettingsManager.KEY_USE_NEW_NOW_PLAYING, false);
        manager.setBooleanSetting(SettingsManager.KEY_USE_TABS, tabsSwitch.isChecked());
    }

    public void settingChanged(View v) {
        //This is where you add dependancies
        manager.switchDependancy(tabsSwitch, true, pageNamesSwitch, false);

        //Saves the settings
        saveSettings();
    }

    public void openIconSelector(View v) {
        startActivity(new Intent(this, ChooseIcon.class));
    }
}

