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
import android.util.AttributeSet;
import android.widget.ImageView;

import com.animbus.music.media.objects.Album;

public class SquareImageViewWidth_Based extends ImageView {
    public boolean isInEditMode() {
        return true;
    }

    public SquareImageViewWidth_Based(Context context) {
        super(context);
    }

    public SquareImageViewWidth_Based(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageViewWidth_Based(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

}
