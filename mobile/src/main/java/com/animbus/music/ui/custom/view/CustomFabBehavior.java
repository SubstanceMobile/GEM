/*
 * Copyright (C) 2016 Substance Mobile
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
 */

package com.animbus.music.ui.custom.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

/**
 * Used by Album Details
 */
public class CustomFabBehavior extends FloatingActionButton.Behavior {
    private Rect mTmpRect;

    public CustomFabBehavior(Context c, AttributeSet a){

    }

    private boolean updateFabVisibilityImageView(CoordinatorLayout parent, ImageView imageView, FloatingActionButton child) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)child.getLayoutParams();
        if(lp.getAnchorId() != imageView.getId()) {
            return false;
        } else {
            if(this.mTmpRect == null) {
                this.mTmpRect = new Rect();
            }

            Rect rect = this.mTmpRect;
            rect.set(0, 0, imageView.getWidth(), imageView.getHeight());
            parent.offsetDescendantRectToMyCoords(imageView, rect);
            if(rect.bottom <= (child.getHeight() + 4)) {
                child.hide();
            } else {
                child.show();
            }

            return true;
        }
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {
        List dependencies = parent.getDependencies(child);
        int i = 0;

        for(int count = dependencies.size(); i < count; ++i) {
            View dependency = (View)dependencies.get(i);
            if(dependency instanceof ImageView && this.updateFabVisibilityImageView(parent, (ImageView) dependency, child)) {
                break;
            }
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
         if(dependency instanceof ImageView) {
            this.updateFabVisibilityImageView(parent, (ImageView) dependency, child);
        }

        return super.onDependentViewChanged(parent, child, dependency);
    }

}
