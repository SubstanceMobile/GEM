package com.animbus.music.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.animbus.music.databinding.ItemAlbumDetailsList;
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
        return new AlbumDetailsViewHolder(ItemAlbumDetailsList.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(AlbumDetailsViewHolder holder, int position) {
        holder.item.setSong(data.get(position));
        holder.item.executePendingBindings();
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
        ItemAlbumDetailsList item;

        public AlbumDetailsViewHolder(ItemAlbumDetailsList binding) {
            super(binding.getRoot());
            item = binding;
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