package com.animbus.music.CustomViews;

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

    public LockableViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !locked && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return !locked && super.onTouchEvent(ev);
    }

    public void lock(){
        locked = false;
    }

    public void unlock(){
        locked = true;
    }

}
