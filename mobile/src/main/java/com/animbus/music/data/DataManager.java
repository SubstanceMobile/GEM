package com.animbus.music.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.graphics.Palette;

import com.animbus.music.R;
import com.animbus.music.data.dataModels.AlbumGridDataModel;
import com.animbus.music.data.dataModels.SongDataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataManager {
    public Integer DATATYPE_NAVIGATION_DRAWER = 0, DATATYPE_ALBUM_GRIDVIEW = 1, DATATYPE_SONG_LISTVIEW = 2, DATATYPE_ARTIST_LISTVIEW = 3, DATATYPE_PLAYLIST_LISTVIEW = 4, DATATYPE_GENRE_LISTVIEW = 5;
    public Integer BACKGROUND_COLOR = 0, TITLE_COLOR = 1, SUBTITLE_COLOR = 2, ACCENT_BACKGROUND_COLOR = 3, ACCENT_ICON_COLOR = 4;
    Context context;

    public DataManager(Context cxt) {
        context = cxt;
    }

    public List<AlbumGridDataModel> getAlbumGridData(Boolean getColors) {
        List<AlbumGridDataModel> data = new ArrayList<>();
        int[] AlbumArts = {R.drawable.album_art_alt, R.drawable.album_art_alt_alt, R.drawable.album_art,/*later*/R.drawable.album_art_alt, R.drawable.album_art_alt_alt, R.drawable.album_art, R.drawable.album_art_alt, R.drawable.album_art_alt_alt, R.drawable.album_art, R.drawable.album_art_alt, R.drawable.album_art_alt_alt, R.drawable.album_art, R.drawable.album_art_alt, R.drawable.album_art_alt_alt, R.drawable.album_art, R.drawable.album_art_alt, R.drawable.album_art_alt_alt, R.drawable.album_art};
        String[] AlbumName = {"Tombstone", "Silent Unspeakable Memories", "Better Off Ted",/*Later*/"Tombstone", "Silent Unspeakable Memories", "Better Off Ted", "Tombstone", "Silent Unspeakable Memories", "Better Off Ted", "Tombstone", "Silent Unspeakable Memories", "Better Off Ted", "Tombstone", "Silent Unspeakable Memories", "Better Off Ted", "Tombstone", "Silent Unspeakable Memories", "Better Off Ted"};
        String[] AlbumArtist = {"Kisaburo Osawa", "Riola Sardo", "Filbert",/*later*/"Kisaburo Osawa", "Riola Sardo", "Filbert", "Kisaburo Osawa", "Riola Sardo", "Filbert", "Kisaburo Osawa", "Riola Sardo", "Filbert", "Kisaburo Osawa", "Riola Sardo", "Filbert", "Kisaburo Osawa", "Riola Sardo", "Filbert"};
        for (int i = 0; i < AlbumName.length && i < AlbumArts.length; i++) {
            AlbumGridDataModel current = new AlbumGridDataModel();
            current.AlbumGridAlbumart = AlbumArts[i];
            current.AlbumGridAlbumName = AlbumName[i];
            current.AlbumGridAlbumArtist = AlbumArtist[i];
            if (getColors) {
                current.BackgroundColor = getColor(AlbumArts[i], BACKGROUND_COLOR);
                current.TitleTextColor = getColor(AlbumArts[i], TITLE_COLOR);
                current.SubtitleTextColor = getColor(AlbumArts[i], SUBTITLE_COLOR);
            }
            data.add(current);
        }
        return data;
    }

    public List<SongDataModel> getSongListData() {
        List<SongDataModel> data = new ArrayList<>();
        String[] SongTitle = {"Song One", "Song Two", "Song Three and Counting", /*Later*/ "Song One", "Song Two", "Song Three and Counting", "Song One", "Song Two", "Song Three and Counting", "Song One", "Song Two", "Song Three and Counting", "Song One", "Song Two", "Song Three and Counting", "Song One", "Song Two", "Song Three and Counting"};
        String[] SongArtist = {"Riola Sardo", "Kisaburo Osawa", "Filbert",/*Later*/"Riola Sardo", "Kisaburo Osawa", "Filbert","Riola Sardo", "Kisaburo Osawa", "Filbert","Riola Sardo", "Kisaburo Osawa", "Filbert","Riola Sardo", "Kisaburo Osawa", "Filbert","Riola Sardo", "Kisaburo Osawa", "Filbert"};
        String[] SongDuration = {"1:49", "2:50", "5:45", /*Later*/"1:49", "2:50", "5:45","1:49", "2:50", "5:45","1:49", "2:50", "5:45","1:49", "2:50", "5:45","1:49", "2:50", "5:45"};
        int[] SongArt = {R.drawable.album_art_alt_alt, R.drawable.album_art_alt, R.drawable.album_art, /*Later*/R.drawable.album_art_alt_alt, R.drawable.album_art_alt, R.drawable.album_art,R.drawable.album_art_alt_alt, R.drawable.album_art_alt, R.drawable.album_art,R.drawable.album_art_alt_alt, R.drawable.album_art_alt, R.drawable.album_art,R.drawable.album_art_alt_alt, R.drawable.album_art_alt, R.drawable.album_art,R.drawable.album_art_alt_alt, R.drawable.album_art_alt, R.drawable.album_art};
        for (int i = 0; i < SongTitle.length && i < SongArtist.length && i < SongArt.length && i < SongDuration.length; i++) {
            SongDataModel current = new SongDataModel();
            current.title = SongTitle[i];
            current.artist = SongArtist[i];
            current.albumart = SongArt[i];
            current.songDuration = SongDuration[i];
            data.add(current);
        }
        return data;
    }

    public int getColor(int image, Integer type) {
        ThreadColorExtraction colorThread = new ThreadColorExtraction();
        return colorThread.execute(image, type);
    }

    public Integer getRandomTime(Integer minimumNumber, Integer maximumNumber) {
        Random random = new Random();
        return random.nextInt((maximumNumber - minimumNumber) + 1) + minimumNumber;
    }

    class ThreadColorExtraction extends AsyncTaskCompat {
        SettingsManager settings;

        public ThreadColorExtraction() {
            //When new is called
            settings = new SettingsManager(context);
        }

        public int execute(int drawable, Integer type) {
            //Palette and setting colors
            Bitmap AlbumArt = BitmapFactory.decodeResource(context.getResources(), drawable);
            int color = Color.TRANSPARENT;
            Palette palette = Palette.from(AlbumArt).generate();
            Palette.Swatch swatch = palette.getVibrantSwatch();
            Palette.Swatch swatchAccent = palette.getLightVibrantSwatch();
            if (swatch != null) {
                if (type == 0) {
                    color = swatch.getRgb();
                } else if (type == 1) {
                    color = swatch.getTitleTextColor();
                } else if (type == 2) {
                    color = swatch.getBodyTextColor();
                }
            } else {
                if (settings.getBooleanSetting(SettingsManager.KEY_USE_LIGHT_THEME, false)) {
                    if (type == 0) {
                        color = context.getResources().getColor(R.color.primaryGreyLight);
                    } else if (type == 1) {
                        color = context.getResources().getColor(R.color.primary_text_default_material_light);
                    } else if (type == 2) {
                        color = context.getResources().getColor(R.color.secondary_text_default_material_light);
                    }
                } else {
                    if (type == 0) {
                        color = context.getResources().getColor(R.color.primaryGreyDark);
                    } else if (type == 1) {
                        color = context.getResources().getColor(R.color.primary_text_default_material_dark);
                    } else if (type == 2) {
                        color = context.getResources().getColor(R.color.secondary_text_default_material_dark);
                    }
                }
            }
            if (swatchAccent != null) {
                if (type == 3) {
                    color = context.getResources().getColor(R.color.primary_text_default_material_light);
                }
                if (type == 4) {
                    color = context.getResources().getColor(R.color.secondary_text_default_material_light);
                }
            } else {
                if (type == 3) {
                    color = context.getResources().getColor(R.color.primaryDark);
                }
                if (type == 4) {
                    color = context.getResources().getColor(R.color.primaryLight);
                }
            }
            AlbumArt.recycle();
            return color;
        }
    }
}
