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
import android.widget.ImageView;
import android.widget.TextView;

import com.animbus.music.R;
import com.animbus.music.SettingsManager;
import com.animbus.music.media.objects.Album;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AlbumGridAdapter extends RecyclerView.Adapter<AlbumGridAdapter.AlbumGridViewHolder> {
    private static final int GRID_ANIM_DELAY = 10;
    private static final int GRID_ANIM_DUR = 500;
    private static final int COLOR_DUR = 300;
    private static final int COLOR_DELAY_BASE = 550;
    private static final int COLOR_DELAY_MAX = 750;
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
    SettingsManager settings;

    public AlbumGridAdapter(Context c, List<Album> data) {
        inflater = LayoutInflater.from(c);
        this.data = data;
        this.context = c;
    }

    @Override
    public AlbumGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlbumGridViewHolder(inflater.inflate(R.layout.item_album_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(final AlbumGridViewHolder holder, final int position) {
        Album current = data.get(position);
        settings = SettingsManager.get();
        holder.AlbumName.setText(current.getAlbumTitle());
        holder.AlbumArtist.setText(current.getAlbumArtistName());
        holder.AlbumArt.setImageBitmap(current.getAlbumArt());
        setColor(current.getAlbumArt(), holder, current, position);
        animateCell(holder, position);
    }


    private void setColor(Bitmap image, final AlbumGridViewHolder holder, final Album current, final int pos) {
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_PALETTE_IN_GRID, true)) {
            setDefaultColors(holder);
            if (image != null) {
                if (!current.defaultArt) {
                    Palette.from(image).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                            Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                            Palette.Swatch accentSwatch = palette.getLightVibrantSwatch();
                            Palette.Swatch accentFallbackSwatch = palette.getLightMutedSwatch();
                            Palette.Swatch darkAccentSwatch = palette.getDarkVibrantSwatch();
                            Palette.Swatch darkAccentFallbackSwatch = palette.getDarkMutedSwatch();
                            Random colorDelayRandom = new Random();
                            int MAX = COLOR_DELAY_MAX * pos;
                            int COLOR_DELAY = colorDelayRandom.nextInt(COLOR_DELAY_MAX) + COLOR_DELAY_BASE;
                            ObjectAnimator backgroundAnimator, titleAnimator, subtitleAnimator;
                            if (vibrantSwatch != null) {
                                if (!current.colorAnimated) {
                                    current.colorAnimated = true;
                                    current.BackgroundColor = vibrantSwatch.getRgb();
                                    current.TitleTextColor = vibrantSwatch.getTitleTextColor();
                                    current.SubtitleTextColor = vibrantSwatch.getBodyTextColor();

                                    backgroundAnimator = ObjectAnimator.ofObject(holder.AlbumGridItemHeader, "backgroundColor", new ArgbEvaluator(), getDefaultBackColor(),
                                            current.BackgroundColor);
                                    backgroundAnimator.setDuration(COLOR_DUR).setStartDelay(COLOR_DELAY);
                                    backgroundAnimator.start();
                                    titleAnimator = ObjectAnimator.ofInt(holder.AlbumName, textColor, getDefaultTitleColor(), current.TitleTextColor);
                                    titleAnimator.setEvaluator(new ArgbEvaluator());
                                    titleAnimator.setDuration(COLOR_DUR).setStartDelay(COLOR_DELAY);
                                    titleAnimator.start();
                                    subtitleAnimator = ObjectAnimator.ofInt(holder.AlbumArtist, textColor, getDefaultSubTitleColor(), current.SubtitleTextColor);
                                    subtitleAnimator.setEvaluator(new ArgbEvaluator());
                                    subtitleAnimator.setDuration(COLOR_DUR).setStartDelay(COLOR_DELAY);
                                    subtitleAnimator.start();
                                } else {
                                    holder.AlbumGridItemHeader.setBackgroundColor(current.BackgroundColor);
                                    holder.AlbumName.setTextColor(current.TitleTextColor);
                                    holder.AlbumArtist.setTextColor(current.SubtitleTextColor);
                                }
                            } else if (mutedSwatch != null) {
                                if (!current.colorAnimated) {
                                    current.colorAnimated = true;
                                    current.BackgroundColor = mutedSwatch.getRgb();
                                    current.TitleTextColor = mutedSwatch.getTitleTextColor();
                                    current.SubtitleTextColor = mutedSwatch.getBodyTextColor();

                                    backgroundAnimator = ObjectAnimator.ofObject(holder.AlbumGridItemHeader, "backgroundColor", new ArgbEvaluator(), getDefaultBackColor(),
                                            current.BackgroundColor);
                                    backgroundAnimator.setDuration(COLOR_DUR).setStartDelay(COLOR_DELAY);
                                    backgroundAnimator.start();
                                    titleAnimator = ObjectAnimator.ofInt(holder.AlbumName, textColor, getDefaultTitleColor(), current.TitleTextColor);
                                    titleAnimator.setEvaluator(new ArgbEvaluator());
                                    titleAnimator.setDuration(COLOR_DUR).setStartDelay(COLOR_DELAY);
                                    titleAnimator.start();
                                    subtitleAnimator = ObjectAnimator.ofInt(holder.AlbumArtist, textColor, getDefaultSubTitleColor(), current.SubtitleTextColor);
                                    subtitleAnimator.setEvaluator(new ArgbEvaluator());
                                    subtitleAnimator.setDuration(COLOR_DUR).setStartDelay(COLOR_DELAY);
                                    subtitleAnimator.start();
                                } else {
                                    holder.AlbumGridItemHeader.setBackgroundColor(current.BackgroundColor);
                                    holder.AlbumName.setTextColor(current.TitleTextColor);
                                    holder.AlbumArtist.setTextColor(current.SubtitleTextColor);
                                }
                            } else {
                                setDefaultColors(holder, current);
                            }
                            if (accentSwatch != null) {
                                current.accentColor = accentSwatch.getRgb();
                                current.accentIconColor = accentSwatch.getTitleTextColor();
                            } else if (accentFallbackSwatch != null) {
                                current.accentColor = accentFallbackSwatch.getRgb();
                                current.accentIconColor = accentFallbackSwatch.getTitleTextColor();
                            } else {
                                current.accentColor = Color.WHITE;
                                current.accentIconColor = Color.BLACK;
                            }

                            if (darkAccentSwatch != null) {
                                current.darkPrimary = darkAccentSwatch.getRgb();
                            } else if (darkAccentFallbackSwatch != null) {
                                current.darkPrimary = darkAccentFallbackSwatch.getRgb();
                            } else {
                                current.darkPrimary = current.accentColor;
                            }
                        }
                    });
                } else {
                    setDefaultColors(holder, current);
                    current.accentColor = Color.WHITE;
                    current.accentIconColor = Color.BLACK;
                    current.darkPrimary = current.accentColor;
                }
            } else {
                setDefaultColors(holder, current);
                current.accentColor = Color.WHITE;
                current.accentIconColor = Color.BLACK;
                current.darkPrimary = current.accentColor;
            }
        } else {
            setDefaultColors(holder, current);
            current.accentColor = Color.WHITE;
            current.accentIconColor = Color.BLACK;
            current.darkPrimary = current.accentColor;
        }
    }

    public int getDefaultBackColor() {
        int color;
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
            color = R.color.primaryGreyLight;
        } else {
            color = R.color.primaryGreyDark;
        }
        return context.getResources().getColor(color);
    }

    public int getDefaultTitleColor() {
        int color;
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
            color = R.color.primary_text_default_material_light;
        } else {
            color = R.color.primary_text_default_material_dark;
        }
        return context.getResources().getColor(color);
    }

    public int getDefaultSubTitleColor() {
        int color;
        if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
            color = R.color.secondary_text_default_material_light;
        } else {
            color = R.color.secondary_text_default_material_dark;
        }
        return context.getResources().getColor(color);
    }

    private void setDefaultColors(AlbumGridViewHolder holder, Album a) {
        holder.AlbumGridItemHeader.setBackgroundColor(getDefaultBackColor());
        a.BackgroundColor = getDefaultBackColor();
        holder.AlbumName.setTextColor(getDefaultTitleColor());
        a.TitleTextColor = getDefaultTitleColor();
        holder.AlbumArtist.setTextColor(getDefaultSubTitleColor());
        a.SubtitleTextColor = getDefaultSubTitleColor();
    }

    private void setDefaultColors(AlbumGridViewHolder holder) {
        holder.AlbumGridItemHeader.setBackgroundColor(getDefaultBackColor());
        holder.AlbumName.setTextColor(getDefaultTitleColor());
        holder.AlbumArtist.setTextColor(getDefaultSubTitleColor());
    }

    private void animateCell(AlbumGridViewHolder holder, int pos) {
        Album current = data.get(pos);
        if (!current.animated) {
            current.animated = true;

            int animateTill;
            if (!settings.getBooleanSetting(SettingsManager.KEY_USE_TABS, false)) {
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
                holder.AlbumGridItemRoot.setAlpha(0.0f);
                holder.AlbumGridItemRoot.setTranslationY(800.0f);
                int delayPart = pos * 100;
                holder.AlbumGridItemRoot.animate()
                        .translationY(0.0f)
                        .alpha(1.0f)
                        .setDuration(GRID_ANIM_DUR)
                        .setStartDelay(GRID_ANIM_DELAY + delayPart)
                        .start();
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
        void AlbumGridItemClicked(View view, int position, List<Album> data);
    }

    class AlbumGridViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        protected Boolean animated = false;
        TextView AlbumName, AlbumArtist;
        ImageView AlbumArt;
        View AlbumGridItem, AlbumGridItemHeader, AlbumGridItemRoot;

        public AlbumGridViewHolder(View itemView) {
            super(itemView);
            AlbumName = (TextView) itemView.findViewById(R.id.AlbumArtGridItemInfoTitle);
            AlbumArtist = (TextView) itemView.findViewById(R.id.AlbumArtGridItemInfoArtist);
            AlbumArt = (ImageView) itemView.findViewById(R.id.AlbumArtGridItemAlbumArt);
            AlbumGridItemHeader = itemView.findViewById(R.id.AlbumArtGridItemInfoBar);
            AlbumGridItem = itemView.findViewById(R.id.AlbumGridItem);
            AlbumGridItemRoot = itemView.findViewById(R.id.AlbumGridItemRootView);
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