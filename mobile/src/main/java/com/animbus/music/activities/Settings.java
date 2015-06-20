package com.animbus.music.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.animbus.music.R;
import com.animbus.music.ThemeManager;
import com.animbus.music.data.SettingsManager;

public class Settings extends AppCompatActivity {
    Toolbar toolbar;
    SwitchCompat lightThemeSwitch, categoryNamesSwitch, myLibraryPaletteSwitch, masterPaletteSwitch, nowPlayingPaletteSwitch, classicNowPlayingScreenSwitch, nowPlayingPeekSwitch;
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            setSupportActionBar(toolbar);
        }
        lightThemeSwitch = (SwitchCompat) findViewById(R.id.settings_light_theme_switch);
        categoryNamesSwitch = (SwitchCompat) findViewById(R.id.settings_category_names_switch);
        myLibraryPaletteSwitch = (SwitchCompat) findViewById(R.id.settings_MyLibrary_palette_switch);
        masterPaletteSwitch = (SwitchCompat) findViewById(R.id.settings_master_color_extraction_switch);
        nowPlayingPaletteSwitch = (SwitchCompat) findViewById(R.id.settings_now_playing_color_extraction);
        classicNowPlayingScreenSwitch = (SwitchCompat) findViewById(R.id.settings_classic_now_playing_switch);
        nowPlayingPeekSwitch = (SwitchCompat) findViewById(R.id.settings_now_playing_peek_switch);
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
        categoryNamesSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false));
        myLibraryPaletteSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_USE_PALETTE_IN_GRID, false));
        masterPaletteSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_USE_COLOR_EXTRACTION_MASTER, false));
        nowPlayingPeekSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_USE_NOW_PLAYING_PEEK, true));
        classicNowPlayingScreenSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_USE_CLASSIC_NOW_PLAYING, true));
        nowPlayingPaletteSwitch.setChecked(manager.getBooleanSetting(SettingsManager.KEY_EXTRACT_COLORS_IN_NOW_PLAYING_SCREEN, true));
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
            case R.id.action_about:
                startActivity(new Intent(this, About.class));
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
        MyLibrary myLibrary = new MyLibrary();
        Intent intent = new Intent(this, MyLibrary.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        myLibrary.end();
        startActivity(intent);
    }

    private void saveSettings() {
        manager.setBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, lightThemeSwitch.isChecked());
        manager.setBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, categoryNamesSwitch.isChecked());
        manager.setBooleanSetting(SettingsManager.KEY_USE_PALETTE_IN_GRID, myLibraryPaletteSwitch.isChecked());
        manager.setBooleanSetting(SettingsManager.KEY_USE_COLOR_EXTRACTION_MASTER, masterPaletteSwitch.isChecked());
        manager.setBooleanSetting(SettingsManager.KEY_USE_NOW_PLAYING_PEEK, nowPlayingPeekSwitch.isChecked());
        manager.setBooleanSetting(SettingsManager.KEY_USE_CLASSIC_NOW_PLAYING, classicNowPlayingScreenSwitch.isChecked());
        manager.setBooleanSetting(SettingsManager.KEY_EXTRACT_COLORS_IN_NOW_PLAYING_SCREEN, nowPlayingPaletteSwitch.isChecked());
    }

    public void settingChanged(View v) {
        //This is where you add dependancies
        manager.switchDependancy(masterPaletteSwitch, myLibraryPaletteSwitch);
        manager.switchDependancy(masterPaletteSwitch, nowPlayingPaletteSwitch);

        saveSettings();
    }

    public void openIconSelector(View v) {
        startActivity(new Intent(this, ChooseIcon.class));
    }
}

