package com.animbus.music.customImpls;

import android.os.Bundle;

import com.github.paolorotolo.gitty_reporter.GittyReporter;

/**
 * Created by Adrian on 10/30/2015.
 */
public class IssueReportingActivity extends GittyReporter {
    @Override
    public void init(Bundle savedInstanceState) {
        setTargetRepository("Substance-Project", "GEM");
        setGuestOAuth2Token("900d0e425de5c28998b3c9564a8e669554f72884");
        enableGuestGitHubLogin(true);
        enableUserGitHubLogin(true);
    }
}
