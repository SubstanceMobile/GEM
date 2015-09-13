package com.animbus.music.ui.mainScreen;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.animbus.music.R;
import com.animbus.music.media.MediaData;
import com.animbus.music.data.adapter.AlbumGridAdapter;
import com.animbus.music.media.PlaybackManager;
import com.animbus.music.media.objects.Album;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageAlbums extends Fragment  implements AlbumGridAdapter.AlbumArtGridClickListener{
    RecyclerView list;
    Context cxt;
    MediaData dataManager = MediaData.get();

    public PageAlbums() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_albums, container, false);
    }

    public static PageAlbums setUp(Context cxt){
        PageAlbums instance = new PageAlbums();
        instance.cxt = cxt;
        BackupHub.get().fragmentAlbumsCxt = cxt;
        return instance;
    }

    @Override
    public void onStart() {
        if (cxt == null){
            cxt = BackupHub.get().fragmentAlbumsCxt;
        }
        super.onStart();
        list = (RecyclerView) getView().findViewById(R.id.page_albums_list);
        AlbumGridAdapter adapter = new AlbumGridAdapter(cxt, dataManager.getAlbums());
        adapter.setOnItemClickedListener(this);
        list.setAdapter(adapter);
        list.setItemAnimator(new DefaultItemAnimator());
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            list.setLayoutManager(new GridLayoutManager(cxt, 2, GridLayoutManager.VERTICAL, false));
        } else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            list.setLayoutManager(new GridLayoutManager(cxt, 3, GridLayoutManager.VERTICAL, false));
        }
    }

    @Override
    public void AlbumGridItemClicked(View view, int position, List<Album> data) {
        Snackbar.make(getView(), "Playing album", Snackbar.LENGTH_LONG).show();
        PlaybackManager.get().play(data.get(position).getSongs(), 0);
    }
}
