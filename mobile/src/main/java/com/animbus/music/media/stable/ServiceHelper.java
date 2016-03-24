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