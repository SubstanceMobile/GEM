package com.animbus.music.ui.activity.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.animbus.music.BuildConfig;
import com.animbus.music.R;
import com.animbus.music.ui.custom.activity.ThemeActivity;
import com.animbus.music.util.Options;

public class About extends ThemeActivity {

    private static final String SOURCE = "https://github.com/Substance-Project/GEM";

    //DO NOT edit this variable. If you do, you will be reported
    private static final String BASE_SOURCE = "https://github.com/Substance-Project/GEM";

    @Override
    protected void init() {
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void setVariables() {

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setUp() {
        ((TextView) findViewById(R.id.about_version_text_view)).setText(BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
        DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) findViewById(R.id.about_code_icon)).getDrawable()), !Options.isLightTheme() ? Color.WHITE : Color.BLACK);
        DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) findViewById(R.id.about_code_og_icon)).getDrawable()), !Options.isLightTheme() ? Color.WHITE : Color.BLACK);
        DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) findViewById(R.id.about_version_icon)).getDrawable()), !Options.isLightTheme() ? Color.WHITE : Color.BLACK);
        DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) findViewById(R.id.about_play_icon)).getDrawable()), !Options.isLightTheme() ? Color.WHITE : Color.BLACK);
        ((ImageView) findViewById(R.id.about_icon)).setImageResource(
                Options.isLightTheme() ?
                        R.mipmap.ic_launcher_srini_black :
                        R.mipmap.ic_launcher_srini_white);
        findViewById(R.id.origional_source_item).setVisibility(!SOURCE.equals(BASE_SOURCE) ? View.VISIBLE : View.GONE);
    }

    private void startUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Top Card
    ///////////////////////////////////////////////////////////////////////////

    public void openSourceCode(View v) {
        startUrl(SOURCE);
    }

    public void openBaseSource(View v) {
        startUrl(BASE_SOURCE);
    }

    public void openPlayStore(View v) {
        startUrl("https://play.google.com/store/apps/dev?id=4871620813352984682");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Libraries
    ///////////////////////////////////////////////////////////////////////////

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
        startUrl("https://substanceproject.net/");
    }

}
