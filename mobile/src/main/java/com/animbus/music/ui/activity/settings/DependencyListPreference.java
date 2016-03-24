/*
 * Copyright (C) 2016 Substance Mobile
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
 */

package com.animbus.music.ui.activity.settings;

import android.content.Context;
import android.util.AttributeSet;

import com.afollestad.appthemeengine.prefs.ATEListPreference;

/**
 * Created by Adrian on 1/1/2016.
 */
public class DependencyListPreference extends ATEListPreference {
    public DependencyListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DependencyListPreference(Context context) {
        super(context);
    }

    public DependencyListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DependencyListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setValue(String value) {
        String mOldValue = getValue();
        super.setValue(value);
        if (!value.equals(mOldValue)) {
            notifyDependencyChange(shouldDisableDependents());
        }
    }

    @Override
    public boolean shouldDisableDependents() {
        boolean shouldDisableDependents = super.shouldDisableDependents();
        String value = getValue();
        return shouldDisableDependents || value == null || !value.equals("0");
    }
}
