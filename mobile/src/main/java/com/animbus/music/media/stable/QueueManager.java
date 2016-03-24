/*
 * Copyright (C) 2016 Substance Mobile
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
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