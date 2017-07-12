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

package com.animbus.music.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Adrian on 9/20/2015.
 */
public class FabHelper {

    public static void setFabBackground(FloatingActionButton fab, int color) {
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public static void setFabTintedIcon(FloatingActionButton fab, Drawable icon, int color) {
        DrawableCompat.setTint(DrawableCompat.wrap(icon), color);
        fab.setImageDrawable(icon);
    }

    public static ValueAnimator getAnimatorAlong(final FloatingActionButton fab, Path path) {
        if (Build.VERSION.SDK_INT >= 21) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(fab, View.X, View.Y, path);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(400);
            return animator;
        } else {
            final PathMeasure pm = new PathMeasure(path, false);
            final float[] point = {0f, 0f};
            ValueAnimator.AnimatorUpdateListener listener = new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float val = animation.getAnimatedFraction();
                    pm.getPosTan(pm.getLength() * val, point, null);
                    fab.setTranslationX(point[0]);
                    fab.setTranslationY(point[1]);
                }
            };
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(400);
            animator.addUpdateListener(listener);
            return animator;
        }
    }

    public static Animator getRevealAnim(final FloatingActionButton fab, final View toReveal) {
        if (Build.VERSION.SDK_INT >= 21) {
            Animator reveal = ViewAnimationUtils.createCircularReveal(toReveal,
                    toReveal.getWidth() / 2,
                    toReveal.getHeight() / 2,
                    fab.getWidth() / 2,
                    Math.max(toReveal.getWidth() / 2, toReveal.getHeight() / 2));
            reveal.setDuration(300);
            reveal.setInterpolator(new AccelerateDecelerateInterpolator());
            return reveal;
        } else {
            int radius = fab.getWidth() / 2;
            final Rect clipping = new Rect(toReveal.getLeft(), toReveal.getTop(), toReveal.getRight(), toReveal.getBottom());
            ValueAnimator reveal = ObjectAnimator.ofInt(radius, Math.max(toReveal.getWidth() / 2, toReveal.getHeight() / 2));
            reveal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fab.setScaleX((Integer) animation.getAnimatedValue());
                    fab.setScaleY((Integer) animation.getAnimatedValue());
                    ViewCompat.setClipBounds(fab, clipping);
                }
            });
            reveal.setDuration(300);
            reveal.setInterpolator(new AccelerateDecelerateInterpolator());
            return reveal;
        }
    }

    public static Animator getReverseRevealAnim(final FloatingActionButton fab, View toReveal){
        if (Build.VERSION.SDK_INT >= 21) {
            Animator reveal = ViewAnimationUtils.createCircularReveal(toReveal,
                    toReveal.getWidth() / 2,
                    toReveal.getHeight() / 2,
                    Math.max(toReveal.getWidth() / 2, toReveal.getHeight() / 2),
                    fab.getWidth() / 2);
            reveal.setDuration(300);
            reveal.setInterpolator(new AccelerateDecelerateInterpolator());
            return reveal;
        } else {
            int radius = fab.getWidth() / 2;
            final Rect clipping = new Rect(toReveal.getLeft(), toReveal.getTop(), toReveal.getRight(), toReveal.getBottom());
            ValueAnimator reveal = ObjectAnimator.ofInt(Math.max(toReveal.getWidth() / 2, toReveal.getHeight() / 2), radius);
            reveal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    fab.setScaleX((Float) animation.getAnimatedValue());
                    fab.setScaleY((Float) animation.getAnimatedValue());
                    ViewCompat.setClipBounds(fab, clipping);
                }
            });
            reveal.setDuration(300);
            reveal.setInterpolator(new AccelerateDecelerateInterpolator());
            return reveal;
        }
    }

}
