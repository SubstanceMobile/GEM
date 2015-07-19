package com.animbus.music.ui.MainScreen;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.animbus.music.data.objects.Song;
import com.animbus.music.media.Old.MediaController;
import com.animbus.music.ui.NowPlaying.NowPlaying;
import com.animbus.music.ui.NowPlaying.NowPlayingClassic;
import com.animbus.music.R;
import com.animbus.music.ui.NowPlaying.NowPlayingPeek;
import com.animbus.music.SettingsManager;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingToolbar extends Fragment implements MediaController.OnUpdateListener{
    Context cxt;

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
                MediaController.getInstance().togglePlayback();
            }
        });

        MediaController.getInstance().setOnUpdateListener(this);
    }

    @Override
    public void onUpdate(Song currentSong, Boolean isPaused, Boolean isRepeating, Boolean isShuffled, List<Song> currentQueue) {
        TextView title = (TextView) getView().findViewById(R.id.song_toolbar_title);
        TextView artist = (TextView) getView().findViewById(R.id.song_toolbar_artist);
        ImageButton button = (ImageButton) getView().findViewById(R.id.play_pause_toolbar_button);

        title.setText(currentSong.getSongTitle());
        artist.setText(currentSong.getSongArtist());
        if (isPaused){
            button.setImageResource(R.drawable.ic_play_arrow_white_48dp);
        } else {
            button.setImageResource(R.drawable.ic_pause_white_48dp);
        }
    }
}
