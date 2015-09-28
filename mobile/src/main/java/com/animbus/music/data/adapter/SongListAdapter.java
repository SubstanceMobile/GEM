package com.animbus.music.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.animbus.music.R;
import com.animbus.music.media.objects.Song;

import java.util.Collections;
import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongListViewHolder> {
    LayoutInflater inflater;
    List<Song> data = Collections.emptyList();
    Context context;
    SongListItemClickListener onItemClickedListener;

    public SongListAdapter(Context c, List<Song> data) {
        inflater = LayoutInflater.from(c);
        this.data = data;
        this.context = c;
    }

    @Override
    public SongListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_songlist, parent, false);
        return new SongListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SongListViewHolder holder, int position) {
        Song current = data.get(position);
        holder.SongName.setText(current.getSongTitle());
        holder.SongArtist.setText(current.getSongArtist());
        holder.SongDuration.setText(current.getSongDurString());
        holder.SongArt.setImageBitmap(current.getAlbum().getAlbumArt());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickedListener(SongListItemClickListener clickListener) {
        onItemClickedListener = clickListener;
    }

    public interface SongListItemClickListener {
        void SongListItemClicked(int position, List<Song> data);
    }

    class SongListViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        TextView SongName, SongArtist, SongDuration;
        ImageView SongArt;

        public SongListViewHolder(View itemView) {
            super(itemView);
            SongName = (TextView) itemView.findViewById(R.id.songlist_song_title);
            SongArtist = (TextView) itemView.findViewById(R.id.songlist_song_artist);
            SongArt = (ImageView) itemView.findViewById(R.id.songlist_song_albumart);
            SongDuration = (TextView) itemView.findViewById(R.id.songlist_song_duration);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickedListener != null) {
                onItemClickedListener.SongListItemClicked(getAdapterPosition(), data);
            }
        }
    }
}