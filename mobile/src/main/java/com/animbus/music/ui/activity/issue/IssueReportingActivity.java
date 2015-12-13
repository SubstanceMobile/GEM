package com.animbus.music.ui.activity.issue;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.animbus.music.BuildConfig;
import com.animbus.music.R;
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

        setBackgroundColor1(getResources().getColor(R.color.greyDark));
        setBackgroundColor2(getResources().getColor(R.color.greyDark));
        setRippleColor(getResources().getColor(R.color.issue_report_blue));
        setFabColor1(getResources().getColor(R.color.issue_report_blue), getResources().getColor(R.color.issue_report_blue),
                getResources().getColor(R.color.ripple_material_dark));
        setFabColor2(getResources().getColor(R.color.issue_report_blue), getResources().getColor(R.color.issue_report_blue),
                getResources().getColor(R.color.ripple_material_dark));
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
