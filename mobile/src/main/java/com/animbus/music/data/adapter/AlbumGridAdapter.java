package com.animbus.music.data.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
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
import com.animbus.music.media.objects.Album;
import com.animbus.music.ui.theme.ThemeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class AlbumGridAdapter extends RecyclerView.Adapter<AlbumGridAdapter.AlbumGridViewHolder> {
    private static final int GRID_ANIM_DELAY = 10;
    private static final int GRID_ANIM_DUR = 500;
    private static final int COLOR_DUR = 300;
    private static final int COLOR_DELAY_BASE = 550;
    private static final int COLOR_DELAY_MAX = 750;
    private static final int TYPE_BACK = 1, TYPE_TITLE = 2, TYPE_SUBTITLE = 3;
    final Property<TextView, Integer> textColor = new Property<TextView, Integer>(int.class, "textColor") {
        @Override
        public Integer get(TextView object) {
            return object.getCurrentTextColor();
        }

        @Override
        public void set(TextView object, Integer value) {
            object.setTextColor(value);
        }
    };
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
        holder.item.AlbumArtGridItemAlbumArt.albumArt(album);
        setDefaultBackColors(holder);
        setDefaultAccentColors(album);
        album.requestArt(new Album.ArtRequest() {
            @Override
            public void respond(Bitmap albumArt) {
                animateCell(holder, position);
                setColor(holder, album, position);
            }
        });
    }

    private void setColor(final AlbumGridViewHolder holder, final Album album, final int pos) {
        if (!SettingsManager.get().getBooleanSetting(SettingsManager.KEY_USE_PALETTE_IN_GRID, true)) {
            //Color extraction disabled
            setDefaultBackColors(holder, album);
            setDefaultAccentColors(album);
        } else {
            //color extraction enabled
            if (album.defaultArt) {
                //Default album art means default colors
                setDefaultBackColors(holder, album);
                setDefaultAccentColors(album);
            } else {
                //album art is compatible
                album.requestArt(new Album.ArtRequest() {
                    @Override
                    public void respond(Bitmap albumArt) {
                        Palette.from(albumArt).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch swatch = getMainSwatch(palette.getSwatches())[0];
                                Palette.Swatch accentSwatch = getMainSwatch(palette.getSwatches())[1];
                                album.BackgroundColor = swatch.getRgb();
                                album.TitleTextColor = swatch.getTitleTextColor();
                                album.SubtitleTextColor = swatch.getBodyTextColor();
                                album.accentColor = accentSwatch.getRgb();
                                album.accentIconColor = accentSwatch.getBodyTextColor();
                                animateColors(album, holder, pos);
                            }
                        });
                    }
                });
            }
        }
    }

    private Palette.Swatch[] getMainSwatch(List<Palette.Swatch> swatches) {
        ArrayList<Palette.Swatch> sortedSwatches = new ArrayList<>(swatches);
        Collections.sort(sortedSwatches, new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch a, Palette.Swatch b) {
                return ((Integer) a.getPopulation()).compareTo(b.getPopulation());
            }
        });
        return new Palette.Swatch[] {sortedSwatches.get(sortedSwatches.size() - 1), sortedSwatches.get(0)};
    }

    private void animateColors(Album a, AlbumGridViewHolder holder, int pos) {
        if (!a.colorAnimated) {
            Random colorDelayRandom = new Random();
            int MAX = COLOR_DELAY_MAX * pos;
            int COLOR_DELAY = colorDelayRandom.nextInt(COLOR_DELAY_MAX) + COLOR_DELAY_BASE;
            ObjectAnimator backgroundAnimator, titleAnimator, subtitleAnimator;
            backgroundAnimator = ObjectAnimator.ofObject(holder.item.AlbumInfoToolbar, "backgroundColor", new ArgbEvaluator(), defaultColor(TYPE_BACK),
                    a.BackgroundColor);
            backgroundAnimator.setDuration(COLOR_DUR).setStartDelay(COLOR_DELAY);
            backgroundAnimator.start();
            titleAnimator = ObjectAnimator.ofInt(holder.item.AlbumTitle, textColor, defaultColor(TYPE_TITLE), a.TitleTextColor);
            titleAnimator.setEvaluator(new ArgbEvaluator());
            titleAnimator.setDuration(COLOR_DUR).setStartDelay(COLOR_DELAY);
            titleAnimator.start();
            subtitleAnimator = ObjectAnimator.ofInt(holder.item.AlbumArtist, textColor, defaultColor(TYPE_SUBTITLE), a.SubtitleTextColor);
            subtitleAnimator.setEvaluator(new ArgbEvaluator());
            subtitleAnimator.setDuration(COLOR_DUR).setStartDelay(COLOR_DELAY);
            subtitleAnimator.start();

            a.colorAnimated = true;
        } else {
            holder.item.AlbumInfoToolbar.setBackgroundColor(a.BackgroundColor);
            holder.item.AlbumTitle.setTextColor(a.TitleTextColor);
            holder.item.AlbumArtist.setTextColor(a.SubtitleTextColor);
        }
    }

    private int defaultColor(int type) {
        int color;
        if (type == TYPE_BACK) {
            color = context.getResources().getColor(ThemeManager.get().useLightTheme ? R.color.primaryGreyLight : R.color.primaryGreyDark);
        } else if (type == TYPE_TITLE) {
            color = context.getResources().getColor(ThemeManager.get().useLightTheme ? R.color.primary_text_default_material_light : R.color.primary_text_default_material_dark);
        } else if (type == TYPE_SUBTITLE) {
            color = context.getResources().getColor(ThemeManager.get().useLightTheme ? R.color.secondary_text_default_material_light : R.color.secondary_text_default_material_dark);
        } else {
            color = 0;
        }
        return color;
    }

    private void setDefaultBackColors(AlbumGridViewHolder holder, Album a) {
        a.BackgroundColor = defaultColor(TYPE_BACK);
        a.TitleTextColor = defaultColor(TYPE_TITLE);
        a.SubtitleTextColor = defaultColor(TYPE_SUBTITLE);
        setDefaultBackColors(holder);
    }

    private void setDefaultBackColors(AlbumGridViewHolder holder) {
        holder.item.AlbumInfoToolbar.setBackgroundColor(defaultColor(TYPE_BACK));
        holder.item.AlbumTitle.setTextColor(defaultColor(TYPE_TITLE));
        holder.item.AlbumArtist.setTextColor(defaultColor(TYPE_SUBTITLE));
    }

    private void setDefaultAccentColors(Album a) {
        a.accentColor = Color.WHITE;
        a.accentIconColor = Color.BLACK;
    }

    private void animateCell(AlbumGridViewHolder holder, int pos) {
        Album current = data.get(pos);
        if (!current.animated) {
            current.animated = true;

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

            if (pos <= animateTill) {
                holder.item.AlbumGridItemRootView.setTranslationY(800.0f);
                int delayPart = pos * 100;
                holder.item.AlbumGridItemRootView.animate()
                        .translationY(0.0f)
                        .alpha(1.0f)
                        .setDuration(GRID_ANIM_DUR)
                        .setStartDelay(GRID_ANIM_DELAY + delayPart)
                        .start();
            } else holder.item.AlbumGridItemRootView.setAlpha(1.0f);
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
        void AlbumGridItemClicked(View view, Album album);

        void AlbumGridItemLongClicked(View view, Album album);
    }

    class AlbumGridViewHolder extends RecyclerView.ViewHolder implements OnClickListener, View.OnLongClickListener {
        public ItemAlbumGrid item;

        public AlbumGridViewHolder(ItemAlbumGrid item) {
            super(item.getRoot());
            this.item = item;
            item.getRoot().setOnClickListener(this);
            item.getRoot().setOnLongClickListener(this);
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