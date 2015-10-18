package com.animbus.music.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.animbus.music.R;
import com.animbus.music.media.objects.Song;

import java.util.Collections;
import java.util.List;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.AlbumDetailsViewHolder> {
    LayoutInflater inflater;
    List<Song> data = Collections.emptyList();
    Context context;
    AlbumDetailsClickListener onItemClickedListener;

    public AlbumDetailsAdapter(Context c, List<Song> data) {
        inflater = LayoutInflater.from(c);
        this.data = data;
        this.context = c;
    }

    @Override
    public AlbumDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_songlist_album_details, parent, false);
        return new AlbumDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlbumDetailsViewHolder holder, int position) {
        Song current = data.get(position);
        holder.SongName.setText(current.getSongTitle());
        holder.SongDuration.setText(current.getSongDurString());
        holder.SongPos.setText(current.getTrackNumber() != 0 ? String.valueOf(current.getTrackNumber()) : "-");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickedListener(AlbumDetailsClickListener clickListener) {
        onItemClickedListener = clickListener;
    }

    public interface AlbumDetailsClickListener {
        void onAlbumDetailsItemClicked(View v, List<Song> data, int pos);
    }

    class AlbumDetailsViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        TextView SongName, SongDuration, SongPos;

        public AlbumDetailsViewHolder(View itemView) {
            super(itemView);
            SongName = (TextView) itemView.findViewById(R.id.songlist_song_title);
            SongDuration = (TextView) itemView.findViewById(R.id.songlist_song_duration);
            SongPos = (TextView) itemView.findViewById(R.id.albumList_song_order);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickedListener != null) {
                onItemClickedListener.onAlbumDetailsItemClicked(v, data, getAdapterPosition());
            }
        }
    }
}