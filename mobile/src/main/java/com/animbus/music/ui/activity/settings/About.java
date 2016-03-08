package com.animbus.music.ui.activity.settings;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.animbus.music.BuildConfig;
import com.animbus.music.R;
import com.animbus.music.ui.custom.activity.ThemeActivity;
import com.animbus.music.util.Options;

import butterknife.OnClick;

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
        findViewById(R.id.about_base_source_code).setVisibility(!SOURCE.equals(BASE_SOURCE) ? View.VISIBLE : View.GONE);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Top Card
    ///////////////////////////////////////////////////////////////////////////

    @OnClick(R.id.about_source_code) void openSourceCode() {
        startUrl(SOURCE);
    }

    @OnClick(R.id.about_base_source_code) void openBaseSource() {
        startUrl(BASE_SOURCE);
    }

    @OnClick(R.id.about_play) void openPlayStore() {
        startUrl("https://play.google.com/store/apps/dev?id=4871620813352984682");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Main Credits
    ///////////////////////////////////////////////////////////////////////////

    @OnClick(R.id.about_substance) void openSubstance() {
        startUrl("https://substanceproject.net/");
    }

    @OnClick(R.id.about_adrian) void openAdrian() {
        startUrl("https://substanceproject.net/");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Libraries
    ///////////////////////////////////////////////////////////////////////////

    @OnClick(R.id.about_support) void openAppCompat() {
        startUrl("https://developer.android.com/tools/support-library/index.html");
    }

    @OnClick(R.id.about_palette) void openPalette() {
        startUrl("https://developer.android.com/tools/support-library/features.html#v7-palette");
    }

    @OnClick(R.id.about_glide) void openGlide() {
        startUrl("https://github.com/bumptech/glide");
    }

    @OnClick(R.id.about_gitty) void openGitty() {
        startUrl("https://github.com/PaoloRotolo/GittyReporter");
    }

    @OnClick(R.id.about_appintro) void openAppIntro() {
        startUrl("https://github.com/PaoloRotolo/AppIntro");
    }

    @OnClick(R.id.about_fast_scroll) void openFastScroll() {
        startUrl("https://github.com/plusCubed/recycler-fast-scroll");
    }

    @OnClick(R.id.about_material_dialogs) void openDialogs() {
        startUrl("https://github.com/afollestad/material-dialogs");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Special thanks
    ///////////////////////////////////////////////////////////////////////////

    @OnClick(R.id.about_srini) void openSrini() {
        startUrl("https://plus.google.com/+SriniKumarREM/posts");
    }

    @OnClick(R.id.about_alex) void openAlex() {
        startUrl("https://plus.google.com/+AlexMueller392/posts");
    }

    @OnClick(R.id.about_jaka) void openJaka() {
        startUrl("https://plus.google.com/+JakaMusic/posts");
    }

    @OnClick(R.id.about_nguyen) void openNguyen() {
        startUrl("https://plus.google.com/111080505870850761155/posts");
    }

    @OnClick(R.id.about_karim) void openKarim() {
        startUrl("https://plus.google.com/+KarimAbouZeid23697/posts");
    }

    @OnClick(R.id.about_neel) void openNeel() {
        startUrl("https://plus.google.com/+NeelRaj/posts");
    }

}
