package com.animbus.music.ui.MainScreen;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.animbus.music.media.Old.MediaController;
import com.animbus.music.R;
import com.animbus.music.ThemeManager;
import com.animbus.music.ui.Settings.Settings;
import com.animbus.music.CustomViews.LockableViewPager;
import com.animbus.music.data.DataManager;
import com.animbus.music.SettingsManager;


public class MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    MediaController controller;
    String AlbumName, AlbumArtist, currentScreenName;
    int AlbumArt = R.drawable.album_art;
    MediaController musicControl;
    Configuration config;
    DataManager dataManager;
    SettingsManager settings;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView drawerContent;
    ThemeManager themeManager;
    public View quickToolbar;
    Menu navMenu;
    LockableViewPager pager;
    TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = new SettingsManager(this);
        themeManager = new ThemeManager(this, ThemeManager.TYPE_NORMAL);

        setTheme(themeManager.getCurrentTheme());
        setContentView(R.layout.activity_main);
        findViewById(R.id.MainView).setBackgroundColor(themeManager.getCurrentBackgroundColor());

        //This sets all of the variables
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        musicControl = MediaController.getInstance();
        dataManager = new DataManager(this);
        drawerContent = (NavigationView) findViewById(R.id.navigation);
        quickToolbar = (View) findViewById(R.id.mylibrary_toolbar_fragment);

        drawerContent.setNavigationItemSelectedListener(this);
        musicControl = MediaController.getInstance();
        musicControl.setContext(this);
        musicControl.setQueue(dataManager.getSongListData());
        musicControl.setRepeat(false);

        quickToolbar.setVisibility(View.GONE);

        //Basic Stuff
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        updateSettings(this);
        setUpNavdrawer(drawerLayout, toolbar);
    }

    public void end() {
        finish();
    }

    private void updateSettings(Context cxt) {
        //Sets Dynamic Title
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            toolbar.setTitle(currentScreenName);
        } else {
            toolbar.setTitle(cxt.getResources().getString(R.string.title_activity_main));
        }

        pager = (LockableViewPager) findViewById(R.id.main_view_pager);
        pager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(3);

        tabs = (TabLayout) findViewById(R.id.main_tab_layout);
        tabs.setupWithViewPager(pager);

        AppBarLayout appBarBackground = (AppBarLayout) findViewById(R.id.main_app_bar);

        if(!settings.getBooleanSetting(SettingsManager.KEY_USE_TABS, false)){
            ViewCompat.setElevation(appBarBackground, 0.0f);
            pager.lock();
            tabs.setVisibility(View.GONE);
        }

        //Sets Window description in Multitasking menu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_new_light);
                setTaskDescription(new ActivityManager.TaskDescription(null, bm, cxt.getResources().getColor(R.color.primaryDark)));
                bm.recycle();
            } else {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_new_dark);
                setTaskDescription(new ActivityManager.TaskDescription(null, bm, cxt.getResources().getColor(R.color.primaryLight)));
                bm.recycle();
            }
        }
    }

    private void setUpNavdrawer(DrawerLayout drawerLayout, Toolbar toolbar) {
        final ActionBarDrawerToggle mDrawerToggle;
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        View header = View.inflate(this, R.layout.navigation_drawer_header, null);
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
                            getResources().getColor(R.color.accent_material_light), //When selected
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
                            getResources().getColor(R.color.accent_material_dark), //When selected
                            getResources().getColor(R.color.secondary_text_default_material_dark)
                    }
            );
        }
        drawerContent.setItemIconTintList(colorStateList);
        drawerContent.setItemTextColor(colorStateList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(this, Settings.class);
            startActivity(intent);
            return true;
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
            } else if(position == 2) {
                return PagePlaylists.setUp(MainScreen.this);
            } else if(position == 3){
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
    //End Section


    //This section is for the helper methods

    //End section


    //This section is where you can open other screens (Now Playing, Albums Details, Playlist Details, etc.)
    //You add the Bundle and Intent for the alternate activities

    //End Section
}
