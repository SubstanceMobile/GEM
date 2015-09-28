package com.animbus.music.data.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.animbus.music.R;
import com.animbus.music.media.PlaybackManager;
import com.animbus.music.media.QueueManager;
import com.animbus.music.media.objects.Song;
import com.animbus.music.ui.theme.ThemeManager;

import java.util.Collections;
import java.util.List;

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.NowPlayingAdapterViewHolder> implements PlaybackManager.OnChangedListener {
    LayoutInflater inflater;
    List<Song> data = Collections.emptyList();
    Context context;
    NowPlayingClickedListener onItemClickedListener;

    public NowPlayingAdapter(Context c) {
        inflater = LayoutInflater.from(c);
        data = QueueManager.get().getCurrentQueueAsSong();
        this.context = c;
        data.add(0, PlaybackManager.get().getCurrentSong());
        update();
        PlaybackManager.get().registerListener(this);
    }

    @Override
    public NowPlayingAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_songlist, parent, false);
        return new NowPlayingAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NowPlayingAdapterViewHolder holder, final int position) {
        final Song current = data.get(position);
        holder.SongName.setText(current.getSongTitle());
        holder.SongArtist.setText(current.getSongArtist());
        if (position == 0){
            InsetDrawable eqIcon = new InsetDrawable(context.getResources().getDrawable(R.drawable.ic_equalizer_black_48dp), context.getResources().getDimensionPixelSize(R.dimen.margin_medium));
            DrawableCompat.setTint(eqIcon, current.getAlbum().accentColor);
            holder.SongArt.setImageDrawable(eqIcon);
            holder.SongDuration.setVisibility(View.GONE);
            holder.RepeatButtonRoot.setVisibility(View.VISIBLE);
            configureRepeatIcon(holder.RepeatButton, current);
            holder.RepeatButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleRepeatIcon((ImageView) v, current);
                }
            });
        } else {
            holder.SongArt.setImageBitmap(current.getAlbum().getAlbumArt());
            holder.SongDuration.setVisibility(View.VISIBLE);
            holder.SongDuration.setText(current.getSongDurString());
            holder.RepeatButtonRoot.setVisibility(View.GONE);
        }
    }

    private void configureRepeatIcon(ImageView i, Song s){
        if (PlaybackManager.get().isLooping()){
            Drawable repeatIcon = context.getResources().getDrawable(R.drawable.ic_repeat_one_black_48dp);
            DrawableCompat.setTint(repeatIcon, s.getAlbum().accentColor);
            i.setImageDrawable(repeatIcon);
        } else {
            Drawable repeatIcon = context.getResources().getDrawable(R.drawable.ic_repeat_black_48dp);
            DrawableCompat.setTint(repeatIcon, ThemeManager.get().useLightTheme ? context.getResources().getColor(R.color.secondary_text_default_material_light) : context.getResources().getColor(R.color.secondary_text_default_material_dark));
            i.setImageDrawable(repeatIcon);
        }
    }

    private void toggleRepeatIcon(ImageView i, Song s){
        if (PlaybackManager.get().isLooping()){
            Drawable repeatIcon = context.getResources().getDrawable(R.drawable.ic_repeat_black_48dp);
            DrawableCompat.setTint(repeatIcon, ThemeManager.get().useLightTheme ? context.getResources().getColor(R.color.secondary_text_default_material_light) : context.getResources().getColor(R.color.secondary_text_default_material_dark));
            i.setImageDrawable(repeatIcon);
            PlaybackManager.get().setRepeat(false);
        } else {
            Drawable repeatIcon = context.getResources().getDrawable(R.drawable.ic_repeat_one_black_48dp);
            DrawableCompat.setTint(repeatIcon, s.getAlbum().accentColor);
            i.setImageDrawable(repeatIcon);
            PlaybackManager.get().setRepeat(true);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickedListener(NowPlayingClickedListener clickListener) {
        onItemClickedListener = clickListener;
    }

    @Override
    public void onSongChanged(Song song) {
        update();
    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {

    }

    public interface NowPlayingClickedListener {
        void onNowPlayingItemClicker(View v, List<Song> data, int pos);
    }

    public void update(){
        data.set(0, PlaybackManager.get().getCurrentSong());
        notifyItemChanged(0);
    }

    class NowPlayingAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        TextView SongName, SongArtist, SongDuration;
        ImageView SongArt, RepeatButton;
        FrameLayout RepeatButtonRoot;

        public NowPlayingAdapterViewHolder(View itemView) {
            super(itemView);
            SongName = (TextView) itemView.findViewById(R.id.songlist_song_title);
            SongArtist = (TextView) itemView.findViewById(R.id.songlist_song_artist);
            SongArt = (ImageView) itemView.findViewById(R.id.songlist_song_albumart);
            SongDuration = (TextView) itemView.findViewById(R.id.songlist_song_duration);
            RepeatButton = (ImageView) itemView.findViewById(R.id.now_playing_repeat_icon);
            RepeatButtonRoot = (FrameLayout) itemView.findViewById(R.id.now_playing_repeat_icon_root);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickedListener != null) {
                onItemClickedListener.onNowPlayingItemClicker(v, data, getAdapterPosition());
            }
        }
    }
}