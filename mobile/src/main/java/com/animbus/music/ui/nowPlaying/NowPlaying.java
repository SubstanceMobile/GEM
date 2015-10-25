package com.animbus.music.ui.nowPlaying;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.animbus.music.R;
import com.animbus.music.customImpls.MusicControlsView;
import com.animbus.music.customImpls.ThemableActivity;
import com.animbus.music.data.adapter.NowPlayingAdapter;
import com.animbus.music.media.PlaybackManager;
import com.animbus.music.media.objects.Song;
import com.animbus.music.ui.settings.Settings;
import com.animbus.music.ui.settings.chooseIcon.IconManager;
import com.animbus.music.ui.theme.Theme;

import java.util.List;

public class NowPlaying extends ThemableActivity implements PlaybackManager.OnChangedListener {
    Toolbar mToolbar;
    CollapsingToolbarLayout mCollapsingToolbar;
    RecyclerView mList;
    Song mSong;
    LinearLayout mControlsRoot;
    MusicControlsView mControls;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_now_playing);
        PlaybackManager.get().registerListener(this);
        configureTransition();
        mSong = PlaybackManager.get().getCurrentSong();
    }

    private void configureTransition() {
        ViewCompat.setTransitionName(findViewById(R.id.now_playing_album_art), "art");
        ViewCompat.setTransitionName(findViewById(R.id.now_playing_recycler), "list");
        ViewCompat.setTransitionName(findViewById(R.id.now_playing_controls_root), "controls");
        ViewCompat.setTransitionName(findViewById(R.id.now_playing_toolbar), "appbar");
        ViewCompat.setTransitionName(findViewById(R.id.now_playing_toolbar_text_protection), "appbar_text_protection");
    }

    @Override
    protected void setVariables() {
        mToolbar = (Toolbar) findViewById(R.id.now_playing_toolbar);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.now_playing_collapsing_toolbar);
        mList = (RecyclerView) findViewById(R.id.now_playing_recycler);
        mControlsRoot = (LinearLayout) findViewById(R.id.now_playing_controls_root);
        mControls = (MusicControlsView) findViewById(R.id.now_playing_media_controller_view);
    }

    @Override
    protected void setUp() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable menu = getResources().getDrawable(R.drawable.ic_close_24dp);
        DrawableCompat.setTint(menu, getResources().getColor(R.color.primaryLight));
        getSupportActionBar().setHomeAsUpIndicator(menu);
        mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
        ViewCompat.setElevation(findViewById(R.id.now_playing_controls_root), 12f);
        configureRecyclerView();
        configureControls();
        configureUI();
    }

    private void configureRecyclerView() {
        NowPlayingAdapter adapter = new NowPlayingAdapter(this);
        mList.setAdapter(adapter);
        adapter.setOnItemClickedListener(new NowPlayingAdapter.NowPlayingClickedListener() {
            @Override
            public void onNowPlayingItemClicker(View v, List<Song> data, int pos) {
                if (pos != -1) {
                    PlaybackManager.get().playQueueItem(pos);
                }
            }
        });
        mList.setItemAnimator(new DefaultItemAnimator());
        mList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mList.setNestedScrollingEnabled(true);
    }

    private void configureUIColors() {
        mControlsRoot.setBackgroundColor(mSong.getAlbum().getAccentColor());
        mCollapsingToolbar.setContentScrimColor(mSong.getAlbum().getAccentColor());
        mCollapsingToolbar.setStatusBarScrimColor(mSong.getAlbum().getAccentColor());
        mControls.setUIColors(mSong.getAlbum().getAccentIconColor(),
                mSong.getAlbum().getAccentSecondaryIconColor(),
                mSong.getAlbum().getAccentSecondaryIconColor(),
                mSong.getAlbum().getBackgroundColor());
    }

    private void configureUI() {
        ImageView mImage = (ImageView) findViewById(R.id.now_playing_album_art);
        mSong.getAlbum().requestArt(mImage);
        configureUIColors();

        //Sets Window description in Multitasking menu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IconManager iconM = IconManager.get().setContext(this);
            Bitmap bm = BitmapFactory.decodeResource(getResources(), iconM.getDrawable(iconM.getOverviewIcon(iconM.getIcon()).getId()));
            setTaskDescription(new ActivityManager.TaskDescription(mSong.getSongTitle(), bm, mSong.getAlbum().getAccentColor()));
            bm.recycle();
        }
    }

    private void configureControls() {
        mControls.initView();
        mControls.setController(PlaybackManager.get().getService().getSession().getController());
    }

    @Override
    protected void setUpTheme(Theme theme) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_now_playing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_stop:
                PlaybackManager.get().stop();
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, Settings.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSongChanged(Song song) {
        mSong = song;
        configureUI();
    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mControls.onKeyEvent(event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
