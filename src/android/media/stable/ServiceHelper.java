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