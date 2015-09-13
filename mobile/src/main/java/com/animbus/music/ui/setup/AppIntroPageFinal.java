package com.animbus.music.ui.setup;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.animbus.music.R;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class AppIntroPageFinal extends AppIntroFragment {

    public AppIntroPageFinal() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_intro_page_final, container, false);
    }

}
