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

package com.animbus.music.tasks;

import android.content.Context;

import com.animbus.music.media.objects.Artist;

import java.util.List;

/**
 * Created by Adrian on 3/25/2016.
 */
public class ArtistsTask extends Loader<Artist> {

    public ArtistsTask(Context context, Object... params) {
        super(context, params);
    }

    @Override
    protected List<Artist> doLoad(Object... params) {
        //TODO
        return null;
    }
}
