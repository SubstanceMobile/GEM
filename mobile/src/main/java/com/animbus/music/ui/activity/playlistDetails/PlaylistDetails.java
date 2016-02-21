package com.animbus.music.ui.activity.playlistDetails;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.animbus.music.R;
import com.animbus.music.media.Library;
import com.animbus.music.media.PlaybackRemote;
import com.animbus.music.media.objects.Playlist;
import com.animbus.music.ui.custom.activity.ThemeActivity;
import com.animbus.music.ui.list.ListAdapter;

public class PlaylistDetails extends ThemeActivity {
    Playlist mPlaylist;
    RecyclerView mRecycler;

    @Override
    protected void init() {
        setContentView(R.layout.activity_playlist_details);
    }

    @Override
    protected void setVariables() {
        mPlaylist = Library.findPlaylistById(getIntent().getLongExtra("playlist_id", -1));
        mRecycler = (RecyclerView) findViewById(R.id.playlist_details_recycler);
        setTitle(mPlaylist.getName());
    }

    @Override
    protected void setUp() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configureRecycler();

        //For the transition animation
        ViewCompat.setTransitionName(mRoot, "window");
    }

    private void configureRecycler() {
        mRecycler.setAdapter(new ListAdapter(ListAdapter.TYPE_SONG, mPlaylist.getSongs(), this));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlist_details, menu);
        menu.findItem(R.id.action_play_all).setVisible(!mPlaylist.getSongs().isEmpty());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.action_play_all:
                PlaybackRemote.play(mPlaylist.getSongs(), 0);
                return true;
        }
        return false;
    }
}
