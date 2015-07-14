package com.animbus.music.activities;

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
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;

import com.animbus.music.MediaController;
import com.animbus.music.R;
import com.animbus.music.ThemeManager;
import com.animbus.music.data.DataManager;
import com.animbus.music.data.SettingsManager;
import com.animbus.music.data.adapter.AlbumGridAdapter;
import com.animbus.music.data.adapter.SongListAdapter;
import com.animbus.music.data.objects.Album;
import com.animbus.music.data.objects.Song;

import java.util.List;


public class MyLibrary extends AppCompatActivity implements AlbumGridAdapter.AlbumArtGridClickListener, NavigationView.OnNavigationItemSelectedListener,
        SongListAdapter.SongListItemClickListener {
    public RecyclerView mainList;
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
    View quickToolbar;
    Menu navMenu;

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
        mainList = (RecyclerView) findViewById(R.id.MyLibraryMainListLayout);
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
            navMenu.findItem(R.id.navdrawer_album_icon).setChecked(true);
        } else if (setting == 2) {
            switchToSongs();
            navMenu.findItem(R.id.navdrawer_songs).setChecked(true);
        } else if (setting == 3) {
            switchToArtists();
            navMenu.findItem(R.id.navdrawer_artists).setChecked(true);
        } else if (setting == 4) {
            switchToPlaylists();
            navMenu.findItem(R.id.navdrawer_playlists).setChecked(true);
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
                            getResources().getColor(R.color.accent_material_dark ), //When selected
                                    getResources().getColor(R.color.secondary_text_default_material_dark)
                    }
            );
        }
        drawerContent.setItemIconTintList(colorStateList);
        drawerContent.setItemTextColor(colorStateList);

    }


    @Override
    public void AlbumGridItemClicked(View view, int position, List<Album> data) {
        Snackbar.make(findViewById(R.id.MainView), "Removed for renovation", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dismiss
            }
        }).show();
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
                Intent intent = new Intent();
                intent.setClass(this, Settings.class);
                startActivity(intent);
                break;
        }
        menuItem.setChecked(true);
        return true;
    }

    @Override
    public void SongListItemClicked(int position, List<Song> data) {
        musicControl.startPlayback(data, position);

        //TODO: Set a listener
        quickToolbar.setVisibility(View.VISIBLE);
        quickToolbar.setTranslationY(200.0f);
        quickToolbar.animate().translationY(0).start();
    }

    //This section is where you select which view to see. Only views with back arrows should be set as separate activities.
    //Add code to this section as necessary (For example:If you need to update the list of songs in 'switchToSongs' you can add updateSongList(), or if you add a extra view add it to all sections)
    public void switchToAlbum() {
        //Configures the Recyclerview
        AlbumGridAdapter adapter = new AlbumGridAdapter(this, dataManager.getAlbumGridData());
        adapter.setOnItemClickedListener(this);
        mainList.setAdapter(adapter);
        mainList.setItemAnimator(new DefaultItemAnimator());
        config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mainList.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        } else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mainList.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        }
        currentScreenName = getResources().getString(R.string.page_albums);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            toolbar.setTitle(currentScreenName);
        }
        //Closes the Navdrawer
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawers();
    }

    public void switchToSongs() {
        //Configures the Recyclerview
        SongListAdapter adapter = new SongListAdapter(this, dataManager.getSongListData());
        adapter.setOnItemClickedListener(this);
        mainList.setAdapter(adapter);
        mainList.setItemAnimator(new DefaultItemAnimator());
        mainList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        currentScreenName = getResources().getString(R.string.page_songs);
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
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            toolbar.setTitle(currentScreenName);
        }
        //Closes the Navdrawer
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawers();
    }
    //End Section


    //This section is for the helper methods

    //End section


    //This section is where you can open other screens (Now Playing, Albums Details, Playlist Details, etc.)
    //You add the Bundle and Intent for the alternate activities

    //End Section
}
