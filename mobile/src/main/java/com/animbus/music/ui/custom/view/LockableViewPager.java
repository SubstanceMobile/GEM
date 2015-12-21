package com.animbus.music.ui.custom.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Adrian on 7/18/2015.
 */
public class LockableViewPager extends ViewPager {
    private boolean locked = false;

    public LockableViewPager(Context context) {
        super(context);
    }

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !this.locked && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return !this.locked && super.onTouchEvent(ev);
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }
}