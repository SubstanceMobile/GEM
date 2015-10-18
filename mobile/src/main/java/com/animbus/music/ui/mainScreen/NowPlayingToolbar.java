package com.animbus.music.ui.mainScreen;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.animbus.music.R;
import com.animbus.music.media.PlaybackManager;
import com.animbus.music.media.objects.Song;

/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingToolbar extends Fragment implements PlaybackManager.OnChangedListener {
    Context cxt;
    TextView title, artist;
    ImageButton button;
    ImageView art;

    public NowPlayingToolbar() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        cxt = getActivity();
        return inflater.inflate(R.layout.fragment_now_playing_toolbar, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getView().findViewById(R.id.play_pause_toolbar_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PlaybackManager.get().isPlaying()) {
                    PlaybackManager.get().resume();
                } else {
                    PlaybackManager.get().pause();
                }
            }
        });
        getView().findViewById(R.id.now_playing_toolbar_root_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openNowPaying();
            }
        });
        PlaybackManager.get().registerListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        title = (TextView) getView().findViewById(R.id.song_toolbar_title);
        artist = (TextView) getView().findViewById(R.id.song_toolbar_artist);
        button = (ImageButton) getView().findViewById(R.id.play_pause_toolbar_button);
        art = (ImageView) getView().findViewById(R.id.nowplayingtoolbar_albumart);
    }

    public void openNowPaying() {
        Snackbar.make(getView(), R.string.msg_temp_removed, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onSongChanged(Song song) {
        title.setText(song.getSongTitle());
        artist.setText(song.getSongArtist());
        song.getAlbum().requestArt(art);
    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        boolean isPaused = state.getState() == PlaybackStateCompat.STATE_PAUSED;
        if (isPaused) {
            button.setImageResource(R.drawable.ic_play_arrow_white_48dp);
        } else {
            button.setImageResource(R.drawable.ic_pause_white_48dp);
        }
    }

    public void show(){

    }

    public void hide(){

    }
}
