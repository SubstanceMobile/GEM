package com.animbus.music.ui.NowPlaying;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.animbus.music.media.Old.MediaController;
import com.animbus.music.R;
import com.animbus.music.ThemeManager;
import com.animbus.music.SettingsManager;
import com.animbus.music.data.objects.Song;

import java.util.List;


public class NowPlaying extends AppCompatActivity implements MediaController.OnUpdateListener {
    SettingsManager settings;
    ThemeManager themeManager;
    MediaController mediaController;
    Boolean repeat = false;
    ImageButton shuffleButton, togglePlayButton, repeatButton;
    TextView songTitle, songArtisrt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new SettingsManager(this);
        themeManager = new ThemeManager(this, ThemeManager.TYPE_NORMAL);
        //Themeing
        setTheme(themeManager.getCurrentTheme());
        setContentView(R.layout.activity_now_playing);
        findViewById(R.id.now_playing_root_view).setBackgroundColor(themeManager.getCurrentBackgroundColor());
        findViewById(R.id.now_playing_controls_containter_root_view).setBackgroundColor(themeManager.getCurrentGreyColor());

        //Toolbar, setting toolbar as Actionbar,Setting the back arrow to be shown, and setting the NavdrawerItemTitle to nothing
        Toolbar toolbar = (Toolbar) findViewById(R.id.now_playing_appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(settings.getExitIcon());
        } else {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(settings.getExitIcon());
        }
        toolbar.setTitle("");

        shuffleButton = (ImageButton) findViewById(R.id.now_playing_shuffle_button);
        togglePlayButton = (ImageButton) findViewById(R.id.now_playing_play_button);
        repeatButton = (ImageButton) findViewById(R.id.now_playing_repeat_button);

        songTitle = (TextView) findViewById(R.id.now_playing_song_title);
        songArtisrt = (TextView) findViewById(R.id.now_playing_song_artist);

        mediaController = MediaController.getInstance();
        mediaController.setOnUpdateListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaController.requestUpdate();
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
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void playButtonClicked(View v) {
        mediaController.togglePlayback();
        mediaController.requestUpdate();
    }

    public void nextClicked(View v) {
        mediaController.playNextSong();
        mediaController.requestUpdate();
    }

    public void prevClicked(View v) {
        mediaController.playPrevSong();
        mediaController.requestUpdate();
    }

    public void repeatclicked(View v) {
        if (repeat) {
          repeat = false;
        } else {
            repeat = true;
        }
        mediaController.setRepeat(repeat);
        mediaController.requestUpdate();
    }

    @Override
    public void onUpdate(Song currentSong, Boolean isPaused, Boolean isRepeating, Boolean isShuffled, List<Song> currentQueue) {

        //Title and artist text
        songTitle.setText(currentSong.getSongTitle());
        songArtisrt.setText(currentSong.getSongArtist());

        //Repeat button icon
        if (isRepeating) {
            repeatButton.setImageResource(R.drawable.ic_repeat_one_black_48dp);
        } else {
            repeatButton.setImageResource(R.drawable.ic_repeat_black_48dp);
        }

        //Play Button icon
        if (!isPaused) {
            togglePlayButton.setImageResource(R.drawable.ic_pause_black_48dp);
        } else {
            togglePlayButton.setImageResource(R.drawable.ic_play_arrow_black_48dp);
        }

    }
}
