/*
 * Copyright (C) 2016 Substance Mobile
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
 */

package com.animbus.music.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

public class GEMUtil {

    private GEMUtil() {
        //No "new" statements
    }

    /**
     * Convenience method that simplifies calling intents and such
     * @param cxt The context to start the activity from
     * @param url The url so start
     */
    public static void startUrl(Context cxt, String url) {
        cxt.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
    }

    /**
     * Formats strings to match time. Is either hh:mm:ss or mm:ss
     * @param time The raw time in ms
     * @return The formatted string value
     */
    @SuppressLint("DefaultLocale")
    public static String stringForTime(long time) {
        int totalSeconds = (int) time / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Android Version Utils
    ///////////////////////////////////////////////////////////////////////////

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= 23;
    }
}
