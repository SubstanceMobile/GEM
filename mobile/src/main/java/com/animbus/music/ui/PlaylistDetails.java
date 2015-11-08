package com.animbus.music.ui;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.animbus.music.R;
import com.animbus.music.customImpls.ThemableActivity;
import com.animbus.music.data.list.ListAdapter;
import com.animbus.music.media.Library;
import com.animbus.music.media.PlaybackManager;
import com.animbus.music.media.objects.Playlist;
import com.animbus.music.ui.theme.Theme;

public class PlaylistDetails extends ThemableActivity {
    Toolbar mToolbar;
    Playlist mPlaylist;
    RecyclerView mRecycler;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_playlist_details);
    }

    @Override
    protected void setVariables() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mPlaylist = Library.findPlaylistById(getIntent().getLongExtra("playlist_id", -1));
        mRecycler = (RecyclerView) findViewById(R.id.playlist_details_recycler);
    }

    @Override
    protected void setUp() {
        mToolbar.setTitle(mPlaylist.getName());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configureRecycler();
    }

    private void configureRecycler() {
        mRecycler.setAdapter(new ListAdapter(ListAdapter.TYPE_SONG, mPlaylist.getSongs(), this));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void setUpTheme(Theme theme) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlist_details, menu);
        menu.findItem(R.id.action_play_all).setVisible(!mPlaylist.getSongs().isEmpty());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())  {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.action_play_all:
                PlaybackManager.get().play(mPlaylist.getSongs(), 0);
                return true;
        }
        return false;
    }
}
