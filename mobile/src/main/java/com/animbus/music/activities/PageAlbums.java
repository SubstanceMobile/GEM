package com.animbus.music.activities;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.animbus.music.R;
import com.animbus.music.data.DataManager;
import com.animbus.music.data.adapter.AlbumGridAdapter;
import com.animbus.music.data.objects.Album;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageAlbums extends Fragment  implements AlbumGridAdapter.AlbumArtGridClickListener{
    RecyclerView list;
    Context cxt;
    DataManager dataManager = new DataManager(cxt);

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
        InstanceSingleton.getInstance().fragmentAlbumsCxt = cxt;
        return instance;
    }

    @Override
    public void onStart() {
        if (cxt == null){
            cxt = InstanceSingleton.getInstance().fragmentAlbumsCxt;
        }
        super.onStart();
        list = (RecyclerView) getView().findViewById(R.id.page_albums_list);
        AlbumGridAdapter adapter = new AlbumGridAdapter(cxt, dataManager.getAlbumGridData());
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
        Snackbar.make(getView(), "Removed for Renovation", Snackbar.LENGTH_LONG).setAction("ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }
}
