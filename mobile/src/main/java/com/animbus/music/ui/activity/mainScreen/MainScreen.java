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

package com.animbus.music.ui.activity.mainScreen;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.util.ATEUtil;
import com.animbus.music.BuildConfig;
import com.animbus.music.R;
import com.animbus.music.media.Library;
import com.animbus.music.media.PlaybackRemote;
import com.animbus.music.media.objects.Song;
import com.animbus.music.ui.activity.issue.IssueReportingActivity;
import com.animbus.music.ui.activity.nowPlaying.NowPlaying;
import com.animbus.music.ui.activity.search.SearchActivity;
import com.animbus.music.ui.activity.settings.Settings;
import com.animbus.music.ui.activity.setup.SetupActivity;
import com.animbus.music.ui.custom.activity.ThemeActivity;
import com.animbus.music.ui.custom.view.LockableViewPager;
import com.animbus.music.ui.list.ListAdapter;
import com.animbus.music.util.GEMUtil;
import com.animbus.music.util.Options;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import com.pluscubed.recyclerfastscroll.RecyclerFastScrollerUtils;

import butterknife.Bind;
import butterknife.BindString;

public class MainScreen extends ThemeActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.main_tab_layout) TabLayout mTabs;
    @Bind(R.id.main_view_pager) LockableViewPager mPager;
    @Bind(R.id.navigation) NavigationView mNavigationView;
    @Bind(R.id.main_screen_now_playing_toolbar) View quickToolbar;
    @BindString(R.string.title_activity_main) String mScreenName;
    private static final int RESET_AT = 16;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void setUp() {
        //noinspection ConstantConditions, PointlessBooleanExpression;
        if ((BuildConfig.VERSION_CODE == RESET_AT || BuildConfig.BUILD_TYPE.equals("debug")) && !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("did_reset", false)) {
            Options.resetPrefs();
            Options.restartApp();
        }

        //Play a song if the intent wants it to
        if (getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_VIEW))
            PlaybackRemote.play(getIntent().getData());

        //The rest of the UI
        setUpNavigation();
        mPager.setAdapter(new RecyclerPagerAdapter());
        mPager.setOffscreenPageLimit(3);
        switchToAlbum();
        setTitle(mScreenName);
        setUpNowPlayingBar(null, null);
        requestPermissions();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Permissions
    ///////////////////////////////////////////////////////////////////////////

    @SuppressLint("NewApi")
    public void requestPermissions() {
        if (GEMUtil.isMarshmallow() && (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
            startActivityForResult(new Intent(this, SetupActivity.class), 1);
        else Library.build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) recreate();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Configuration and setup
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
        setUpDrawerHeader(PlaybackRemote.getCurrentSong());
    }

    private void setUpDrawerHeader(Song song) {
        if (song != null) {
            View header = mNavigationView.getHeaderView(0);
            if (header != null && song != null) {
                song.getAlbum().requestArt((ImageView) header.findViewById(R.id.navdrawer_header_image));
                header.findViewById(R.id.navdrawer_header_clickable).setBackground(ContextCompat.getDrawable(this, !ATEUtil.isColorLight(song.getAlbum().getBackgroundColor()) ? R.drawable.ripple_dark : R.drawable.ripple_light));
            }
        }

        if (PlaybackRemote.isActive() && mNavigationView.getHeaderCount() == 0)
            mNavigationView.inflateHeaderView(R.layout.drawer_header);
        else mNavigationView.removeHeaderView(mNavigationView.getHeaderView(0));
    }

    private void setUpNavigation() {
        ///////////////////////
        // Navigation Drawer //
        ///////////////////////
        mNavigationView.setNavigationItemSelectedListener(this);
        PlaybackRemote.registerStateListener(new PlaybackRemote.StateChangedListener() {
            @Override
            public void onStateChanged(PlaybackStateCompat newState) {
                setUpDrawerHeader(null);
            }
        });

        PlaybackRemote.registerSongListener(new PlaybackRemote.SongChangedListener() {
            @Override
            public void onSongChanged(Song newSong) {
                setUpDrawerHeader(newSong);
            }
        });

        if (GEMUtil.isLollipop()) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.RED);
        }

        mDrawerLayout.setStatusBarBackgroundColor(getPrimaryDarkColor());

        //////////
        // Tabs //
        //////////
        if (!Options.usingTabs()) {
            mPager.lock();
            mTabs.setVisibility(View.GONE);
            return;
        }

        //Makes tabs scrollable or fixed based on setting
        mTabs.setTabMode(Options.usingScrollableTabs() ? TabLayout.MODE_SCROLLABLE : TabLayout.MODE_FIXED);
        if (!Options.usingScrollableTabs()) mTabs.setPadding(0, 0, 0, 0);

        //Configures and adds tabs
        TabLayout.Tab songsTab, albumsTab, playlistsTab, artistsTab;
        if (!Options.usingIconTabs()) {
            songsTab = mTabs.newTab().setText(R.string.page_songs);
            albumsTab = mTabs.newTab().setText(R.string.page_albums);
            artistsTab = mTabs.newTab().setText(R.string.page_artists);
            playlistsTab = mTabs.newTab().setText(R.string.page_playlists);
        } else {
            songsTab = mTabs.newTab().setIcon(R.drawable.ic_audiotrack_24dp);
            albumsTab = mTabs.newTab().setIcon(R.drawable.ic_album_24dp);
            artistsTab = mTabs.newTab().setIcon(R.drawable.ic_artist_24dp);
            playlistsTab = mTabs.newTab().setIcon(R.drawable.ic_queue_music_black_24dp);
        }
        mTabs.addTab(songsTab);
        mTabs.addTab(albumsTab);
        mTabs.addTab(artistsTab);
        mTabs.addTab(playlistsTab);

        //Allows the tabs to sync to the view pager
        mTabs.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mPager));
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
    }

    private void setUpNowPlayingBar(Song song, PlaybackStateCompat state) {
        if (!PlaybackRemote.isActive()) {
            /*quickToolbar.setVisibility(View.GONE);*/
            //TODO: Add This
        } else {
            setUpNowPlayingBar(PlaybackRemote.getCurrentSong(), PlaybackRemote.getState());
        }

        PlaybackRemote.registerSongListener(new PlaybackRemote.SongChangedListener() {
            @Override
            public void onSongChanged(Song newSong) {
                setUpNowPlayingBar(newSong, null);
            }
        });

        PlaybackRemote.registerStateListener(new PlaybackRemote.StateChangedListener() {
            @Override
            public void onStateChanged(PlaybackStateCompat newState) {
                setUpNowPlayingBar(null, newState);
            }
        });

        quickToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainScreen.this,
                        new Pair<>(v.findViewById(R.id.main_screen_now_playing_toolbar_art), "art"),
                        new Pair<>(v.findViewById(R.id.main_screen_now_playing_toolbar_controls_transition), "controls"),
                        new Pair<>(v.findViewById(R.id.main_screen_now_playing_toolbar_title), "title"),
                        new Pair<>(v.findViewById(R.id.main_screen_now_playing_toolbar_artist), "artist")
                );
                ActivityCompat.startActivity(MainScreen.this, new Intent(MainScreen.this, NowPlaying.class), options.toBundle());
            }
        });
        if (song != null) {
            song.getAlbum().requestArt((ImageView) findViewById(R.id.main_screen_now_playing_toolbar_art));
            TextView title = (TextView) quickToolbar.findViewById(R.id.main_screen_now_playing_toolbar_title),
                    artist = (TextView) quickToolbar.findViewById(R.id.main_screen_now_playing_toolbar_artist);
            title.setText(song.getSongTitle());
            artist.setText(song.getSongArtist());
        }
        if (state != null) {
            if (state.getState() == PlaybackStateCompat.STATE_STOPPED || state.getState() == PlaybackStateCompat.STATE_NONE) {
                if (quickToolbar.getVisibility() != View.GONE) {
                    quickToolbar.setTranslationY(0f);
                    quickToolbar.animate().translationY(200f).setInterpolator(new FastOutSlowInInterpolator())
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    quickToolbar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }).start();
                }
            } else {
                if (quickToolbar.getVisibility() != View.VISIBLE) {
                    quickToolbar.setTranslationY(200f);
                    quickToolbar.setVisibility(View.VISIBLE);
                    quickToolbar.animate().translationY(0f).setInterpolator(new FastOutSlowInInterpolator()).start();
                }
                //For inconsistency sake
                quickToolbar.setVisibility(View.VISIBLE);
            }
            ImageButton button = (ImageButton) findViewById(R.id.main_screen_now_playing_toolbar_playpause);
            boolean isPaused = state.getState() == PlaybackStateCompat.STATE_PAUSED;
            if (isPaused) {
                button.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlaybackRemote.resume();
                    }
                });
            } else {
                button.setImageResource(R.drawable.ic_pause_white_48dp);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlaybackRemote.pause();
                    }
                });
            }
        }
    }

    @Override
    protected boolean shouldKeepAppBarShadow() {
        return Options.usingTabs();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Navigation
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected int getOptionsMenu() {
        return R.menu.menu_main;
    }

    @Override
    protected boolean processMenuItem(int id) {
        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_search:
                /*View searchMenuView = mToolbar.findViewById(R.id.action_search);
                int[] loc = new int[2];
                searchMenuView.getLocationOnScreen(loc);
                Intent intent = SearchActivity.createStartIntent(this, loc[0], loc[0] + (searchMenuView.getWidth() / 2));
                ActivityCompat.startActivity(this, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
                searchMenuView.setAlpha(0f);*/
                startActivity(new Intent(this, SearchActivity.class));
                return true;
        }
        return super.processMenuItem(id);
    }

    @Override
    protected boolean overrideUpBehavior() {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) mDrawerLayout.closeDrawers();
        else super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navdrawer_songs:
                switchToSongs();
                break;
            case R.id.navdrawer_album_icon:
                switchToAlbum();
                break;
            case R.id.navdrawer_artists:
                switchToArtists();
                break;
            case R.id.navdrawer_playlists:
                switchToPlaylists();
                break;
            case R.id.navdrawer_settings:
                startActivity(new Intent(this, Settings.class));
                break;
            case R.id.navdrawer_report_bug:
                startActivity(new Intent(this, IssueReportingActivity.class));
                break;
        }
        return true;
    }

    public void switchTab(int pos) {
        try {
            //noinspection ConstantConditions
            mTabs.getTabAt(pos).select();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Pages
    ///////////////////////////////////////////////////////////////////////////

    public void switchToSongs() {
        mNavigationView.setCheckedItem(R.id.navdrawer_songs);
        switchTab(0);
        mPager.setCurrentItem(0);
        if (Options.usingCategoryNames()) {
            mScreenName = getResources().getString(R.string.page_songs);
            setTitle(mScreenName);
        }
        //Closes the Navdrawer
        mDrawerLayout.closeDrawers();
    }

    public void switchToAlbum() {
        mNavigationView.setCheckedItem(R.id.navdrawer_album_icon);
        switchTab(1);
        mPager.setCurrentItem(1);
        if (Options.usingCategoryNames()) {
            mScreenName = getResources().getString(R.string.page_albums);
            setTitle(mScreenName);
        }
        //Closes the Navdrawer
        mDrawerLayout.closeDrawers();
    }

    public void switchToArtists() {
        //Sets the current screen
        mScreenName = getResources().getString(R.string.page_artists);
        mNavigationView.setCheckedItem(R.id.navdrawer_artists);
        switchTab(2);
        mPager.setCurrentItem(2);
        if (Options.usingCategoryNames()) {
            mScreenName = getResources().getString(R.string.page_artists);
            setTitle(mScreenName);
        }
        //Closes the Navdrawer
        mDrawerLayout.closeDrawers();
    }

    public void switchToPlaylists() {
        mNavigationView.setCheckedItem(R.id.navdrawer_playlists);
        switchTab(3);
        mPager.setCurrentItem(3);
        if (Options.usingCategoryNames()) {
            mScreenName = getResources().getString(R.string.page_playlists);
            setTitle(mScreenName);
        }

        //Closes the Navdrawer
        mDrawerLayout.closeDrawers();
    }

    class RecyclerPagerAdapter extends PagerAdapter {
        private String[] titles = new String[]{
                getResources().getString(R.string.page_songs),
                getResources().getString(R.string.page_albums),
                getResources().getString(R.string.page_artists),
                getResources().getString(R.string.page_playlists)
        };

        @Override
        public int getCount() {
            return titles.length;
        }

        private void configureRecycler(RecyclerView recycler, RecyclerFastScroller scroller, int pos) {
            switch (pos) {
                case 0:
                    configureAsSongs(recycler);
                    break;
                case 1:
                    configureAsAlbums(recycler);
                    break;
                case 2:
                    configureAsArtists(recycler);
                    break;
                case 3:
                    configureAsPlaylists(recycler);
                    break;
            }
            scroller.attachRecyclerView(recycler);
            scroller.setTouchTargetWidth(RecyclerFastScrollerUtils.convertDpToPx(MainScreen.this, 16));
            scroller.setHandlePressedColor(getAccentColor());
        }

        private void configureAsSongs(RecyclerView list) {
            list.setAdapter(new ListAdapter(ListAdapter.TYPE_SONG, Library.getSongs(), MainScreen.this));
            list.setItemAnimator(new DefaultItemAnimator());
            list.setLayoutManager(new LinearLayoutManager(MainScreen.this, LinearLayoutManager.VERTICAL, false));
        }

        private void configureAsAlbums(RecyclerView list) {
            list.setAdapter(new ListAdapter(ListAdapter.TYPE_ALBUM, Library.getAlbums(), MainScreen.this).withTransitionActivity(MainScreen.this));
            list.setItemAnimator(new DefaultItemAnimator());
            if (MainScreen.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                list.setLayoutManager(new GridLayoutManager(MainScreen.this, 2, GridLayoutManager.VERTICAL, false));
            else
                list.setLayoutManager(new GridLayoutManager(MainScreen.this, 3, GridLayoutManager.VERTICAL, false));
        }

        private void configureAsArtists(RecyclerView list) {
            list.setAdapter(new ListAdapter(ListAdapter.TYPE_ARTIST, Library.getArtists(), MainScreen.this));
            list.setItemAnimator(new DefaultItemAnimator());
            list.setLayoutManager(new LinearLayoutManager(MainScreen.this, LinearLayoutManager.VERTICAL, false));
        }

        private void configureAsPlaylists(RecyclerView list) {
            list.setAdapter(new ListAdapter(ListAdapter.TYPE_PLAYLIST, Library.getPlaylists(), MainScreen.this).withTransitionActivity(MainScreen.this));
            list.setItemAnimator(new DefaultItemAnimator());
            list.setLayoutManager(new LinearLayoutManager(MainScreen.this, LinearLayoutManager.VERTICAL, false));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position != 2) {
                View root = getLayoutInflater().inflate(R.layout.blank_page, container, false);
                RecyclerView list = (RecyclerView) root.findViewById(R.id.main_screen_page_recycler);
                RecyclerFastScroller scroller = (RecyclerFastScroller) root.findViewById(R.id.main_screen_page_scroller);
                configureRecycler(list, scroller, position);
                container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                ATE.themeView(root, getATEKey());
                return root;
            } else {
                TextView text = new TextView(MainScreen.this);
                text.setSingleLine();
                text.setText(R.string.msg_coming_soon);
                text.setGravity(Gravity.CENTER);
                container.addView(text, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                return text;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }
}
