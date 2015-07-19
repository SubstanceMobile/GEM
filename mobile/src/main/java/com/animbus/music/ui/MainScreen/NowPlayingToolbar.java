package com.animbus.music.ui.MainScreen;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.animbus.music.ui.NowPlaying.NowPlaying;
import com.animbus.music.ui.NowPlaying.NowPlayingClassic;
import com.animbus.music.R;
import com.animbus.music.ui.NowPlaying.NowPlayingPeek;
import com.animbus.music.SettingsManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingToolbar extends Fragment implements View.OnClickListener {
    View layout;
    Context cxt;

    public NowPlayingToolbar() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_now_playing_toolbar, container, false);
        cxt = getActivity();
        onCreate();
        return layout;
    }

    protected void onCreate() {
        View root = layout.findViewById(R.id.now_playing_toolbar_root_view);
        root.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        SettingsManager settings = new SettingsManager(cxt);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_NOW_PLAYING_PEEK, true)) {
            startActivity(new Intent(cxt, NowPlayingPeek.class));
        } else {
            if (settings.getBooleanSetting(SettingsManager.KEY_USE_NEW_NOW_PLAYING, true)) {
                startActivity(new Intent(cxt, NowPlayingClassic.class));
            } else {
                startActivity(new Intent(cxt, NowPlaying.class));
            }
        }
    }
}
