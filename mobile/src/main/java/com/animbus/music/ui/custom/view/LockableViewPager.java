package com.animbus.music.ui.custom.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Adrian on 7/18/2015.
 */
public class LockableViewPager extends ViewPager {
    Boolean locked = false;

    public LockableViewPager(Context context) {
        super(context);
    }

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!this.locked) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!this.locked) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

}
