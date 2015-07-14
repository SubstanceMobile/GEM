package com.animbus.music.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.animbus.music.R;
import com.animbus.music.data.objects.NowPlayingDatamodel;

import java.util.Collections;
import java.util.List;

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.NowPlayingViewHolder> {
    LayoutInflater inflater;
    List<NowPlayingDatamodel> data = Collections.emptyList();
    Context context;
    NowPlayingListClickListener onItemClickedListener;

    public NowPlayingAdapter(Context c, List<NowPlayingDatamodel> data) {
        inflater = LayoutInflater.from(c);
        this.data = data;
        this.context = c;
    }

    @Override
    public NowPlayingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_nowplaying, parent, false);
        return new NowPlayingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NowPlayingViewHolder holder, int position) {
        NowPlayingDatamodel current = data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickedListener(NowPlayingListClickListener clickListener) {
        onItemClickedListener = clickListener;
    }

    public interface NowPlayingListClickListener {
        void NowPlayingListItemClicked();
    }

    class NowPlayingViewHolder extends RecyclerView.ViewHolder implements OnClickListener {


        public NowPlayingViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickedListener != null) {
                onItemClickedListener.NowPlayingListItemClicked();
            }
        }
    }
}
