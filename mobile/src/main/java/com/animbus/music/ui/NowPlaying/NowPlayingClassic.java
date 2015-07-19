package com.animbus.music.ui.NowPlaying;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.animbus.music.R;
import com.animbus.music.SettingsManager;
import com.animbus.music.ThemeManager;
import com.animbus.music.data.objects.Song;
import com.animbus.music.media.Old.MediaController;
import com.animbus.music.ui.Settings.Settings;

import java.util.List;

public class NowPlayingClassic extends AppCompatActivity implements MediaController.OnUpdateListener {
    SettingsManager settings;
    ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new SettingsManager(this);
        themeManager = new ThemeManager(this, ThemeManager.TYPE_NORMAL);
        //Themeing
        setTheme(themeManager.getCurrentTheme());
        setContentView(R.layout.activity_now_playing_classic);
        findViewById(R.id.now_playing_classic_root_view).setBackgroundColor(themeManager.getCurrentBackgroundColor());

        //Toolbar, setting toolbar as Actionbar,Setting the back arrow to be shown, and setting the NavdrawerItemTitle to nothing
        Toolbar toolbar = (Toolbar) findViewById(R.id.now_playing_classic_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_exit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_now_playing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, Settings.class));
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdate(Song currentSong, Boolean isPaused, Boolean isRepeating, Boolean isShuffled, List<Song> currentQueue) {

    }
}
