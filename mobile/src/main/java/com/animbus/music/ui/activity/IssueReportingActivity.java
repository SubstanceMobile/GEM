package com.animbus.music.ui.activity;

import android.os.Bundle;

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
        setExtraInfo(getExtraInfo());

        setBackgroundColor1(getResources().getColor(R.color.primaryGreyDark));
        setBackgroundColor2(getResources().getColor(R.color.primaryGreyDark));
        setRippleColor(getResources().getColor(R.color.accent_blue_dark));
        setFabColor1(getResources().getColor(R.color.accent_blue_dark), getResources().getColor(R.color.accent_blue_dark),
                getResources().getColor(R.color.ripple_dark));
        setFabColor2(getResources().getColor(R.color.accent_blue_dark), getResources().getColor(R.color.accent_blue_dark),
                getResources().getColor(R.color.ripple_dark));
    }

    public String getExtraInfo(){
        String s = "";
        s += "\n App Version: " + BuildConfig.VERSION_NAME;
        s += "\n App Version ID: " + BuildConfig.VERSION_CODE;
        return s;
    }
}
