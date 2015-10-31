package com.animbus.music.ui.mainScreen;

import android.animation.Animator;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.media.session.MediaControllerCompat;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.animbus.music.R;
import com.animbus.music.SettingsManager;
import com.animbus.music.ui.IssueReportingActivity;
import com.animbus.music.customImpls.LockableViewPager;
import com.animbus.music.customImpls.ThemableActivity;
import com.animbus.music.data.VariablesSingleton;
import com.animbus.music.data.adapter.AlbumGridAdapter;
import com.animbus.music.data.list.ListAdapter;
import com.animbus.music.data.list.ListAdapter.SongListener;
import com.animbus.music.media.Library;
import com.animbus.music.media.PlaybackManager;
import com.animbus.music.media.ServiceHelper;
import com.animbus.music.media.objects.Album;
import com.animbus.music.media.objects.Song;
import com.animbus.music.ui.albumDetails.AlbumDetails;
import com.animbus.music.ui.nowPlaying.NowPlaying;
import com.animbus.music.ui.settings.Settings;
import com.animbus.music.ui.settings.chooseIcon.IconManager;
import com.animbus.music.ui.setup.SetupActivity;
import com.animbus.music.ui.theme.Theme;
import com.animbus.music.ui.theme.ThemeManager;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import com.pluscubed.recyclerfastscroll.RecyclerFastScrollerUtils;

import java.util.List;


public class MainScreen extends ThemableActivity implements NavigationView.OnNavigationItemSelectedListener {
    View quickToolbar;
    String currentScreenName;
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
        /*showSetupIfNeeded();*/
    }

    @Override
    protected void setVariables() {
        settings = SettingsManager.get();
        themeManager = ThemeManager.get();
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerContent = (NavigationView) findViewById(R.id.navigation);
        quickToolbar = findViewById(R.id.main_screen_now_playing_toolbar);
        tabs = (TabLayout) findViewById(R.id.main_tab_layout);
        pager = (LockableViewPager) findViewById(R.id.main_view_pager);

        VariablesSingleton.get().settingsMyLib = this;
    }

    @Override
    protected void setUpTheme(Theme theme) {
        Drawable menu = getResources().getDrawable(R.drawable.ic_menu_24dp);
        DrawableCompat.setTint(menu, getResources().getColor(!ThemeManager.get().useLightTheme ? R.color.primaryLight : R.color.primaryDark));
        getSupportActionBar().setHomeAsUpIndicator(menu);
        setUpNavdrawer();
    }

    @Override
    protected void setUp() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Sets Dynamic Title
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            toolbar.setTitle(currentScreenName);
        } else {
            toolbar.setTitle(getResources().getString(R.string.title_activity_main));
        }

        pager.setAdapter(new RecyclerPagerAdapter());
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
            ColorStateList state = new ColorStateList(new int[][]{
                    {android.R.attr.state_selected}, //When selected
                    {}
            }, new int[]{
                    getThemeAccentColor(),
                    ThemeManager.get().useLightTheme ? getResources().getColor(R.color.secondary_text_default_material_light) : getResources().getColor(R.color.secondary_text_default_material_dark)
            });
            Drawable albums = getResources().getDrawable(R.drawable.ic_album_24dp);
            Drawable songs = getResources().getDrawable(R.drawable.ic_audiotrack_24dp);
            Drawable playlists = getResources().getDrawable(R.drawable.ic_queue_music_black_24dp);
            Drawable artists = getResources().getDrawable(R.drawable.ic_artist_24dp);
            DrawableCompat.setTintList(albums, state);
            DrawableCompat.setTintList(songs, state);
            DrawableCompat.setTintList(playlists, state);
            DrawableCompat.setTintList(artists, state);
            tabs.addTab(tabs.newTab().setIcon(albums));
            tabs.addTab(tabs.newTab().setIcon(songs));
            tabs.addTab(tabs.newTab().setIcon(playlists));
            tabs.addTab(tabs.newTab().setIcon(artists));
        }

        //Allows the tabs to sync to the view pager
        tabs.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        configureNowPlayingBar();
        configureWindow();
    }

    private void configureNowPlayingBar() {
        if (!PlaybackManager.get().isActive()) {
            quickToolbar.setVisibility(View.GONE);
        } else {
            try {
                setUpNowPlayingBarWithSong(PlaybackManager.get().getCurrentSong());
                setUpNowPlayingBarWithState(ServiceHelper.get(this).getService().getStateObj());
            } catch (Exception ignored) {
            }
        }
        PlaybackManager.get().registerListener(new PlaybackManager.OnChangedListener() {
            @Override
            public void onSongChanged(Song song) {
                setUpNowPlayingBarWithSong(song);
            }

            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                setUpNowPlayingBarWithState(state);
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
        final MediaControllerCompat.TransportControls controls = ServiceHelper.get(this).getService().getSession().getController().getTransportControls();
        ImageButton button = (ImageButton) findViewById(R.id.main_screen_now_playing_toolbar_playpause);
        boolean isPaused = state.getState() == PlaybackStateCompat.STATE_PAUSED;
        if (isPaused) {
            button.setImageResource(R.drawable.ic_play_arrow_white_48dp);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    controls.play();
                }
            });
        } else {
            button.setImageResource(R.drawable.ic_pause_white_48dp);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    controls.pause();
                }
            });
        }
    }

    private void configureWindow() {
        //Sets Window description in Multitasking menu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IconManager iconM = IconManager.get().setContext(this);
            Bitmap bm = BitmapFactory.decodeResource(getResources(), iconM.getDrawable(iconM.getOverviewIcon(iconM.getIcon()).getId()));
            setTaskDescription(new ActivityManager.TaskDescription(currentScreenName, bm, ThemeManager.get().useLightTheme ? getResources().getColor(R.color.primaryLight) : getResources().getColor(R.color.primaryDark)));
            bm.recycle();
        }
    }

    public int getThemeAccentColor() {
        final TypedValue value = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        return value.data;
    }

    private void setUpNavdrawer() {
        drawerContent.setNavigationItemSelectedListener(this);

        final View header = View.inflate(this, R.layout.navigation_drawer_header, null);

        final TextView title = (TextView) header.findViewById(R.id.navdrawer_header_title);
        final TextView artist = (TextView) header.findViewById(R.id.navdrawer_header_artist);
        final ImageView art = (ImageView) header.findViewById(R.id.navdrawer_header_image);

        try {
            Song current = PlaybackManager.get().getCurrentSong();
            title.setText(current.getSongTitle());
            artist.setText(current.getSongArtist());
            current.getAlbum().requestArt(art);
        } catch (NullPointerException e) {
            Log.d("Navdrawer Header", "Not playing music");
        }

        PlaybackManager.get().registerListener(new PlaybackManager.OnChangedListener() {
            @Override
            public void onSongChanged(Song song) {
                title.setText(song.getSongTitle());
                artist.setText(song.getSongArtist());
                song.getAlbum().requestArt(art);
            }

            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {

            }
        });

        header.findViewById(R.id.navdrawer_header_clickable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                header.findViewById(R.id.navdrawer_header_items_root).animate().alpha(0.0f).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainScreen.this, header.findViewById(R.id.navdrawer_header_image), "art");
                        ActivityCompat.startActivity(MainScreen.this, new Intent(MainScreen.this, NowPlaying.class), options.toBundle());
                        header.findViewById(R.id.navdrawer_header_items_root).setAlpha(1.0f);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }
        });

        drawerContent.addHeaderView(header);
        drawerContent.inflateMenu(R.menu.navigation_drawer_items);

        //This sets up the RecyclerView to the default screen based on ` setting.
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
        int secondaryColor = ThemeManager.get().useLightTheme ? getResources().getColor(R.color.secondary_text_default_material_light) : getResources().getColor(R.color.secondary_text_default_material_dark);
        colorStateList = new ColorStateList(
                new int[][]{
                        {android.R.attr.state_checked}, //When selected
                        {}
                },
                new int[]{
                        getThemeAccentColor(), //When selected
                        secondaryColor
                }
        );
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
                /*startActivity(new Intent(this, Search.class));*/
                Snackbar.make(findViewById(R.id.MainView), R.string.msg_coming_soon, Snackbar.LENGTH_SHORT).show();
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
                menuItem.setChecked(true);
                break;
            case R.id.navdrawer_songs:
                switchToSongs();
                menuItem.setChecked(true);
                break;
            case R.id.navdrawer_playlists:
                switchToPlaylists();
                menuItem.setChecked(true);
                break;
            case R.id.navdrawer_artists:
                switchToArtists();
                menuItem.setChecked(true);
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
    //Add code to this section as necessary (For example:If you need to update the list of songs in 'switchToSongs' you can add updateSongList(), or if you add a extra view add it to all sections)
    public void switchToAlbum() {
        currentScreenName = getResources().getString(R.string.page_albums);
        navMenu.findItem(R.id.navdrawer_album_icon).setChecked(true);
        tabs.getTabAt(0).select();
        pager.setCurrentItem(0);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_CATEGORY_NAMES_ON_MAIN_SCREEN, false)) {
            toolbar.setTitle(currentScreenName);
            configureWindow();
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
            configureWindow();
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
            configureWindow();
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
            configureWindow();
        }
        //Closes the Navdrawer
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawers();
    }

    class RecyclerPagerAdapter extends PagerAdapter implements AlbumGridAdapter.AlbumArtGridClickListener {
        private String[] titles = new String[]{
                getResources().getString(R.string.page_albums),
                getResources().getString(R.string.page_songs),
                getResources().getString(R.string.page_playlists),
                getResources().getString(R.string.page_artists)
        };

        @Override
        public int getCount() {
            return titles.length;
        }

        private void configureRecycler(RecyclerView recycler, RecyclerFastScroller scroller, int pos) {
            switch (pos) {
                case 0:
                    configureAsAlbums(recycler);
                    break;
                case 1:
                    configureAsSongs(recycler);
                    break;
                case 2:
                    configureAsPlaylists(recycler);
                    break;
                case 3:
                    configureAsArtists(recycler);
                    break;
            }
            scroller.setRecyclerView(recycler);
            scroller.setTouchTargetWidth(RecyclerFastScrollerUtils.convertDpToPx(MainScreen.this, 16));
        }

        private void configureAsAlbums(RecyclerView list) {
            AlbumGridAdapter adapter = new AlbumGridAdapter(MainScreen.this, Library.getAlbums());
            adapter.setOnItemClickedListener(this);
            list.setAdapter(adapter);
            list.setItemAnimator(new DefaultItemAnimator());
            Configuration config = MainScreen.this.getResources().getConfiguration();
            if (config.orientation == Configuration.ORIENTATION_PORTRAIT)
                list.setLayoutManager(new GridLayoutManager(MainScreen.this, 2, GridLayoutManager.VERTICAL, false));
            else
                list.setLayoutManager(new GridLayoutManager(MainScreen.this, 3, GridLayoutManager.VERTICAL, false));
        }

        private void configureAsSongs(RecyclerView list) {
            ListAdapter adapter = new ListAdapter(ListAdapter.TYPE_SONG, Library.getSongs(), MainScreen.this);
            adapter.setListener(new SongListener() {
                @Override
                public void onClick(Song object, List<Song> data, int pos) {
                    PlaybackManager.get().play(data, pos);
                }

                @Override
                public boolean onLongClick(Song object, List<Song> data, int pos) {
                    return false;
                }
            });
            list.setAdapter(adapter);
            list.setItemAnimator(new DefaultItemAnimator());
            list.setLayoutManager(new LinearLayoutManager(MainScreen.this, LinearLayoutManager.VERTICAL, false));
        }

        private void configureAsPlaylists(RecyclerView list) {
            //TODO: Add this
        }

        private void configureAsArtists(RecyclerView list) {
            //TODO: Add this
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0 || position == 1) {
                View root = getLayoutInflater().inflate(R.layout.main_screen_page, container, false);
                RecyclerView list = (RecyclerView) root.findViewById(R.id.main_screen_page_recycler);
                RecyclerFastScroller scroller = (RecyclerFastScroller) root.findViewById(R.id.main_screen_page_scroller);
                configureRecycler(list, scroller, position);
                container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

        @Override
        public void AlbumGridItemClicked(View view, Album album) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainScreen.this,
                    new Pair<View, String>(MainScreen.this.toolbar, "appbar"),
                    new Pair<View, String>(MainScreen.this.toolbar, "appbar_text_protection"),
                    new Pair<View, String>(view.findViewById(R.id.AlbumArtGridItemAlbumArt), "art"),
                    new Pair<View, String>(MainScreen.this.findViewById(R.id.my_library_to_albumdetails_list_space), "list"),
                    new Pair<View, String>(view.findViewById(R.id.AlbumInfoToolbar), "info")
            );
            ActivityCompat.startActivity(MainScreen.this, new Intent(MainScreen.this, AlbumDetails.class).putExtra("album_id", album.getId()), options.toBundle());
        }

        @Override
        public void AlbumGridItemLongClicked(View view, Album album) {
            Snackbar.make(MainScreen.this.findViewById(R.id.MainView), R.string.playing_album, Snackbar.LENGTH_SHORT).show();
            PlaybackManager.get().play(album.getSongs(), 0);
        }
    }
}
