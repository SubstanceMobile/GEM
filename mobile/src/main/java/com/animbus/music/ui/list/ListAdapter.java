/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.animbus.music.ui.list;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.appthemeengine.ATE;
import com.animbus.music.BR;
import com.animbus.music.R;
import com.animbus.music.media.Library;
import com.animbus.music.media.PlaybackRemote;
import com.animbus.music.media.objects.Album;
import com.animbus.music.media.objects.Artist;
import com.animbus.music.media.objects.Playlist;
import com.animbus.music.media.objects.Song;
import com.animbus.music.tasks.Loader.TaskListener;
import com.animbus.music.ui.ItemAlbum;
import com.animbus.music.ui.ItemAlbumDetailsList;
import com.animbus.music.ui.ItemNowPlaying;
import com.animbus.music.ui.ItemPlaylist;
import com.animbus.music.ui.ItemSearch;
import com.animbus.music.ui.ItemSongList;
import com.animbus.music.ui.activity.albumDetails.AlbumDetails;
import com.animbus.music.ui.activity.playlistDetails.PlaylistDetails;
import com.animbus.music.ui.activity.search.SearchResult;
import com.animbus.music.ui.custom.activity.ThemeActivity;
import com.animbus.music.util.GEMUtil;
import com.animbus.music.util.Options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static com.animbus.music.media.objects.Album.FRAME_COLOR;
import static com.animbus.music.media.objects.Album.SUBTITLE_COLOR;
import static com.animbus.music.media.objects.Album.TITLE_COLOR;

public class ListAdapter<TYPE> extends RecyclerView.Adapter<ListAdapter.BasicViewHolder> {
    List<TYPE> data = new ArrayList<>();
    Type type;
    LayoutInflater inflater;
    Context context;

    public enum Type {
        TYPE_SONG, TYPE_ALBUM, TYPE_PLAYLIST, TYPE_ARTIST, TYPE_ALBUM_DETAILS, TYPE_NOW_PLAYING, TYPE_SEARCH, UNDEFINED
    }

    public ListAdapter(Type type, List<TYPE> data, Context cxt) {
        this.type = type;
        this.data = data;
        this.context = cxt.getApplicationContext();
        this.inflater = LayoutInflater.from(cxt);
    }

    @SuppressWarnings("unchecked")
    public ListAdapter(Type type, Context cxt) {
        this.type = type;
        this.context = cxt;
        this.inflater = LayoutInflater.from(cxt);

        //Listens for data changes
        TaskListener<TYPE> listener = new TaskListener<TYPE>() {
            @Override
            public void onOneLoaded(TYPE item, int pos) {
                if (!data.contains(item)) {
                    data.add(item);
                    notifyItemInserted(pos);
                }
            }

            @Override
            public void onCompleted(List<TYPE> result) {
                if (!data.equals(result)) {
                    data = result;
                    notifyDataSetChanged();
                }
            }
        };
        switch (type) {
            case TYPE_SONG:
                Library.registerSongListener((TaskListener<Song>) listener);
            case TYPE_ALBUM:
                Library.registerAlbumListener((TaskListener<Album>) listener);
            case TYPE_PLAYLIST:
                Library.registerPlaylstListener((TaskListener<Playlist>) listener);
            case TYPE_ARTIST:
                Library.registerArtistListener((TaskListener<Artist>) listener);
        }
    }

    @Override
    public BasicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (type) {
            case TYPE_SONG:
                return new SongsViewHolder(ItemSongList.inflate(inflater, parent, false));
            case TYPE_ALBUM:
                return new AlbumsViewHolder(ItemAlbum.inflate(inflater, parent, false));
            case TYPE_PLAYLIST:
                return new PlaylistsViewHolder(ItemPlaylist.inflate(inflater, parent, false));
            case TYPE_ARTIST:
                return null;
            case TYPE_ALBUM_DETAILS:
                return new AlbumDetailsViewHolder(ItemAlbumDetailsList.inflate(inflater, parent, false));
            case TYPE_NOW_PLAYING:
                return new NowPlayingViewHolder(ItemNowPlaying.inflate(inflater, parent, false));
            case TYPE_SEARCH:
                return new SearchViewHolder(ItemSearch.inflate(inflater, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BasicViewHolder holder, int position) {
        holder.update(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    Toolbar transitionAppBar;
    ThemeActivity transitionActivity;

    public ListAdapter withTransitionActivity(ThemeActivity activity) {
        this.transitionActivity = activity;
        this.transitionAppBar = activity.mToolbar;
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Holders
    ///////////////////////////////////////////////////////////////////////////

    protected abstract class BasicViewHolder<BINDING extends ViewDataBinding, OBJ> extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        protected BINDING binding;
        protected Context context;

        protected BasicViewHolder(BINDING binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void update(OBJ object) {
            binding.setVariable(getVarId(), object);
            binding.getRoot().setOnClickListener(this);
            binding.getRoot().setOnLongClickListener(this);
            configure(object);
        }

        private int getVarId() {
            int varId;
            switch (type) {
                case TYPE_ALBUM:
                    varId = BR.album;
                    break;
                case TYPE_SONG:
                    varId = BR.song;
                    break;
                case TYPE_ALBUM_DETAILS:
                    varId = BR.song;
                    break;
                case TYPE_PLAYLIST:
                    varId = BR.playlist;
                    break;
                case TYPE_NOW_PLAYING:
                    varId = BR.song;
                    break;
                case TYPE_SEARCH:
                    varId = BR.result;
                    break;
                default:
                    varId = -1;
                    break;
            }
            return varId;
        }

        protected abstract void configure(OBJ object);

        @Override
        public boolean onLongClick(View v) {
            //Do nothing. Can be overridden if it is necessary to do anything on a long click
            return false;
        }

    }

    protected abstract class SimpleViewHolder<BINDING extends ViewDataBinding, OBJ> extends BasicViewHolder<BINDING, OBJ> {
        protected SimpleViewHolder(BINDING binding) {
            super(binding);
        }

        @Override
        protected void configure(OBJ object) {
            //Do nothing. The default impl should do everything automatically
        }
    }

    protected class SongsViewHolder extends BasicViewHolder<ItemSongList, Song> {

        public SongsViewHolder(ItemSongList binding) {
            super(binding);
        }

        @Override
        public void configure(Song object) {
            object.getAlbum().requestArt(binding.songlistSongAlbumart);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onClick(View v) {
            PlaybackRemote.play((List<Song>) data, getAdapterPosition());
        }
    }

    @SuppressLint("PrivateResource")
    protected class AlbumsViewHolder extends BasicViewHolder<ItemAlbum, Album> implements Palette.PaletteAsyncListener, Album.ArtRequest {
        private final int defaultBackground = ContextCompat.getColor(context, !Options.isLightTheme() ? R.color.greyDark : R.color.greyLight),
        defaultTitle = ContextCompat.getColor(context, !Options.isLightTheme() ? R.color.primary_text_default_material_dark : R.color.primary_text_default_material_light),
        defaultSubtitle = ContextCompat.getColor(context, !Options.isLightTheme() ? R.color.secondary_text_default_material_dark : R.color.secondary_text_default_material_light);

        public AlbumsViewHolder(ItemAlbum binding) {
            super(binding);
        }

        @Override
        public void configure(Album object) {
            resetColors();
            binding.getAlbum().requestArt(binding.AlbumArtGridItemAlbumArt);
            binding.getAlbum().requestArt(this);
        }

        @Override
        public void respond(Bitmap art) {
            if (!binding.getAlbum().colorsLoaded && !binding.getAlbum().defaultArt) {
                Palette.from(art).generate(this);
                return;
            }
            if (binding.getAlbum().colorAnimated) {
                binding.AlbumInfoToolbar.setBackgroundColor(binding.getAlbum().getBackgroundColor());
                binding.AlbumTitle.setTextColor(binding.getAlbum().getTitleTextColor());
                binding.AlbumArtist.setTextColor(binding.getAlbum().getSubtitleTextColor());
            } else animatePalette();
        }

        @Override
        public void onGenerated(Palette palette) {
            int back = defaultBackground, title = defaultTitle, subtitle = defaultSubtitle;
            int accent = Color.BLACK, accentIcon = Color.WHITE, accentSubIcon = Color.GRAY;

            if (Options.usingPalette()) {

                //Gets main swatches
                ArrayList<Palette.Swatch> sortedSwatches = new ArrayList<>(palette.getSwatches());
                Collections.sort(sortedSwatches, new Comparator<Palette.Swatch>() {
                    @Override
                    public int compare(Palette.Swatch a, Palette.Swatch b) {
                        return ((Integer) a.getPopulation()).compareTo(b.getPopulation());
                    }
                });

                //Applies swatches to album
                try {
                    Palette.Swatch[] swatches = new Palette.Swatch[]{sortedSwatches.get(sortedSwatches.size() - 1), sortedSwatches.get(0)};

                    back = swatches[0].getRgb();
                    title = swatches[0].getBodyTextColor();
                    subtitle = swatches[0].getTitleTextColor();

                    accent = swatches[1].getRgb();
                    accentIcon = swatches[1].getBodyTextColor();
                    accentSubIcon = swatches[1].getTitleTextColor();
                } catch (Exception e) {
                    resetColors();
                    return;
                }
            }

            binding.getAlbum().mainColors = new int[]{back, title, subtitle};
            binding.getAlbum().accentColors = new int[]{accent, accentIcon, accentSubIcon};
            binding.getAlbum().colorsLoaded = true;

            animatePalette();
        }

        private void animatePalette() {
            int[] colors = binding.getAlbum().mainColors;
            Random colorDelayRandom = new Random();
            int COLOR_DELAY = colorDelayRandom.nextInt(750) + 550;

            ObjectAnimator backgroundAnimator = ObjectAnimator.ofObject(
                    binding.AlbumInfoToolbar, "backgroundColor", new ArgbEvaluator(), defaultBackground, colors[FRAME_COLOR]).setDuration(300);
            backgroundAnimator.setStartDelay(COLOR_DELAY);
            backgroundAnimator.start();

            ObjectAnimator titleAnimator = ObjectAnimator.ofObject(
                    binding.AlbumTitle, "textColor", new ArgbEvaluator(), defaultTitle, colors[TITLE_COLOR]).setDuration(300);
            titleAnimator.setStartDelay(COLOR_DELAY);
            titleAnimator.start();

            ObjectAnimator subtitleAnimator = ObjectAnimator.ofObject(
                    binding.AlbumArtist, "textColor", new ArgbEvaluator(), defaultSubtitle, colors[SUBTITLE_COLOR]).setDuration(300);
            subtitleAnimator.setStartDelay(COLOR_DELAY);
            subtitleAnimator.start();
        }

        private void resetColors() {
            binding.AlbumInfoToolbar.setBackgroundColor(defaultBackground);
            binding.AlbumTitle.setTextColor(defaultTitle);
            binding.AlbumArtist.setTextColor(defaultSubtitle);
        }

        ///////////////////////////////////////////////////////////////////////////
        // Click events
        ///////////////////////////////////////////////////////////////////////////

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, AlbumDetails.class).putExtra("album_id", binding.getAlbum().getID());
            try {
                @SuppressWarnings("unchecked")
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(transitionActivity,
                        new Pair<View, String>(transitionAppBar, "appbar"),
                        new Pair<View, String>(transitionAppBar, "appbar_text_protection"),
                        new Pair<>(binding.getRoot().findViewById(R.id.AlbumArtGridItemAlbumArt), "art"),
                        new Pair<>(binding.getRoot().findViewById(R.id.AlbumInfoToolbar), "info")
                );
                ActivityCompat.startActivity(transitionActivity, intent, options.toBundle());
            } catch (Exception e) {
                context.startActivity(intent);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            Snackbar.make(v, R.string.playing_album, Snackbar.LENGTH_SHORT).show();
            PlaybackRemote.play(binding.getAlbum().getSongs(), 0);
            return true;
        }
    }

    protected class AlbumDetailsViewHolder extends SimpleViewHolder<ItemAlbumDetailsList, Song> {

        protected AlbumDetailsViewHolder(ItemAlbumDetailsList binding) {
            super(binding);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onClick(View v) {
            PlaybackRemote.play((List<Song>) data, getAdapterPosition());
        }
    }

    protected class PlaylistsViewHolder extends BasicViewHolder<ItemPlaylist, Playlist> {

        protected PlaylistsViewHolder(ItemPlaylist binding) {
            super(binding);
        }

        @Override
        protected void configure(Playlist object) {
            if (object.getType() == 0) {
                binding.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_24dp));
            }
        }

        @Override
        public void onClick(View v) {
            //TODO: Fix this
            ValueAnimator anim = ObjectAnimator.ofFloat(0f, 16f).setDuration(500);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewCompat.setElevation(itemView, (float) animation.getAnimatedValue());
                }
            });
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    transition();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            anim.start();
        }

        private void transition() {
            Intent intent = new Intent(context, PlaylistDetails.class).putExtra("playlist_id", binding.getPlaylist().getId());
            try {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(transitionActivity, itemView, "window");
                ActivityCompat.startActivity(transitionActivity, intent, options.toBundle());
            } catch (Exception e) {
                context.startActivity(intent);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            try {
                PlaybackRemote.play(binding.getPlaylist().getSongs(), 0);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    protected class NowPlayingViewHolder extends BasicViewHolder<ItemNowPlaying, Song> {

        protected NowPlayingViewHolder(ItemNowPlaying binding) {
            super(binding);
        }

        @Override
        protected void configure(Song object) {
            binding.getSong().getAlbum().requestArt(binding.nowplayingAlbumart);
        }

        @Override
        public void onClick(View v) {
            PlaybackRemote.playQueueItem(getAdapterPosition());
        }
    }

    protected class SearchViewHolder extends BasicViewHolder<ItemSearch, SearchResult> {

        protected SearchViewHolder(ItemSearch binding) {
            super(binding);
        }

        @Override
        protected void configure(SearchResult object) {
            binding.getRoot().setClickable(false);
            Type type = Type.UNDEFINED;
            if (object.results.get(0) instanceof Album) type = Type.TYPE_ALBUM;
            else if (object.results.get(0) instanceof Song) type = Type.TYPE_SONG;
            else if (object.results.get(0) instanceof Playlist) type = Type.TYPE_PLAYLIST;
            ListAdapter<?> adapter = new ListAdapter<>(type, object.results, context);
            adapter.withTransitionActivity(transitionActivity);
            binding.recycler.setAdapter(adapter);
            if (type != Type.TYPE_ALBUM) {
                binding.recycler.setLayoutManager(new CustomLinearLayoutManager(context));
                binding.recycler.setLayoutFrozen(true);
                binding.recycler.setHasFixedSize(false);
            } else {
                binding.recycler.setLayoutManager(new CustomLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                int margin = (int) context.getResources().getDimension(R.dimen.margin_medium);
                binding.recycler.setClipToPadding(false);
                binding.recycler.setPaddingRelative(margin, 0, margin, 0);
            }
            binding.recycler.setItemAnimator(new DefaultItemAnimator());
            binding.recycler.requestLayout();
            ATE.themeView(context, binding.getRoot(), null);
        }

        @Override
        public void onClick(View v) {
            //Do nothing
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Album-specific classes
    ///////////////////////////////////////////////////////////////////////////

    public static final class AlbumAnimator extends DefaultItemAnimator {
        @Override
        public boolean animateAdd(final RecyclerView.ViewHolder holder) {
            holder.itemView.setTranslationY(800.0f);
            ViewCompat.animate(holder.itemView)
                    .translationY(0.0f)
                    .setDuration(500)
                    .setStartDelay(10 + (holder.getAdapterPosition() * 100))
                    .setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {
                            dispatchAddStarting(holder);
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            dispatchAddFinished(holder);
                        }

                        @Override
                        public void onAnimationCancel(View view) {

                        }
                    })
                    .start();
            return false;
        }
    }

    public static final class AlbumDecor extends RecyclerView.ItemDecoration {
        Context c;

        public AlbumDecor(Context context) {
            c = context;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            float px;
            if (!Options.usingBiggerSpaceInAlbumList()) px = GEMUtil.dpToPx(c, 0.5f); else px = GEMUtil.dpToPx(c, 2);
            outRect.top = (int) px;
            outRect.bottom = (int) px;
            outRect.left = (int) px;
            outRect.right = (int) px;
        }
    }
}

