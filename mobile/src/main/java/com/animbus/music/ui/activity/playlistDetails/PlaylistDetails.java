/*
 * Copyright (C) 2016 Substance Mobile
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
 */

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

import butterknife.Bind;

public class PlaylistDetails extends ThemeActivity {
    Playlist mPlaylist;
    @Bind(R.id.playlist_details_recycler) RecyclerView mRecycler;

    @Override
    protected int getLayout() {
        return R.layout.activity_playlist_details;
    }

    @Override
    protected void setVariables() {
        mPlaylist = Library.findPlaylistById(getIntent().getLongExtra("playlist_id", -1));
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
