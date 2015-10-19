package com.animbus.music.ui.albumDetails;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Adrian on 9/20/2015.
 */
public class FabHelper {

    public static void setFabBackground(FloatingActionButton fab, int color) {
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public static void setFabTintedIcon(FloatingActionButton fab, Drawable icon, int color) {
        DrawableCompat.setTint(icon, color);
        fab.setImageDrawable(icon);
    }

    public static void setRippleColor(FloatingActionButton fab, int color) {
        fab.setRippleColor(color);
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

}
