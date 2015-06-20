package com.animbus.music.data.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.animbus.music.R;
import com.animbus.music.data.SettingsManager;
import com.animbus.music.data.dataModels.AlbumGridDataModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public class AlbumGridAdapter extends RecyclerView.Adapter<AlbumGridAdapter.AlbumGridViewHolder> {
    LayoutInflater inflater;
    List<AlbumGridDataModel> data = Collections.emptyList();
    Context context;
    AlbumArtGridClickListener onItemClickedListener;
    SettingsManager settings;
    Integer DEFAULT_TIME_MINIMUM = 15, DEFAULT_TIME_MAXIMUM = 35;

    public AlbumGridAdapter(Context c, List<AlbumGridDataModel> data) {
        inflater = LayoutInflater.from(c);
        this.data = data;
        this.context = c;
    }

    @Override
    public AlbumGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlbumGridViewHolder(inflater.inflate(R.layout.item_album_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(final AlbumGridViewHolder holder, int position) {
        AlbumGridDataModel current = data.get(position);
        settings = new SettingsManager(context);
        holder.AlbumName.setText(current.AlbumGridAlbumName);
        holder.AlbumArtist.setText(current.AlbumGridAlbumArtist);
        holder.AlbumArt.setImageResource(current.AlbumGridAlbumart);
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_PALETTE_IN_GRID,true)) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            Bitmap albumArtBitap = imageLoader.loadImageSync(null);
            Palette.from(albumArtBitap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch swatch = palette.getVibrantSwatch();
                    if (swatch != null){
                        holder.AlbumGridItemHeader.setBackgroundColor(swatch.getRgb());
                        holder.AlbumName.setTextColor(swatch.getTitleTextColor());
                        holder.AlbumArtist.setTextColor(swatch.getBodyTextColor());
                    } else {
                        if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
                            holder.AlbumGridItemHeader.setBackgroundColor(context.getResources().getColor(R.color.primaryGreyLight));
                            holder.AlbumName.setTextColor(context.getResources().getColor(R.color.primary_text_default_material_light));
                            holder.AlbumArtist.setTextColor(context.getResources().getColor(R.color.secondary_text_default_material_light));
                        } else {
                            holder.AlbumGridItemHeader.setBackgroundColor(context.getResources().getColor(R.color.primaryGreyDark));
                            holder.AlbumName.setTextColor(context.getResources().getColor(R.color.primary_text_default_material_dark));
                            holder.AlbumArtist.setTextColor(context.getResources().getColor(R.color.secondary_text_default_material_dark));
                        }
                    }
                }
            });
        } else {
            if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
                holder.AlbumGridItemHeader.setBackgroundColor(context.getResources().getColor(R.color.primaryGreyLight));
                holder.AlbumName.setTextColor(context.getResources().getColor(R.color.primary_text_default_material_light));
                holder.AlbumArtist.setTextColor(context.getResources().getColor(R.color.secondary_text_default_material_light));
            } else {
                holder.AlbumGridItemHeader.setBackgroundColor(context.getResources().getColor(R.color.primaryGreyDark));
                holder.AlbumName.setTextColor(context.getResources().getColor(R.color.primary_text_default_material_dark));
                holder.AlbumArtist.setTextColor(context.getResources().getColor(R.color.secondary_text_default_material_dark));
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickedListener(AlbumArtGridClickListener clickListener) {
        onItemClickedListener = clickListener;
    }

    public interface AlbumArtGridClickListener {
        void AlbumGridItemClicked(View view, int position, List<AlbumGridDataModel> data);
    }

    class AlbumGridViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        TextView AlbumName, AlbumArtist;
        ImageView AlbumArt;
        View AlbumGridItem, AlbumGridItemHeader;

        public AlbumGridViewHolder(View itemView) {
            super(itemView);
            AlbumName = (TextView) itemView.findViewById(R.id.AlbumArtGridItemInfoTitle);
            AlbumArtist = (TextView) itemView.findViewById(R.id.AlbumArtGridItemInfoArtist);
            AlbumArt = (ImageView) itemView.findViewById(R.id.AlbumArtGridItemAlbumArt);
            AlbumGridItemHeader = itemView.findViewById(R.id.AlbumArtGridItemInfoBar);
            AlbumGridItem = itemView.findViewById(R.id.AlbumGridItem);
            AlbumGridItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickedListener != null) {
                onItemClickedListener.AlbumGridItemClicked(v, getAdapterPosition(), data);
            }
        }
    }
}