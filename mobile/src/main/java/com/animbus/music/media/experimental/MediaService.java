package com.animbus.music.media.experimental;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Adrian on 11/21/2015.
 */
public class MediaService extends Service {

    ///////////////////////////////////////////////////////////////////////////
    // Binding
    ///////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Actual Code
    ///////////////////////////////////////////////////////////////////////////

    PlaybackBase mPlaybackIMPL;


}
