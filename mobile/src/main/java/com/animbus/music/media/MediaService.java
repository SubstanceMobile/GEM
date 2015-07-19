package com.animbus.music.media;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Adrian on 7/18/2015.
 */
public class MediaService extends Service {
    IBinder binder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void stopService() {
        stopSelf();
    }

    public void setAsForeground(){

    }

    public void removeFromForeground() {
        stopForeground(false);
    }
}
