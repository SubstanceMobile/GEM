package com.animbus.music.ui.mainScreen;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.animbus.music.R;
import com.animbus.music.SettingsManager;
import com.animbus.music.customImpls.LockableViewPager;
import com.animbus.music.customImpls.ThemableActivity;
import com.animbus.music.media.MediaData;
import com.animbus.music.media.PlaybackManager;
import com.animbus.music.media.objects.Song;
import com.animbus.music.ui.Search;
import com.animbus.music.ui.settings.Settings;
import com.animbus.music.ui.settings.chooseIcon.IconManager;
import com.animbus.music.ui.setup.SetupActivity;
import com.animbus.music.ui.theme.Theme;
import com.animbus.music.ui.theme.ThemeManager;


public class MainScreen extends ThemableActivity implements NavigationView.OnNavigationItemSelectedListener {
    public View quickToolbar;
    String currentScreenName;
    int AlbumArt = R.drawable.album_art;
    MediaData dataManager;
    SettingsManager settings;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView drawerContent;
    ThemeManager themeManager;
    Menu navMenu;
    LockableViewPager pager;
    TabLayout tabs;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void sequence(Bundle savedInstanceState) {
        super.sequence(savedInstanceState);
        setUpNavdrawer();
        showSetupIfNeeded();
    }

    @Override
    protected void setVariables() {
        settings = SettingsManager.get();
        themeManager = ThemeManager.get();
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerContent = (NavigationView) findViewById(R.id.navigation);
        quickToolbar = findViewById(R.id.mylibrary_toolbar_fragment);
        tabs = (TabLayout) findViewById(R.id.main_tab_layout);
        pager = (LockableViewPager) findViewById(R.id.main_view_pager);

        BackupHub.get().settingsMyLib = this;
    }

    @Override
    protected void setUpTheme(Theme theme) {

    }

    @Override
    protected void setUp() {
       /* if (!PlaybackManager.get().isActive()){
            quickToolbar.setVisibility(View.GONE);
        }*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_exit_light);
        } else {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //Sets Dynamic Title
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            toolbar.setTitle(currentScreenName);
        } else {
            toolbar.setTitle(getResources().getString(R.string.title_activity_main));
        }

        pager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(3);

        if (settings.getBooleanSetting(SettingsManager.KEY_SCROLLABLE_TABS, true)) {
            tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tabs.setTabMode(TabLayout.MODE_FIXED);
        }

        if (!settings.getBooleanSetting(SettingsManager.KEY_USE_TABS, false)) {
            ViewCompat.setElevation(findViewById(R.id.main_app_bar), 0.0f);
            pager.lock();
            tabs.setVisibility(View.GONE);
        }

        if (!settings.getBooleanSetting(SettingsManager.KEY_USE_TAB_ICONS, false)) {
            tabs.setupWithViewPager(pager);
        } else {
            tabs.addTab(tabs.newTab().setIcon(R.drawable.ic_tab_albums));
            tabs.addTab(tabs.newTab().setIcon(R.drawable.ic_tab_songs));
            tabs.addTab(tabs.newTab().setIcon(R.drawable.ic_tab_playlists));
            tabs.addTab(tabs.newTab().setIcon(R.drawable.ic_tab_artists));
        }

        //Allows the tabs to sync to the view pager
        tabs.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));


        //Sets Window description in Multitasking menu
        IconManager iconM = IconManager.get().setContext(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), iconM.getDrawable(iconM.getOverviewIcon(iconM.getIcon()).getId()));
                setTaskDescription(new ActivityManager.TaskDescription(null, bm, getResources().getColor(R.color.primaryDark)));
                bm.recycle();
            } else {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), iconM.getDrawable(iconM.getOverviewIcon(iconM.getIcon()).getId()));
                setTaskDescription(new ActivityManager.TaskDescription(null, bm, getResources().getColor(R.color.primaryLight)));
                bm.recycle();
            }
        }
        if (!PlaybackManager.get().isActive()) {
            quickToolbar.setVisibility(View.GONE);
        }
    }

    private void setUpNavdrawer() {
        drawerContent.setNavigationItemSelectedListener(this);

        final View header = View.inflate(this, R.layout.navigation_drawer_header, null);
        PlaybackManager.get().registerListener(new PlaybackManager.OnChangedListener() {
            @Override
            public void onSongChanged(Song song) {
                TextView title = (TextView) header.findViewById(R.id.navdrawer_header_title);
                TextView artist = (TextView) header.findViewById(R.id.navdrawer_header_artist);
                ImageView art = (ImageView) header.findViewById(R.id.navdrawer_header_image);
                title.setText(song.getSongTitle());
                artist.setText(song.getSongArtist());
                art.setImageBitmap(song.getAlbum().getAlbumArt());
            }

            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {

            }
        });
        drawerContent.addHeaderView(header);
        drawerContent.inflateMenu(R.menu.navigation_drawer_items);

        //This sets up the RecyclerView to the default screen based on a setting.
        Integer setting = settings.getIntegerSetting(SettingsManager.KEY_DEFAULT_SCREEN, SettingsManager.SCREEN_ALBUMS);
        navMenu = drawerContent.getMenu();
        if (setting == 0) {
            Toast.makeText(this, "What?", Toast.LENGTH_LONG).show();
            switchToAlbum();
            navMenu.findItem(R.id.navdrawer_album_icon).setChecked(true);
        } else if (setting == 1) {
            switchToAlbum();
        } else if (setting == 2) {
            switchToSongs();
        } else if (setting == 3) {
            switchToArtists();
        } else if (setting == 4) {
            switchToPlaylists();
        }
        ColorStateList colorStateList;
        if (themeManager.useLightTheme) {
            colorStateList = new ColorStateList(
                    new int[][]{
                            {android.R.attr.state_checked}, //When selected
                            {}
                    },
                    new int[]{
                            getResources().getColor(R.color.primaryDark), //When selected
                            getResources().getColor(R.color.secondary_text_default_material_light)
                    }
            );
        } else {
            colorStateList = new ColorStateList(
                    new int[][]{
                            {android.R.attr.state_checked}, //When selected
                            {}
                    },
                    new int[]{
                            getResources().getColor(R.color.primaryLight), //When selected
                            getResources().getColor(R.color.secondary_text_default_material_dark)
                    }
            );
        }
        drawerContent.setItemIconTintList(colorStateList);
        drawerContent.setItemTextColor(colorStateList);

    }

    private void showSetupIfNeeded() {
        //TODO:Placeholder
        boolean isDesigned = false;
        boolean romAllows = true;
        if (!isDesigned && romAllows && settings.getBooleanSetting(SettingsManager.KEY_FIRST_RUN, true)) {
            startActivity(new Intent(this, SetupActivity.class));
            settings.setBooleanSetting(SettingsManager.KEY_FIRST_RUN, false);
        }
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
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_search:
                startActivity(new Intent(this, Search.class));/*
                new MediaNotification(this);
                startActivity(new Intent(this, SetupActivity.class));*/
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.navdrawer_album_icon:
                switchToAlbum();
                break;
            case R.id.navdrawer_songs:
                switchToSongs();
                break;
            case R.id.navdrawer_playlists:
                switchToPlaylists();
                break;
            case R.id.navdrawer_artists:
                switchToArtists();
                break;
            case R.id.navdrawer_settings:
                startActivity(new Intent(this, Settings.class));
                break;
        }
        menuItem.setChecked(true);
        return true;
    }

    //This section is where you select which view to see. Only views with back arrows should be set as separate activities.
    //Add code to this section as necessary (For example:If you need to update the list of songs in 'switchToSongs' you can add updateSongList(), or if you add a extra view add it to all sections)
    public void switchToAlbum() {
        currentScreenName = getResources().getString(R.string.page_albums);
        navMenu.findItem(R.id.navdrawer_album_icon).setChecked(true);
        tabs.getTabAt(0).select();
        pager.setCurrentItem(0);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            toolbar.setTitle(currentScreenName);
        }
        //Closes the Navdrawer
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawers();
    }

    public void switchToSongs() {
        currentScreenName = getResources().getString(R.string.page_songs);
        navMenu.findItem(R.id.navdrawer_songs).setChecked(true);
        tabs.getTabAt(1).select();
        pager.setCurrentItem(1);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            toolbar.setTitle(currentScreenName);
        }
        //Closes the Navdrawer
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawers();
    }

    public void switchToPlaylists() {
        //Sets the current screen
        currentScreenName = getResources().getString(R.string.page_playlists);
        navMenu.findItem(R.id.navdrawer_playlists).setChecked(true);
        tabs.getTabAt(2).select();
        pager.setCurrentItem(2);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            toolbar.setTitle(currentScreenName);
        }
        //Closes the Navdrawer
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawers();
    }

    public void switchToArtists() {
        //Sets the current screen
        currentScreenName = getResources().getString(R.string.page_artists);
        navMenu.findItem(R.id.navdrawer_artists).setChecked(true);
        tabs.getTabAt(3).select();
        pager.setCurrentItem(3);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            toolbar.setTitle(currentScreenName);
        }
        //Closes the Navdrawer
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawers();
    }

    class MainPagerAdapter extends FragmentStatePagerAdapter {
        int lockedPos;
        boolean isLocked;


        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private Fragment getListItem(final int position) {
            if (position == 0) {
                return PageAlbums.setUp(MainScreen.this);
            } else if (position == 1) {
                return PageSongs.setUp(MainScreen.this, MainScreen.this);
            } else if (position == 2) {
                return PagePlaylists.setUp(MainScreen.this);
            } else if (position == 3) {
                return PageArtists.setUp(MainScreen.this);
            } else {
                return null;
            }
        }

        @Override
        public Fragment getItem(int position) {
            return getListItem(position);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getResources().getString(R.string.page_albums);
            } else if (position == 1) {
                return getResources().getString(R.string.page_songs);
            } else if (position == 2) {
                return getResources().getString(R.string.page_playlists);
            } else if (position == 3) {
                return getResources().getString(R.string.page_artists);
            } else {
                return "";
            }
        }

    }
}
