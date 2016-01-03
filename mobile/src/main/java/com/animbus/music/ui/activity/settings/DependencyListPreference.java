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
