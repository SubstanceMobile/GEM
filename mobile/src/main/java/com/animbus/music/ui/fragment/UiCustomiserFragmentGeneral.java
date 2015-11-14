package com.animbus.music.ui.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.animbus.music.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class UiCustomiserFragmentGeneral extends Fragment {

    public UiCustomiserFragmentGeneral() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ui_customiser_general, container, false);
    }
}
