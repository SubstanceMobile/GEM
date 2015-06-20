package com.animbus.music.activities;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.animbus.music.R;
import com.animbus.music.data.SettingsManager;


public class albums_activity extends AppCompatActivity {

    public Bundle b;
    public Palette palette;
    SettingsManager settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        //Get intent data
        b = getIntent().getExtras();
        settings = new SettingsManager(this);
        Bitmap AlbumArt = BitmapFactory.decodeResource(getResources(), b.getInt("ALBUM_ART"));
        ImageView albumArtView = (ImageView) findViewById(R.id.backdrop);
        FloatingActionButton playAll = (FloatingActionButton) findViewById(R.id.FAB);
        //Toolbar, setting toolbar as Actionbar,Setting the back arrow to be shown, and setting the NavdrawerItemTitle to nothing
        /*android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.album_toolbar);
        android.support.v7.widget.Toolbar infoToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.album_info_toolbar);*/
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Sets the NavdrawerItemTitle to the intent's data
        /*infoToolbar.setSongTitle(b.getString("ALBUM_NAME"));
        infoToolbar.setSubtitle(b.getString("ALBUM_ARTIST"));*/
        //Sets the albumart
        albumArtView.setImageResource(b.getInt("ALBUM_ART"));
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(b.getString("ALBUM_NAME"));
        //Palette and setting colors
        palette = Palette.from(AlbumArt).generate();
        Palette.Swatch swatch = palette.getVibrantSwatch();
        Palette.Swatch swatchAccent = palette.getLightVibrantSwatch();
        if (swatch != null) {
           /* infoToolbar.setBackgroundColor(swatch.getRgb());
            infoToolbar.setTitleTextColor(swatch.getTitleTextColor());
            infoToolbar.setSubtitleTextColor(swatch.getBodyTextColor());*/
            collapsingToolbar.setContentScrimColor(swatch.getRgb());
            collapsingToolbar.setStatusBarScrimColor(swatch.getRgb());
            collapsingToolbar.setExpandedTitleColor(swatch.getTitleTextColor());
            collapsingToolbar.setCollapsedTitleTextColor(swatch.getTitleTextColor());

        } else {
           /* infoToolbar.setBackgroundColor(getResources().getColor(R.color.primaryLight));
            infoToolbar.setTitleTextColor(getResources().getColor(R.color.primary_text_default_material_light));
            infoToolbar.setSubtitleTextColor(getResources().getColor(R.color.secondary_text_default_material_light));*/
            collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.primaryLight));
            collapsingToolbar.setStatusBarScrimColor(getResources().getColor(R.color.primaryLight));
            collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.primaryDark));
            collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.primaryDark));
        }
        if (swatchAccent != null) {
            playAll.getBackground().setColorFilter(swatchAccent.getRgb(), PorterDuff.Mode.SRC_ATOP);
            playAll.getDrawable().setColorFilter(swatchAccent.getTitleTextColor(), PorterDuff.Mode.SRC_ATOP);
        } else {
            playAll.getBackground().setColorFilter(getResources().getColor(R.color.primaryDark), PorterDuff.Mode.SRC_ATOP);
            playAll.getDrawable().setColorFilter(getResources().getColor(R.color.primaryLight), PorterDuff.Mode.SRC_ATOP);
        }
        //Sets Window description in Multitasking menu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_new_light);
                setTaskDescription(new ActivityManager.TaskDescription(null, bm, getResources().getColor(R.color.primaryDark)));
                bm.recycle();
            } else {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_new_dark);
                setTaskDescription(new ActivityManager.TaskDescription(null, bm, getResources().getColor(R.color.primaryLight)));
                bm.recycle();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_albums_activity, menu);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
