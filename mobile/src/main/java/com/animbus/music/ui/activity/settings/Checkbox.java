package com.animbus.music.ui.activity.settings;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import com.afollestad.appthemeengine.prefs.ATECheckBoxPreference;

import java.lang.reflect.Field;

/**
 * Created by Adrian on 1/5/2016.
 */
public class Checkbox extends ATECheckBoxPreference {

    public Checkbox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Checkbox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Checkbox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        fixAnim();
    }

    @Override
    public void setWidgetLayoutResource(int widgetLayoutResId) {
        super.setWidgetLayoutResource(widgetLayoutResId);
        fixAnim();
    }

    @Override
    public void setLayoutResource(int layoutResId) {
        super.setLayoutResource(layoutResId);
        fixAnim();
    }

    void fixAnim() {
        try {
            Field canRecycleLayoutField = Preference.class.getDeclaredField("mHasSpecifiedLayout");
            canRecycleLayoutField.setAccessible(true);
            canRecycleLayoutField.setBoolean(this, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Field canRecycleLayoutField = Preference.class.getDeclaredField("mCanRecycleLayout");
            canRecycleLayoutField.setAccessible(true);
            canRecycleLayoutField.setBoolean(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
