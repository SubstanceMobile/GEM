package com.animbus.music.data.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.animbus.music.R;
import com.animbus.music.SettingsManager;
import com.animbus.music.databinding.ItemAlbumGrid;
import com.animbus.music.media.objects.album.Album;
import com.animbus.music.media.objects.album.AlbumColorHelper;
import com.animbus.music.ui.theme.ThemeManager;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AlbumGridAdapter extends RecyclerView.Adapter<AlbumGridAdapter.AlbumGridViewHolder> {
    private static final int GRID_ANIM_DELAY = 10;
    private static final int GRID_ANIM_DUR = 500;
    LayoutInflater inflater;
    List<Album> data = Collections.emptyList();
    Context context;
    AlbumArtGridClickListener onItemClickedListener;

    public AlbumGridAdapter(Context c, List<Album> data) {
        inflater = LayoutInflater.from(c);
        this.data = data;
        this.context = c;
    }

    @Override
    public AlbumGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlbumGridViewHolder(ItemAlbumGrid.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(final AlbumGridViewHolder holder, final int position) {
        final Album album = this.data.get(position);
        holder.item.setAlbum(album);
        holder.item.executePendingBindings();

        if (!album.animated) {
            album.animated = true;

            int animateTill;
            if (!SettingsManager.get().getBooleanSetting(SettingsManager.KEY_USE_TABS, false)) {
                animateTill = 5;
            } else {
                Configuration configuration = context.getResources().getConfiguration();
                if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    animateTill = 3;
                } else {
                    animateTill = 2;
                }
            }

            if (position <= animateTill) {
                holder.item.AlbumGridItemRootView.setTranslationY(800.0f);
                int delayPart = position * 100;
                holder.item.AlbumGridItemRootView.animate()
                        .translationY(0.0f)
                        .alpha(1.0f)
                        .setDuration(GRID_ANIM_DUR)
                        .setStartDelay(GRID_ANIM_DELAY + delayPart)
                        .start();
            } else holder.item.AlbumGridItemRootView.setAlpha(1.0f);
        }

        if (!album.colorAnimated) setDefaultBackColors(holder);
        AlbumColorHelper.into(album,
                holder.item.AlbumInfoToolbar,
                holder.item.AlbumTitle,
                holder.item.AlbumArtist,
                position,
                context,
                this);
    }

    private void setDefaultBackColors(AlbumGridViewHolder holder) {
        holder.item.AlbumInfoToolbar.setBackgroundColor(AlbumColorHelper.defaultColor(AlbumColorHelper.TYPE_BACK, context));
        holder.item.AlbumTitle.setTextColor(AlbumColorHelper.defaultColor(AlbumColorHelper.TYPE_TITLE, context));
        holder.item.AlbumArtist.setTextColor(AlbumColorHelper.defaultColor(AlbumColorHelper.TYPE_SUBTITLE, context));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickedListener(AlbumArtGridClickListener clickListener) {
        onItemClickedListener = clickListener;
    }

    public interface AlbumArtGridClickListener {
        void AlbumGridItemClicked(View view, Album album);

        void AlbumGridItemLongClicked(View view, Album album);
    }

    class AlbumGridViewHolder extends RecyclerView.ViewHolder implements OnClickListener, View.OnLongClickListener {
        public ItemAlbumGrid item;

        public AlbumGridViewHolder(ItemAlbumGrid item) {
            super(item.AlbumGridItemRootView);
            this.item = item;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickedListener != null) {
                onItemClickedListener.AlbumGridItemClicked(v, data.get(getAdapterPosition()));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemClickedListener != null) {
                onItemClickedListener.AlbumGridItemLongClicked(v, data.get(getAdapterPosition()));
                return true;
            } else {
                return false;
            }
        }
    }
}