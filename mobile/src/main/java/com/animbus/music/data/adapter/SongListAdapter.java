package com.animbus.music.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.animbus.music.databinding.ItemSongList;
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
        return new SongListViewHolder(ItemSongList.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(final SongListViewHolder holder, int position) {
        holder.item.setSong(data.get(position));
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
        ItemSongList item;

        public SongListViewHolder(ItemSongList binding) {
            super(binding.root);
            item = binding;
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