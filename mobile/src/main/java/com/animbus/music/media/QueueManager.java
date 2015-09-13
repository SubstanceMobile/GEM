package com.animbus.music.media;

import android.support.v4.media.session.MediaSessionCompat.QueueItem;
import android.util.Log;

import com.animbus.music.media.objects.Song;

import java.util.Collections;
import java.util.List;

/**
 * Created by Adrian on 7/22/2015.
 */
public class QueueManager {
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
            songList.add(Song.parse(itemList.get(i)));
        }
        return songList;
    }

    public List<QueueItem> toQueueItemList(List<Song> songList) {
        List<QueueItem> itemList = Collections.emptyList();
        for (int i = 0; i <= itemList.size(); i++) {
            itemList.add(songList.get(i).toQueueItem());
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

    public void addToQueue(QueueItem queueItem) {
        queue.add(Song.parse(queueItem));
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
