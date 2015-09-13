package com.animbus.music.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Adrian on 8/24/2015.
 */
public class MediaButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PlaybackManager.get().getService().getCallback().onMediaButtonEvent(intent);
    }
}