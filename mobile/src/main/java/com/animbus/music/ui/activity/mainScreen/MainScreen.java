package com.animbus.music.ui.activity.mainScreen;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.animbus.music.R;
import com.animbus.music.media.Library;
import com.animbus.music.media.PlaybackRemote;
import com.animbus.music.media.objects.Song;
import com.animbus.music.ui.activity.IssueReportingActivity;
import com.animbus.music.ui.activity.nowPlaying.NowPlaying;
import com.animbus.music.ui.activity.search.SearchActivity;
import com.animbus.music.ui.activity.settings.Settings;
import com.animbus.music.ui.custom.activity.ThemeActivity;
import com.animbus.music.ui.custom.view.LockableViewPager;
import com.animbus.music.ui.list.ListAdapter;
import com.animbus.music.util.ColorUtil;
import com.animbus.music.util.Options;
import com.animbus.music.util.SettingsManager;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import com.pluscubed.recyclerfastscroll.RecyclerFastScrollerUtils;


public class MainScreen extends ThemeActivity implements NavigationView.OnNavigationItemSelectedListener {
    View quickToolbar;
    String mScreenName;
    SettingsManager settings;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    Menu mNavMenu;
    LockableViewPager mPager;
    TabLayout mTabs;

    @Override
    protected void init() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void setVariables() {
        settings = SettingsManager.get();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        quickToolbar = findViewById(R.id.main_screen_now_playing_toolbar);
        mTabs = (TabLayout) findViewById(R.id.main_tab_layout);
        mPager = (LockableViewPager) findViewById(R.id.main_view_pager);
    }

    @Override
    protected void setUp() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable menu = DrawableCompat.wrap(getResources().getDrawable(R.drawable.ic_menu_24dp));
        DrawableCompat.setTint(menu, getPrimaryTextColor());
        getSupportActionBar().setHomeAsUpIndicator(menu);
        setUpNavdrawer();
        setUpTabs();
        mPager.setAdapter(new RecyclerPagerAdapter());
        mPager.setOffscreenPageLimit(3);
        goToDefaultPage();
        mToolbar.setTitle(Options.usingCategoryNames() ? mScreenName : getResources().getString(R.string.title_activity_main));
        configureNowPlayingBar();
    }

    @Override
    protected void setUpTheme() {
        super.setUpTheme();
        if (Build.VERSION.SDK_INT >= 21) getWindow().setStatusBarColor(Color.TRANSPARENT);
        mDrawerLayout.setStatusBarBackgroundColor(getPrimaryDarkColor());
    }

    private void configureNowPlayingBar() {
        if (!PlaybackRemote.isActive()) {
            quickToolbar.setVisibility(View.GONE);
        } else {
            try {
                setUpNowPlayingBarWithSong(PlaybackRemote.getCurrentSong());
                setUpNowPlayingBarWithState(PlaybackRemote.getState());
            } catch (Exception ignored) {
            }
        }

        PlaybackRemote.registerSongListener(new PlaybackRemote.SongChangedListener() {
            @Override
            public void onSongChanged(Song newSong) {
                setUpNowPlayingBarWithSong(newSong);
            }
        });

        PlaybackRemote.registerStateListener(new PlaybackRemote.StateChangedListener() {
            @Override
            public void onStateChanged(PlaybackStateCompat newState) {
                setUpNowPlayingBarWithState(newState);
            }
        });

        quickToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainScreen.this,
                        new Pair<View, String>(v.findViewById(R.id.main_screen_now_playing_toolbar_art), "art"),
                        new Pair<View, String>(v.findViewById(R.id.main_screen_now_playing_toolbar_controls_transition), "controls"),
                        new Pair<View, String>(v.findViewById(R.id.main_screen_now_playing_toolbar_title), "title"),
                        new Pair<View, String>(v.findViewById(R.id.main_screen_now_playing_toolbar_artist), "artist")
                );
                ActivityCompat.startActivity(MainScreen.this, new Intent(MainScreen.this, NowPlaying.class), options.toBundle());
            }
        });
    }

    private void setUpNowPlayingBarWithSong(Song song) {
        song.getAlbum().requestArt((ImageView) findViewById(R.id.main_screen_now_playing_toolbar_art));
        TextView title = (TextView) quickToolbar.findViewById(R.id.main_screen_now_playing_toolbar_title),
                artist = (TextView) quickToolbar.findViewById(R.id.main_screen_now_playing_toolbar_artist);
        title.setText(song.getSongTitle());
        artist.setText(song.getSongArtist());
    }

    private void setUpNowPlayingBarWithState(PlaybackStateCompat state) {
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

    private void setUpNavdrawer() {
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.inflateMenu(R.menu.navigation_drawer_items);
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        {android.R.attr.state_checked}, //When selected
                        {}
                },
                new int[]{
                        getAccentColor(), //When selected
                        getSecondaryTextColor()
                }
        );
        mNavigationView.setItemIconTintList(colorStateList);
        mNavigationView.setItemTextColor(colorStateList);
        mNavMenu = mNavigationView.getMenu();
    }

    private void goToDefaultPage() {
        switchToAlbum();
    }

    private void setUpTabs() {
        mTabs.setTabMode(Options.usingScrollableTabs() ? TabLayout.MODE_SCROLLABLE : TabLayout.MODE_FIXED);

        if (!Options.usingTabs()) {
            mPager.lock();
            mTabs.setVisibility(View.GONE);
        }

        TabLayout.Tab songsTab, albumsTab, playlistsTab, artistsTab;
        ColorStateList tabColors = new ColorStateList(new int[][]{
                {android.R.attr.state_selected}, //When selected
                {}
        }, new int[]{
                getPrimaryTextColor(),
                getSecondaryTextColor()
        });

        if (!Options.usingIconTabs()) {
            songsTab = mTabs.newTab().setText(R.string.page_songs);
            albumsTab = mTabs.newTab().setText(R.string.page_albums);
            artistsTab = mTabs.newTab().setText(R.string.page_artists);
            playlistsTab = mTabs.newTab().setText(R.string.page_playlists);
        } else {
            Drawable songs = DrawableCompat.wrap(getResources().getDrawable(R.drawable.ic_audiotrack_24dp));
            Drawable albums = DrawableCompat.wrap(getResources().getDrawable(R.drawable.ic_album_24dp));
            Drawable artists = DrawableCompat.wrap(getResources().getDrawable(R.drawable.ic_artist_24dp));
            Drawable playlists = DrawableCompat.wrap(getResources().getDrawable(R.drawable.ic_queue_music_black_24dp));

            DrawableCompat.setTintList(songs, tabColors);
            DrawableCompat.setTintList(albums, tabColors);
            DrawableCompat.setTintList(artists, tabColors);
            DrawableCompat.setTintList(playlists, tabColors);

            songsTab = mTabs.newTab().setIcon(songs);
            albumsTab = mTabs.newTab().setIcon(albums);
            artistsTab = mTabs.newTab().setIcon(artists);
            playlistsTab = mTabs.newTab().setIcon(playlists);
        }

        mTabs.addTab(songsTab);
        mTabs.addTab(albumsTab);
        mTabs.addTab(artistsTab);
        mTabs.addTab(playlistsTab);

        //Allows the tabs to sync to the view pager
        mTabs.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mPager));
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));

        mTabs.setSelectedTabIndicatorColor(getPrimaryTextColor());
    }

    @Override
    protected boolean shouldKeepAppBarShadow() {
        return mTabs.getVisibility() == View.VISIBLE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, Settings.class));
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSearchRequested() {
        startActivity(new Intent(this, SearchActivity.class));
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
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

    //This section is where you select which view to see. Only views with back arrows should be set as separate activities.
    //Add code to this section as necessary (For example:If you need to update the list of songs in 'switchToSongs' you can add updateSongList(), or if you add a extra view add it to all sections

    public void switchToSongs() {
        mScreenName = getResources().getString(R.string.page_songs);
        mNavMenu.findItem(R.id.navdrawer_songs).setChecked(true);
        mTabs.getTabAt(0).select();
        mPager.setCurrentItem(0);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            mToolbar.setTitle(mScreenName);
            configureTaskDescription(0, mScreenName);
        }
        //Closes the Navdrawer
        mDrawerLayout.closeDrawers();
    }

    public void switchToAlbum() {
        mScreenName = getResources().getString(R.string.page_albums);
        mNavMenu.findItem(R.id.navdrawer_album_icon).setChecked(true);
        mTabs.getTabAt(1).select();
        mPager.setCurrentItem(1);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            mToolbar.setTitle(mScreenName);
            configureTaskDescription(0, mScreenName);
        }
        //Closes the Navdrawer
        mDrawerLayout.closeDrawers();
    }

    public void switchToArtists() {
        //Sets the current screen
        mScreenName = getResources().getString(R.string.page_artists);
        mNavMenu.findItem(R.id.navdrawer_artists).setChecked(true);
        mTabs.getTabAt(2).select();
        mPager.setCurrentItem(2);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            mToolbar.setTitle(mScreenName);
            configureTaskDescription(0, mScreenName);
        }
        //Closes the Navdrawer
        mDrawerLayout.closeDrawers();
    }

    public void switchToPlaylists() {
        //Sets the current screen
        mScreenName = getResources().getString(R.string.page_playlists);
        mNavMenu.findItem(R.id.navdrawer_playlists).setChecked(true);
        mTabs.getTabAt(3).select();
        mPager.setCurrentItem(3);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            mToolbar.setTitle(mScreenName);
            configureTaskDescription(0, mScreenName);
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
            scroller.setRecyclerView(recycler);
            scroller.setTouchTargetWidth(RecyclerFastScrollerUtils.convertDpToPx(MainScreen.this, 16));
            scroller.setHandlePressedColor(getAccentColor());
        }

        private void configureAsSongs(RecyclerView list) {
            list.setAdapter(new ListAdapter(ListAdapter.TYPE_SONG, Library.getSongs(), MainScreen.this));
            list.setItemAnimator(new DefaultItemAnimator());
            list.setLayoutManager(new LinearLayoutManager(MainScreen.this, LinearLayoutManager.VERTICAL, false));
        }

        private void configureAsAlbums(RecyclerView list) {
            ListAdapter adapter = new ListAdapter(ListAdapter.TYPE_ALBUM, Library.getAlbums(), MainScreen.this);
            adapter.setTransitionToAlbumDetails(MainScreen.this, mToolbar, findViewById(R.id.my_library_to_albumdetails_list_space));
            list.setAdapter(adapter);
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
            list.setAdapter(new ListAdapter(ListAdapter.TYPE_PLAYLIST, Library.getPlaylists(), MainScreen.this));
            list.setItemAnimator(new DefaultItemAnimator());
            list.setLayoutManager(new LinearLayoutManager(MainScreen.this, LinearLayoutManager.VERTICAL, false));
        }

        private void configureAsGenres(RecyclerView list) {
            list.setAdapter(new ListAdapter(ListAdapter.TYPE_GENRE, Library.getGenres(), MainScreen.this));
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
                list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        ColorUtil.setEdgeGlowColor(recyclerView, getPrimaryColor());
                    }
                });
                return root;
            } else {
                TextView text = new TextView(MainScreen.this);
                text.setSingleLine();
                text.setText(titles[position]);
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
