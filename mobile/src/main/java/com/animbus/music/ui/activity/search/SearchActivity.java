package com.animbus.music.ui.activity.search;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.animbus.music.R;
import com.animbus.music.ui.custom.activity.ThemableActivity;
import com.animbus.music.ui.list.ListAdapter;
import com.animbus.music.media.Library;
import com.animbus.music.media.objects.Album;
import com.animbus.music.media.objects.Playlist;
import com.animbus.music.media.objects.Song;
import com.animbus.music.ui.activity.theme.Theme;

import java.util.List;

public class SearchActivity extends ThemableActivity {
    Toolbar mToolbar;
    SearchView mSearchView;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void setVariables() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected void setUp() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Search
        handleIntent(getIntent());
    }

    @Override
    protected void setUpTheme(Theme theme) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                SearchActivity.this.supportFinishAfterTransition();
                return false;
            }
        });
        menu.findItem(R.id.action_search).expandActionView();

        //Search
        final SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryRefinementEnabled(true);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                String suggestion = getSuggestion(position);
                searchView.setQuery(suggestion, true); // submit query now
                return true; // replace default search manager behaviour
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });
        this.mSearchView = searchView;
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction()) && intent.hasExtra(SearchManager.QUERY)) {
            search(intent.getStringExtra(SearchManager.QUERY));
        } else {
            search("");
        }
    }

    private void search(String query) {

        //Resets
        findViewById(R.id.search_empty_textview).setVisibility(View.GONE);
        findViewById(R.id.search_category_albums).setVisibility(View.GONE);
        findViewById(R.id.search_category_songs).setVisibility(View.GONE);
        findViewById(R.id.search_category_playlists).setVisibility(View.GONE);

        //Stops if the query is empty
        if (TextUtils.isEmpty(query)) return;

        //Fetches results
        List<Album> albums = Library.filterAlbums(query);
        List<Song> songs = Library.filterSongs(query);
        List<Playlist> playlists = Library.filterPlaylists(query);

        //Displays the no results page
        if (albums.isEmpty() && songs.isEmpty() && playlists.isEmpty()) {
            findViewById(R.id.search_empty_textview).setVisibility(View.VISIBLE);
            return;
        }

        //Sets the sections that have results to be visible and configures them
        if (!albums.isEmpty()) {
            findViewById(R.id.search_category_albums).setVisibility(View.VISIBLE);

            RecyclerView albumsRecycler = (RecyclerView) findViewById(R.id.search_albums_results);
            albumsRecycler.setAdapter(new ListAdapter(ListAdapter.TYPE_ALBUM, albums, this));
            albumsRecycler.setItemAnimator(new DefaultItemAnimator());
            albumsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        }
        if (!songs.isEmpty()) {
            findViewById(R.id.search_category_songs).setVisibility(View.VISIBLE);
        }
        if (!playlists.isEmpty()) {
            findViewById(R.id.search_category_playlists).setVisibility(View.VISIBLE);
        }
    }

    private String getSuggestion(int position) {
        Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(
                position);
        return cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
    }

}
