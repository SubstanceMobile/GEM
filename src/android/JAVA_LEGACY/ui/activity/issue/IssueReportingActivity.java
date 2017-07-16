/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.animbus.music.ui.activity.issue;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;

import com.afollestad.appthemeengine.util.ATEUtil;
import com.animbus.music.BuildConfig;
import com.animbus.music.R;
import com.animbus.music.util.GEMUtil;
import com.github.paolorotolo.gitty_reporter.GittyReporter;

/**
 * Created by Adrian on 10/30/2015.
 */
public class IssueReportingActivity extends GittyReporter {
    @Override
    public void init(Bundle savedInstanceState) {
        setTargetRepository("Substance-Project", "GEM");

        //Using substring so GitHub doesn't terminate the access token
        setGuestOAuth2Token("START4fd1b70e07912c26c60c06ffa220c7c5c417334b".substring(5));
        enableGuestGitHubLogin(true);
        enableUserGitHubLogin(true);
        setExtraInfo(getExtraInfo(getIntent().getStringExtra("error")));
        configureMessage(getIntent().getStringExtra("msg"), getIntent().getStringExtra("type"));

        int background = ContextCompat.getColor(this, R.color.greyDark), accent = ContextCompat.getColor(this, R.color.default_accent),
                ripple = ContextCompat.getColor(this, R.color.ripple_material_dark);
        setBackgroundColor1(background);
        setBackgroundColor2(background);
        setRippleColor(accent);
        setFabColor1(accent, accent, ripple);
        setFabColor2(accent, accent, ripple);
        if (GEMUtil.isLollipop()) getWindow().setStatusBarColor(ATEUtil.darkenColor(background));
    }

    public String getExtraInfo(@Nullable String stackTrace) {
        String s = "";
        s += "\n App Version: " + BuildConfig.VERSION_NAME;
        s += "\n App Version ID: " + BuildConfig.VERSION_CODE;
        if (stackTrace != null) {
            s += "\n Stack Trace :" + stackTrace;
        }
        return s;
    }

    private void configureMessage(@Nullable String message, @Nullable String crashType) {
        if (message != null && crashType != null) {
            EditText bugTitleEditText = (EditText) findViewById(com.github.paolorotolo.gitty_reporter.R.id.gittyreporter_bug_title);
            EditText bugDescriptionEditText = (EditText) findViewById(com.github.paolorotolo.gitty_reporter.R.id.gittyreporter_bug_description);
            bugTitleEditText.setText("Crash Report: " + crashType);
            bugDescriptionEditText.setText(message);
            findViewById(com.github.paolorotolo.gitty_reporter.R.id.gittyreporter_fab_next).performClick();
        }
    }
}
