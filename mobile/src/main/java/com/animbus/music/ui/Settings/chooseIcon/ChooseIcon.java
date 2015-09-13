package com.animbus.music.ui.settings.chooseIcon;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.animbus.music.R;
import com.animbus.music.SettingsManager;
import com.animbus.music.ui.theme.Theme;
import com.animbus.music.ui.theme.ThemeManager;
import com.animbus.music.customImpls.ThemableActivity;

import static com.animbus.music.ui.settings.chooseIcon.IconManager.COLOR_BLACK;
import static com.animbus.music.ui.settings.chooseIcon.IconManager.COLOR_BLUE;
import static com.animbus.music.ui.settings.chooseIcon.IconManager.COLOR_COLORFUL;
import static com.animbus.music.ui.settings.chooseIcon.IconManager.COLOR_GREEN;
import static com.animbus.music.ui.settings.chooseIcon.IconManager.COLOR_ORANGE;
import static com.animbus.music.ui.settings.chooseIcon.IconManager.COLOR_RED;
import static com.animbus.music.ui.settings.chooseIcon.IconManager.COLOR_WHITE;
import static com.animbus.music.ui.settings.chooseIcon.IconManager.DESIGNER_ALEX;
import static com.animbus.music.ui.settings.chooseIcon.IconManager.DESIGNER_JAKA;
import static com.animbus.music.ui.settings.chooseIcon.IconManager.DESIGNER_NGUYEN;
import static com.animbus.music.ui.settings.chooseIcon.IconManager.DESIGNER_SRINI;

public class ChooseIcon extends ThemableActivity {
    Toolbar toolbar;
    SettingsManager settings;
    ThemeManager themeManager;
    Icon icon, iconOld;
    Intent shortcutClickedIntent, addShortcutIntent, removeShortcutIntent;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings_choose_icon);
    }

    @Override
    protected void setVariables() {
        settings = SettingsManager.get();
        IconManager.get().setContext(this);
        themeManager = ThemeManager.get();
        toolbar = (Toolbar) findViewById(R.id.choose_icon_appbar);
        iconOld = IconManager.get().getIcon();
        icon = iconOld;
    }

    @Override
    protected void setUp() {
        setSupportActionBar(toolbar);
        //More Themeing
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_exit_light);
        } else {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_exit);
        }
        selectIcon(iconOld);
    }

    @Override
    protected void setUpTheme(Theme theme) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings_choose_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_fix_icons:
                resetIcons();
                break;
            case R.id.action_test_icons:
                IconManager.get().enableAll();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectIcon(Icon icon) {
        int id = icon.getId();
        if (id == IconManager.get().getID(DESIGNER_SRINI, COLOR_BLACK)) {
            selectIcon(findViewById(R.id.settings_choose_icon_srini_black), icon);
        } else if (id == IconManager.get().getID(DESIGNER_SRINI, COLOR_WHITE)) {
            selectIcon(findViewById(R.id.settings_choose_icon_srini_white), icon);
        } else if (id == IconManager.get().getID(DESIGNER_SRINI, COLOR_RED)) {
            selectIcon(findViewById(R.id.settings_choose_icon_srini_red), icon);
        } else if (id == IconManager.get().getID(DESIGNER_SRINI, COLOR_GREEN)) {
            selectIcon(findViewById(R.id.settings_choose_icon_srini_green), icon);
        } else if (id == IconManager.get().getID(DESIGNER_SRINI, COLOR_BLUE)) {
            selectIcon(findViewById(R.id.settings_choose_icon_srini_blue), icon);
        } else if (id == IconManager.get().getID(DESIGNER_ALEX, COLOR_BLACK)) {
            selectIcon(findViewById(R.id.settings_choose_icon_alex_black), icon);
        } else if (id == IconManager.get().getID(DESIGNER_ALEX, COLOR_WHITE)) {
            selectIcon(findViewById(R.id.settings_choose_icon_alex_white), icon);
        } else if (id == IconManager.get().getID(DESIGNER_ALEX, COLOR_COLORFUL)) {
            selectIcon(findViewById(R.id.settings_choose_icon_alex_color), icon);
        } else if (id == IconManager.get().getID(DESIGNER_JAKA, COLOR_BLACK)) {
            selectIcon(findViewById(R.id.settings_choose_icon_jaka_black), icon);
        } else if (id == IconManager.get().getID(DESIGNER_JAKA, COLOR_WHITE)) {
            selectIcon(findViewById(R.id.settings_choose_icon_jaka_white), icon);
        } else if (id == IconManager.get().getID(DESIGNER_NGUYEN, COLOR_ORANGE)) {
            selectIcon(findViewById(R.id.settings_choose_icon_nguyen_orange), icon);
        } else if (id == IconManager.get().getID(DESIGNER_NGUYEN, COLOR_GREEN)) {
            selectIcon(findViewById(R.id.settings_choose_icon_nguyen_green), icon);
        } else if (id == IconManager.get().getID(DESIGNER_NGUYEN, COLOR_RED)) {
            selectIcon(findViewById(R.id.settings_choose_icon_nguyen_red), icon);
        }
    }

    public void selectIcon(View v) {
        deselectAll();

        int color;
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
            color = getResources().getColor(R.color.primaryGreyLight);
        } else {
            color = getResources().getColor(R.color.primaryGreyDark);
        }
        v.setBackgroundColor(color);

        icon = IconManager.get().getIcon(v);
    }

    public void selectIcon(View v, Icon icon) {
        int color;
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
            color = getResources().getColor(R.color.primaryGreyLight);
        } else {
            color = getResources().getColor(R.color.primaryGreyDark);
        }
        v.setBackgroundColor(color);

        this.icon = icon;
    }

    public void deselectAll() {
        int color;
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
            color = getResources().getColor(R.color.primaryLight);
        } else {
            color = getResources().getColor(R.color.primaryDark);
        }
        findViewById(R.id.settings_choose_icon_srini_black).setBackgroundColor(color);
        findViewById(R.id.settings_choose_icon_srini_white).setBackgroundColor(color);
        findViewById(R.id.settings_choose_icon_srini_red).setBackgroundColor(color);
        findViewById(R.id.settings_choose_icon_strini_slate).setBackgroundColor(color);
        findViewById(R.id.settings_choose_icon_srini_green).setBackgroundColor(color);
        findViewById(R.id.settings_choose_icon_srini_blue).setBackgroundColor(color);

        findViewById(R.id.settings_choose_icon_alex_black).setBackgroundColor(color);
        findViewById(R.id.settings_choose_icon_alex_white).setBackgroundColor(color);
        findViewById(R.id.settings_choose_icon_alex_color).setBackgroundColor(color);

        findViewById(R.id.settings_choose_icon_jaka_black).setBackgroundColor(color);
        findViewById(R.id.settings_choose_icon_jaka_white).setBackgroundColor(color);

        findViewById(R.id.settings_choose_icon_nguyen_orange).setBackgroundColor(color);
        findViewById(R.id.settings_choose_icon_nguyen_green).setBackgroundColor(color);
        findViewById(R.id.settings_choose_icon_nguyen_red).setBackgroundColor(color);
    }

    public void save() {
        settings.setIntSetting(SettingsManager.KEY_ICON, icon.getId());
        iconOld = icon;
        IconManager.get().switchTo(icon);
    }

    /**
     * Its a wrapper so I can use it in an onClick attribute.
     * @param v useless to me...
     */
    public void save(View v){
        save();
    }

   /* private void addShortcut() {
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
    }*/

    @Override
    public void onBackPressed() {
        if (iconOld.getId() != icon.getId()) {
            new AlertDialog.Builder(this).setTitle(R.string.settings_choose_icon_save_title)
                    .setMessage(R.string.settings_choose_icon_save_message)
                    .setPositiveButton(R.string.settings_choose_icon_save_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            save();
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.settings_choose_icon_save_negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNeutralButton(R.string.settings_choose_icon_save_neutral, null)
                    .create().show();
        } else {
            super.onBackPressed();
        }
    }

    private void resetIcons() {
        IconManager.get().disableAll();
        IconManager.get().enable(IconManager.get().getIcon(DESIGNER_SRINI, COLOR_BLACK));
        save();
    }

    public void openSrini(View v) {
        String url = "https://plus.google.com/+SriniKumarREM";
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
    }

    public void openAlex(View v) {
        String url = "https://plus.google.com/+AlexMueller392";
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
    }

    public void openJaka(View v) {
        String url = "https://plus.google.com/+JakaMusic";
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
    }

    public void openNguyen(View v) {
        String url = "https://plus.google.com/111080505870850761155";
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
    }
}
