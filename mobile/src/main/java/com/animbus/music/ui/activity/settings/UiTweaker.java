package com.animbus.music.ui.activity.settings;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.animbus.music.R;
import com.animbus.music.ui.custom.activity.ThemableActivity;
import com.animbus.music.ui.activity.theme.Theme;

public class UiTweaker extends ThemableActivity {
    public static final int TYPE_GENERAL = 0,
            TYPE_HOME = 1,
            TYPE_ALBUMS = 2,
            TYPE_NOWPLAYING = 3;

    Toolbar mAppBar;
    AppBarLayout mAppBarLayout;

    @Override
    protected void init(Bundle savedInstanceState) {
        if (getIntent() != null) {
            int type = getIntent().getIntExtra("ui_type", -1);
            if (type != -1) {
                switch (type) {
                    case TYPE_GENERAL:
                        break;
                    case TYPE_HOME:
                        break;
                    case TYPE_ALBUMS:
                        break;
                    case TYPE_NOWPLAYING:
                        break;
                    default:
                        break;
                }
                Toast.makeText(this, String.valueOf(type), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void setVariables() {
        mAppBar = (Toolbar) findViewById(R.id.ui_tweaker_app_bar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.ui_tweaker_app_bar_layout);
    }

    @Override
    protected void setUp(){
        setSupportActionBar(mAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void setUpTheme(Theme theme) {
        setAppBarColor(mAppBarLayout, theme.getColorPrimary());
    }


    private void loadSettings() {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ui_customiser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
