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

package com.animbus.music.media.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.animbus.music.R;
import com.animbus.music.util.Options;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 7/5/2015.
 */
public class Album extends MediaObject {
    public String albumArtistName;

    public boolean animated;

    @Override
    protected Uri getBaseUri() {
        return MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This manages the songs of the album
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<Song> getSongs() {
        return new ArrayList<>();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This handles the Album Art
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String albumArtPath;
    public boolean defaultArt = false;

    public void setAlbumArtPath(String albumArtPath) {
        this.albumArtPath = "file://" + albumArtPath;
        if (albumArtPath != null) {
            defaultArt = false;
            colorAnimated = false;
        } else {
            defaultArt = true;
            colorAnimated = true;
        }
    }

    public String getAlbumArtPath() {
        return albumArtPath;
    }

    public interface ArtRequest {
        void respond(Bitmap albumArt);
    }

    public void requestArt(final ArtRequest request) {
        Glide.with(getContext()).load(getAlbumArtPath())
                .asBitmap()
                .placeholder(!Options.isLightTheme() ? R.drawable.art_dark : R.drawable.art_light)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .animate(android.R.anim.fade_in)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        request.respond(resource);
                    }
                });
    }

    public void requestArt(ImageView imageView) {
        Glide.with(imageView.getContext()).load(getAlbumArtPath())
                .placeholder(!Options.isLightTheme() ? R.drawable.art_dark : R.drawable.art_light)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Colors
    ///////////////////////////////////////////////////////////////////////////

    public boolean colorAnimated = false;
    public static final int FRAME_COLOR = 0, TITLE_COLOR = 1, SUBTITLE_COLOR = 2;
    public int[] mainColors;
    public int[] accentColors = new int[]{
            Color.BLACK, Color.WHITE, Color.GRAY
    };
    public boolean colorsLoaded = false;

    public int getBackgroundColor() {
        return mainColors[FRAME_COLOR];
    }

    public int getTitleTextColor() {
        return mainColors[TITLE_COLOR];
    }

    public int getSubtitleTextColor() {
        return mainColors[SUBTITLE_COLOR];
    }

    public int getAccentColor() {
        return accentColors[FRAME_COLOR];
    }

    public int getAccentIconColor() {
        return accentColors[TITLE_COLOR];
    }

    public int getAccentSecondaryIconColor() {
        return accentColors[SUBTITLE_COLOR];
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This handles the album artist
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setAlbumArtistName(String albumArtistName) {
        this.albumArtistName = albumArtistName;
    }

    public String getAlbumArtistName() {
        return albumArtistName;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Context behavior
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected boolean isContextRequired() {
        return true;
    }

    @Override
    protected void onContextSet(Context context) {
        mainColors = new int[]{
                context.getResources().getColor(!Options.isLightTheme() ? R.color.greyDark : R.color.greyLight),
                context.getResources().getColor(!Options.isLightTheme() ? R.color.primary_text_default_material_dark : R.color.primary_text_default_material_light),
                context.getResources().getColor(!Options.isLightTheme() ? R.color.secondary_text_default_material_dark : R.color.secondary_text_default_material_light)
        };
    }

}

