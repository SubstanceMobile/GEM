package com.animbus.music;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.animbus.music.activities.InstanceSingleton;
import com.animbus.music.activities.MyLibrary;
import com.animbus.music.data.DataManager;
import com.animbus.music.data.adapter.SongListAdapter;
import com.animbus.music.data.objects.Song;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageSongs extends Fragment implements SongListAdapter.SongListItemClickListener {
    Context cxt;
    DataManager dataManager;
    RecyclerView list;
    MyLibrary activity;

    public PageSongs() {
        // Required empty public constructor
    }

    public static PageSongs setUp(Context cxt, MyLibrary myLibrary){
        PageSongs instance = new PageSongs();
        instance.cxt = cxt;
        instance.activity = myLibrary;
        InstanceSingleton.getInstance().fragmentSongsCxt = cxt;
        InstanceSingleton.getInstance().fragmentSongsMyLib = myLibrary;
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_songs, container, false);
    }

    @Override
    public void onStart() {
        if (cxt == null){
            cxt = InstanceSingleton.getInstance().fragmentSongsCxt;
        }
        super.onStart();
        list = (RecyclerView) getView().findViewById(R.id.page_songs_list);
        dataManager = new DataManager(cxt);
        //Configures the Recyclerview
        SongListAdapter adapter = new SongListAdapter(cxt, dataManager.getSongListData());
        adapter.setOnItemClickedListener(this);
        list.setAdapter(adapter);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setLayoutManager(new LinearLayoutManager(cxt, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void SongListItemClicked(int position, List<Song> data) {
        if (activity == null){
            activity = InstanceSingleton.getInstance().fragmentSongsMyLib;
        }
        MediaController controller = MediaController.getInstance();
        controller.startPlayback(data,position);
        activity.quickToolbar.setVisibility(View.VISIBLE);
        activity.quickToolbar.setTranslationY(200.0f);
        activity.quickToolbar.animate().translationY(0).start();
    }
}
