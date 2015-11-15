package com.animbus.music.ui.activity.settings;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.animbus.music.BuildConfig;
import com.animbus.music.R;
import com.animbus.music.ui.custom.activity.ThemableActivity;
import com.animbus.music.util.IconManager;
import com.animbus.music.ui.activity.theme.Theme;
import com.animbus.music.ui.activity.theme.ThemeManager;

public class About extends ThemableActivity {
    Toolbar toolbar;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void setVariables() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected void setUp() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) findViewById(R.id.about_version_text_view)).setText(BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
        DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) findViewById(R.id.about_code_icon)).getDrawable()), !ThemeManager.get().useLightTheme ? Color.WHITE : Color.BLACK);
        DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) findViewById(R.id.about_version_icon)).getDrawable()), !ThemeManager.get().useLightTheme ? Color.WHITE : Color.BLACK);
        ((ImageView) findViewById(R.id.about_icon))
                .setImageDrawable(getResources().getDrawable(IconManager.get().getDrawable(IconManager.get()
                        .getOverviewIcon(IconManager.get().getIcon()).getId())));
    }

    @Override
    protected void setUpTheme(Theme theme) {

    }

    private void startUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Libraries
    ///////////////////////////////////////////////////////////////////////////

    public void openSourceCode(View v) {
        startUrl("https://github.com/Substance-Project/GEM");
    }

    public void openAppCompat(View v) {
        startUrl("https://developer.android.com/tools/support-library/index.html");
    }

    public void openPalette(View v) {
        startUrl("https://developer.android.com/tools/support-library/features.html#v7-palette");
    }

    public void openGlide(View v) {
        startUrl("https://github.com/bumptech/glide");
    }

    public void openGitty(View v) {
        startUrl("https://github.com/PaoloRotolo/GittyReporter");
    }

    public void openAppIntro(View v) {
        startUrl("https://github.com/PaoloRotolo/AppIntro");
    }

    public void openFastScroll(View v) {
        startUrl("https://github.com/plusCubed/recycler-fast-scroll");
    }

    public void openDialogs(View v) {
        startUrl("https://github.com/afollestad/material-dialogs");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Special thanks
    ///////////////////////////////////////////////////////////////////////////

    public void openSrini(View v) {
        startUrl("https://plus.google.com/+SriniKumarREM/posts");
    }

    public void openAlex(View v) {
        startUrl("https://plus.google.com/+AlexMueller392/posts");
    }

    public void openJaka(View v) {
        startUrl("https://plus.google.com/+JakaMusic/posts");
    }

    public void openNguyen(View v) {
        startUrl("https://plus.google.com/111080505870850761155/posts");
    }

    public void openKarim(View v) {
        startUrl("https://plus.google.com/+KarimAbouZeid23697/posts");
    }

    public void openNeel(View v) {
        startUrl("https://plus.google.com/+NeelRaj/posts");
    }

    public void openSubstance(View v) {
        startUrl("http://substanceproject.net/");
    }

}
