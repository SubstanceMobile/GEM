/*
 * Copyright (C) 2016 Substance Mobile
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
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
