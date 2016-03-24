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

package com.afollestad.appthemeengine.inflation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.ATEActivity;

/**
 * @author Aidan Follestad (afollestad)
 */
class ATEViewUtil {

    public static String init(@Nullable ATEActivity keyContext, View view, Context context) {
        if (keyContext == null && context instanceof ATEActivity)
            keyContext = (ATEActivity) context;
        String key = null;
        if (keyContext != null)
            key = keyContext.getATEKey();
        // Process views just once (during inflation)
        if (view.isLayoutRequested())
            ATE.themeView(context, view, key);
        return key;
    }

    private ATEViewUtil() {
    }
}