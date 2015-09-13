package com.animbus.music.ui.mainScreen;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.animbus.music.media.PlaybackManager;
import com.animbus.music.media.QueueManager;
import com.animbus.music.media.objects.Song;
import com.animbus.music.R;
import com.animbus.music.media.MediaData;
import com.animbus.music.data.adapter.SongListAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageSongs extends Fragment implements SongListAdapter.SongListItemClickListener {
    Context cxt;
    MediaData dataManager = MediaData.get();
    RecyclerView list;
    MainScreen activity;

    public PageSongs() {
        // Required empty public constructor
    }

    public static PageSongs setUp(Context cxt, MainScreen mainScreen){
        PageSongs instance = new PageSongs();
        instance.cxt = cxt;
        instance.activity = mainScreen;
        BackupHub.get().fragmentSongsCxt = cxt;
        BackupHub.get().fragmentSongsMyLib = mainScreen;
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
            cxt = BackupHub.get().fragmentSongsCxt;
        }
        super.onStart();
        list = (RecyclerView) getView().findViewById(R.id.page_songs_list);
        //Configures the Recyclerview
        SongListAdapter adapter = new SongListAdapter(cxt, dataManager.getSongs());
        adapter.setOnItemClickedListener(this);
        list.setAdapter(adapter);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setLayoutManager(new LinearLayoutManager(cxt, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void SongListItemClicked(int position, List<Song> data) {
        if (activity == null){
            activity = BackupHub.get().fragmentSongsMyLib;
        }
        PlaybackManager.get().play(data, position);
        activity.quickToolbar.setVisibility(View.VISIBLE);
        activity.quickToolbar.setTranslationY(200.0f);
        activity.quickToolbar.animate().translationY(0).start();


    }
}
