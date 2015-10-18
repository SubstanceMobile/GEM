package com.animbus.music.data.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.animbus.music.R;
import com.animbus.music.databinding.ItemNowPlayingList;
import com.animbus.music.media.PlaybackManager;
import com.animbus.music.media.QueueManager;
import com.animbus.music.media.objects.Song;
import com.animbus.music.ui.theme.ThemeManager;

import java.util.Collections;
import java.util.List;

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.NowPlayingAdapterViewHolder>{
    LayoutInflater inflater;
    List<Song> data = Collections.emptyList();
    Context context;
    NowPlayingClickedListener onItemClickedListener;

    public NowPlayingAdapter(Context c) {
        inflater = LayoutInflater.from(c);
        data = QueueManager.get().getCurrentQueueAsSong();
        this.context = c;
        PlaybackManager.get().registerListener(new PlaybackManager.OnChangedListener() {
            @Override
            public void onSongChanged(Song song) {
                notifyItemChanged(0);
            }

            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {

            }
        });
    }

    @Override
    public NowPlayingAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NowPlayingAdapterViewHolder(ItemNowPlayingList.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(final NowPlayingAdapterViewHolder holder, final int position) {
        if (position == 0){
            Song customSong = PlaybackManager.get().getCurrentSong();

            holder.dataBinder.setSong(customSong);
            holder.dataBinder.setIsFirst(true);

            InsetDrawable eqIcon = new InsetDrawable(context.getResources().getDrawable(R.drawable.ic_equalizer_24dp), context.getResources().getDimensionPixelSize(R.dimen.margin_medium));
            DrawableCompat.setTint(eqIcon, customSong.getAlbum().accentColor);
            holder.dataBinder.nowplayingAlbumart.setImageDrawable(eqIcon);
            configureRepeatIcon(holder.dataBinder.nowPlayingRepeatIcon, customSong);
            holder.dataBinder.nowPlayingRepeatIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleRepeatIcon((ImageView) v, PlaybackManager.get().getCurrentSong());
                }
            });
        } else {
            holder.dataBinder.setSong(data.get(position - 1));
            holder.dataBinder.setIsFirst(false);
            holder.dataBinder.nowplayingAlbumart.albumArt(holder.dataBinder.getSong().getAlbum());
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
        return data.size() + 1;
    }

    public void setOnItemClickedListener(NowPlayingClickedListener clickListener) {
        onItemClickedListener = clickListener;
    }


    public interface NowPlayingClickedListener {
        void onNowPlayingItemClicker(View v, List<Song> data, int pos);
    }

    class NowPlayingAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        ItemNowPlayingList dataBinder;

        public NowPlayingAdapterViewHolder(ItemNowPlayingList dataBinder) {
            super(dataBinder.root);
            this.dataBinder = dataBinder;
            itemView.setOnClickListener(this);

            ViewCompat.setTransitionName(dataBinder.songlistSongTitle, "title");
            ViewCompat.setTransitionName(dataBinder.songlistSongArtist, "artist");
            ViewCompat.setTransitionName(dataBinder.nowPlayingFavoriteIcon, "nowPlayingButton");
        }

        @Override
        public void onClick(View v) {
            if (onItemClickedListener != null) {
                onItemClickedListener.onNowPlayingItemClicker(v, data, getAdapterPosition() - 1);
            }
        }
    }
}