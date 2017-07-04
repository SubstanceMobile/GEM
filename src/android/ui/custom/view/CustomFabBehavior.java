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
