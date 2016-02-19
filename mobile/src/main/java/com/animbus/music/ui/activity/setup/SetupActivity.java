package com.animbus.music.ui.activity.setup;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.afollestad.appthemeengine.util.ATEUtil;
import com.animbus.music.BuildConfig;
import com.animbus.music.R;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Adrian on 8/3/2015.
 */
public class SetupActivity extends AppIntro2 {
    @Override
    public void init(Bundle bundle) {

        int background = Color.parseColor("#303030");
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ATEUtil.darkenColor(background));
        } else if (Build.VERSION.SDK_INT == 19) getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        addSlide(AppIntroFragment.newInstance(
                getResources().getString(R.string.app_name_actual),
                getResources().getString(R.string.setup_intro),
                R.drawable.ic_gem_simple_white_112dp, background));
        addSlide(AppIntroFragment.newInstance(
                getResources().getString(R.string.permission_storage_explain_title),
                getResources().getString(R.string.permission_storage_explain_message),
                R.drawable.ic_folder_white_112dp, background));
        if (BuildConfig.BUILD_TYPE.equals("debug") || BuildConfig.BUILD_TYPE.equals("internal")) addSlide(AppIntroFragment.newInstance(
                getResources().getString(R.string.internal_tester_warning_title),
                getResources().getString(R.string.internal_tester_warning),
                R.drawable.ic_warning_white_112dp, background));
        addSlide(AppIntroFragment.newInstance(
                getResources().getString(R.string.setup_thanks),
                getResources().getString(R.string.enjoy),
                R.drawable.ic_smile_white_112dp, background));

        askForPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
    }

    @Override
    public void onDonePressed() {
        finish();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onSlideChanged() {

    }
}

