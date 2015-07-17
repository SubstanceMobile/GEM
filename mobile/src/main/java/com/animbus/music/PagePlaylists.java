package com.animbus.music;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class PagePlaylists extends Fragment {


    public PagePlaylists() {
        // Required empty public constructor
    }

    public static PagePlaylists setUp(Context cxt){
        PagePlaylists instance = new PagePlaylists();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_playlists, container, false);
    }


}
