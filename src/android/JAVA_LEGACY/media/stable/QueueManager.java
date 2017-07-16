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

package com.animbus.music.media.stable;

import android.support.v4.media.session.MediaSessionCompat.QueueItem;
import android.util.Log;

import com.animbus.music.media.objects.Song;

import java.util.Collections;
import java.util.List;

/**
 * Created by Adrian on 7/22/2015.
 */
public class QueueManager{
    private static final QueueManager instance = new QueueManager();
    List<Song> queue = Collections.emptyList();
    int currentSongPos;

    private QueueManager() {
    }

    public static QueueManager get() {
        return instance;
    }

    public List<QueueItem> getCurrentQueueAsQueueItem() {
        return toQueueItemList(queue);
    }

    public List<Song> getCurrentQueueAsSong() {
        return queue;
    }

    public List<Song> toSongList(List<QueueItem> itemList) {
        List<Song> songList = Collections.emptyList();
        for (int i = 0; i <= itemList.size(); i++) {
            songList.add(Song.parse());
        }
        return songList;
    }

    public List<QueueItem> toQueueItemList(List<Song> songList) {
        List<QueueItem> itemList = Collections.emptyList();
        for (Song s: songList) {
            itemList.add(s.toQueueItem());
        }
        return itemList;
    }

    public void setQueue(List queue) {
        if (queue.get(0) instanceof Song) {
            setQueueAsSongList(queue);
        } else if (queue.get(0) instanceof QueueItem) {
            setQueueAsQueueItemList(queue);
        } else {
            Log.e("QueueManager:", "Cannot parse list of type other then Song or QueueItem");
        }
    }

    public void setQueueAsQueueItemList(List<QueueItem> itemQueue) {
        queue = toSongList(itemQueue);
    }

    public void setQueueAsSongList(List<Song> songQueue) {
        queue = songQueue;
    }

    public void addToQueue(Song queueItem) {
        queue.add(queueItem);
    }

    public void addToQueue() {
        queue.add(Song.parse());
    }

    public int getCurrentSongPos() {
        return currentSongPos;
    }

    public void setCurrentSongPos(int pos) {
        currentSongPos = pos;
    }

    public int updateNextSongPos() {
        int pos;
        pos = currentSongPos + 1;
        if (pos > (getCurrentQueueAsSong().size() - 1)) {
            pos = 0;
        }
        setCurrentSongPos(pos);
        return pos;
    }

    public int updatePrevSongPos() {
        int pos;
        pos = currentSongPos - 1;
        if (pos == 0) {
            pos = (getCurrentQueueAsSong().size() - 1);
        }
        setCurrentSongPos(pos);
        return pos;
    }
}