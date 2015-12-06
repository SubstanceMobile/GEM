package com.animbus.music.ui.activity.setup;

import android.os.Bundle;

import com.animbus.music.ui.custom.activity.SetupScreen;
import com.animbus.music.R;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Adrian on 8/3/2015.
 */
public class SetupActivity extends SetupScreen {
    @Override
    public void init(Bundle bundle) {
        showDoneButton(true);
        showSkipButton(true);
        showIndicator(true);
    }

    @Override
    public void onSkipPressed() {
        finish();
    }

    @Override
    public void onDonePressed() {
        finish();
    }
}
