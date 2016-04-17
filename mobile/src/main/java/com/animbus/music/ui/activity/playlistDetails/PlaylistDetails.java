/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.animbus.music.ui.activity.playlistDetails;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

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
        mRecycler.setAdapter(new ListAdapter<>(ListAdapter.Type.TYPE_SONG, mPlaylist.getSongs(), this));
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
