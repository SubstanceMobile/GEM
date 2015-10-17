package com.animbus.music.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Adrian on 8/24/2015.
 */
public class MediaButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            PlaybackManager.get().getService().getCallback().onMediaButtonEvent(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Here I am!", Toast.LENGTH_SHORT).show();
        }
    }
}