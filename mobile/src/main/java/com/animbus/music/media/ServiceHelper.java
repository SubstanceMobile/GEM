package com.animbus.music.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Adrian on 9/18/2015.
 */
public class ServiceHelper {
    Context cxt;
    private static ServiceHelper i = new ServiceHelper();

    public static ServiceHelper get(Context c) {
        i.cxt = c;
        return i;
    }

    private ServiceHelper() {
    }

    MediaService mService;
    public void initService(){
        if (mService == null){
            Intent i = new Intent(cxt, MediaService.class);
            ServiceConnection conn = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    MediaService.MusicBinder binder = (MediaService.MusicBinder) service;
                    mService = binder.getService();
                    mService.setUp();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mService = null;
                }
            };
            cxt.startService(i);
            boolean bound;
            bound = cxt.bindService(i, conn, Context.BIND_AUTO_CREATE);
            Log.d("Service Helper", "Bind Successful = " + String.valueOf(bound));
        }
    }

    public MediaService getService() {
        return mService;
    }
}
