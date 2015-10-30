package com.animbus.music.data.list;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.annotation.IntDef;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.animbus.music.databinding.ItemAlbumDetailsList;
import com.animbus.music.databinding.ItemAlbumGrid;
import com.animbus.music.databinding.ItemNowPlayingList;
import com.animbus.music.databinding.ItemSongList;
import com.animbus.music.media.objects.Album;
import com.animbus.music.media.objects.Song;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by Adrian on 10/28/2015.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.SimpleViewHolder> {
    public static final int TYPE_SONG = 0, TYPE_ALBUM = 1, TYPE_PLAYLIST = 2, TYPE_GENRE = 3, TYPE_ARTIST = 4;
    public static final int TYPE_ALBUM_DETAILS = -1, TYPE_NOW_PLAYING = -2;
    List data;
    int type;
    LayoutInflater inflater;
    Context context;
    BaseListener listener;

    @IntDef({TYPE_SONG, TYPE_ALBUM, TYPE_PLAYLIST, TYPE_GENRE, TYPE_ARTIST, TYPE_NOW_PLAYING, TYPE_ALBUM_DETAILS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public ListAdapter(@Type int type, List data, Context cxt) {
        this.type = type;
        this.data = data;
        this.context = cxt;
        this.inflater = LayoutInflater.from(cxt);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (type == TYPE_SONG) {
            return new SongsViewHolder(ItemSongList.inflate(inflater, parent, false));
        } else if (type == TYPE_ALBUM) {
            return new AlbumsViewHolder(ItemAlbumGrid.inflate(inflater, parent, false));
        } else if (type == TYPE_PLAYLIST) {
            return null;
        } else if (type == TYPE_ARTIST) {
            return null;
        } else if (type == TYPE_GENRE) {
            return null;
        } else if (type == TYPE_ALBUM_DETAILS) {
          return new AlbumDetailsViewHolder(ItemAlbumDetailsList.inflate(inflater, parent, false));
        } else if (type == TYPE_NOW_PLAYING) {
            return new NowPlayingViewHolder(ItemNowPlayingList.inflate(inflater, parent, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.update(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    protected interface BaseListener<TYPE> {
        void onClick(TYPE object, List<TYPE> data, int pos);

        boolean onLongClick(TYPE object, List<TYPE> data, int pos);
    }

    public interface AlbumListener extends BaseListener<Album> {
    }

    public interface SongListener extends BaseListener<Song> {
    }

    public void setListener(BaseListener listener) {
        this.listener = listener;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Holders
    ///////////////////////////////////////////////////////////////////////////

    protected abstract class SimpleViewHolder<BINDING extends ViewDataBinding, TYPE> extends RecyclerView.ViewHolder {
        protected BINDING binding;

        protected SimpleViewHolder(BINDING binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void update(TYPE object) {
            configure(object);
            configureListener(object);
        }

        protected abstract void configure(TYPE object);

        private void configureListener(final TYPE object) {
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onClick(object, data, getAdapterPosition());
                }
            });

            binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return listener != null && listener.onLongClick(object, data, getAdapterPosition());
                }
            });
        }

    }

    protected class SongsViewHolder extends SimpleViewHolder<ItemSongList, Song> {

        public SongsViewHolder(ItemSongList binding) {
            super(binding);
        }

        @Override
        public void configure(Song object) {
            binding.setSong(object);
        }
    }

    protected class AlbumsViewHolder extends SimpleViewHolder<ItemAlbumGrid, Album> {

        public AlbumsViewHolder(ItemAlbumGrid binding) {
            super(binding);
        }

        @Override
        public void configure(Album object) {
            binding.setAlbum(object);

        }
    }

    protected class NowPlayingViewHolder extends SimpleViewHolder<ItemNowPlayingList, Song> {

        protected NowPlayingViewHolder(ItemNowPlayingList binding) {
            super(binding);
        }

        @Override
        protected void configure(Song object) {
           binding.setSong(object);
        }
    }

    protected class AlbumDetailsViewHolder extends SimpleViewHolder<ItemAlbumDetailsList, Song> {

        protected AlbumDetailsViewHolder(ItemAlbumDetailsList binding) {
            super(binding);
        }

        @Override
        protected void configure(Song object) {
            binding.setSong(object);
        }
    }

}
