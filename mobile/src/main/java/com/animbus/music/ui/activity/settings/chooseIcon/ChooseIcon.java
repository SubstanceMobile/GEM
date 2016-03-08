package com.animbus.music.ui.activity.settings.chooseIcon;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.appthemeengine.util.ATEUtil;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.animbus.music.R;
import com.animbus.music.ui.custom.activity.ThemeActivity;
import com.animbus.music.util.IconManager;
import com.animbus.music.util.Options;

import butterknife.OnClick;

import static com.animbus.music.util.IconManager.COLOR_BLACK;
import static com.animbus.music.util.IconManager.COLOR_BLUE;
import static com.animbus.music.util.IconManager.COLOR_COLORFUL;
import static com.animbus.music.util.IconManager.COLOR_GREEN;
import static com.animbus.music.util.IconManager.COLOR_ORANGE;
import static com.animbus.music.util.IconManager.COLOR_RED;
import static com.animbus.music.util.IconManager.COLOR_WHITE;
import static com.animbus.music.util.IconManager.DESIGNER_ALEX;
import static com.animbus.music.util.IconManager.DESIGNER_JAKA;
import static com.animbus.music.util.IconManager.DESIGNER_NGUYEN;
import static com.animbus.music.util.IconManager.DESIGNER_SRINI;

public class ChooseIcon extends ThemeActivity {
    Icon icon, iconOld;

    @Override
    protected void init() {
        setContentView(R.layout.activity_settings_choose_icon);
    }

    @Override
    protected void setVariables() {
        IconManager.get().setContext(this);
        iconOld = IconManager.get().getIcon();
        icon = iconOld;
        selectIcon(icon);
    }

    @Override
    protected void setUp() {
    }

    @Override
    protected void setUpTheme() {
        super.setUpTheme();/*
        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.save_fab);
        mFab.setBackgroundTintList(ColorStateList.valueOf(getAccentColor()));
        DrawableCompat.setTint(DrawableCompat.wrap(mFab.getDrawable()), !ATEUtil.isColorLight(getAccentColor()) ? Color.WHITE : Color.BLACK);*/
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
        v.setBackgroundColor(getSecondaryTextColor());
        icon = IconManager.get().getIcon(v);
    }

    public void selectIcon(View v, Icon icon) {
        v.setBackgroundColor(getSecondaryTextColor());
        this.icon = icon;
    }

    public void deselectAll() {
        findViewById(R.id.settings_choose_icon_srini_black).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.settings_choose_icon_srini_white).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.settings_choose_icon_srini_red).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.settings_choose_icon_strini_slate).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.settings_choose_icon_srini_green).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.settings_choose_icon_srini_blue).setBackgroundColor(Color.TRANSPARENT);

        findViewById(R.id.settings_choose_icon_alex_black).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.settings_choose_icon_alex_white).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.settings_choose_icon_alex_color).setBackgroundColor(Color.TRANSPARENT);

        findViewById(R.id.settings_choose_icon_jaka_black).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.settings_choose_icon_jaka_white).setBackgroundColor(Color.TRANSPARENT);

        findViewById(R.id.settings_choose_icon_nguyen_orange).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.settings_choose_icon_nguyen_green).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.settings_choose_icon_nguyen_red).setBackgroundColor(Color.TRANSPARENT);
    }

    void save() {
        Options.setSavedIconID(icon.getId());
        iconOld = icon;
        IconManager.get().switchTo(icon);
    }

    @OnClick(R.id.fab) void saveAndNotify() {
        save();
        Snackbar.make(mRoot, R.string.saved, Snackbar.LENGTH_SHORT).show();
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
    public void supportFinishAfterTransition() {
        if (iconOld.getId() != icon.getId()) {
            new MaterialDialog.Builder(this).title(R.string.settings_choose_icon_save_title)
                    .content(R.string.settings_choose_icon_save_message).positiveText(android.R.string.yes)
                    .negativeText(android.R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    save();
                    ChooseIcon.super.supportFinishAfterTransition();
                }
            }).onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    ChooseIcon.super.supportFinishAfterTransition();
                }
            }).show();
        } else {
            super.supportFinishAfterTransition();
        }
    }

    private void resetIcons() {
        IconManager.get().disableAll();
        IconManager.get().enable(IconManager.get().getIcon(DESIGNER_SRINI, COLOR_BLACK));
        save();
    }
}
