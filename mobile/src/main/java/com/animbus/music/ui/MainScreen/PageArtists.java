package com.animbus.music.ui.mainScreen;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.animbus.music.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageArtists extends Fragment {


    public PageArtists() {
        // Required empty public constructor
    }

    public static PageArtists setUp(Context cxt){
        PageArtists instance = new PageArtists();
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_artists, container, false);
    }


}
