package com.animbus.music;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class GestureManager extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
    OnSwipeListener swipeListener;
    OnSwipeUpListener swipeUpListener;
    OnSwipeDownListener swipeDownListener;
    OnSwipeLeftListener swipeLeftListener;
    OnSwipeRightListener swipeRightListener;
    int direction;
    Context cxt;
    View v;
    public static final Integer DIRECTION_UP = 1, DIRATION_DOWN = 3, DIRECTION_RIGHT = 2, DIRECTION_LEFT = 4;

    public GestureManager(){

    }

    public GestureManager(Context context, View view){
        cxt = context;
        v = view;
    }

    public void setSwipeUpListener(OnSwipeUpListener swipeUpListener) {
        this.swipeUpListener = swipeUpListener;
    }

    public void setSwipeDownListener(OnSwipeDownListener swipeDownListener) {
        this.swipeDownListener = swipeDownListener;
    }

    public void setSwipeLeftListener(OnSwipeLeftListener swipeLeftListener) {
        this.swipeLeftListener = swipeLeftListener;
    }

    public void setSwipeRightListener(OnSwipeRightListener swipeRightListener) {
        this.swipeRightListener = swipeRightListener;
    }

    public void setSwipeListener(OnSwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        GestureDetectorCompat mDetector = new GestureDetectorCompat(cxt, this);
        mDetector.onTouchEvent(event);
        return true;
    }

    public interface OnSwipeUpListener {
        void onSwipeUp(View v);
    }

    public interface OnSwipeDownListener {
        void onSwipeDown(View v);
    }

    public interface OnSwipeLeftListener {
        void onSwipeLeft(View v);
    }

    public interface OnSwipeRightListener {
        void onSwipeRight(View v);
    }

    public interface OnSwipeListener {
        void onSwipe(View v, int direction);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Integer velocityXCompat =  Math.round(velocityX);
        Integer velocityYCompat = Math.round(velocityY);
        if (velocityXCompat > 0.00){
            direction = DIRECTION_RIGHT;
            swipeRightListener.onSwipeRight(v);
        } else {
            direction = DIRECTION_LEFT;
            swipeLeftListener.onSwipeLeft(v);
        }

        if (velocityYCompat > 0.00){
            direction = DIRECTION_UP;
            swipeUpListener.onSwipeUp(v);

        } else {
            direction = DIRATION_DOWN;
            swipeDownListener.onSwipeDown(v);
        }
        swipeListener.onSwipe(v, direction);
        return true;
    }
}