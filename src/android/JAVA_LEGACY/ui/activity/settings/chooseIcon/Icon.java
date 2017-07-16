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

package com.animbus.music.ui.activity.settings.chooseIcon;

import android.content.ComponentName;

import com.animbus.music.util.IconManager;

/**
 * Created by Adrian on 7/26/2015.
 */
public class Icon {
    ComponentName name;
    int id = -1;
    int designer = -1;
    int color = -1;

    public Icon(int designer, int color) {
        this.designer = designer;
        this.color = color;
    }

    public Icon() {

    }

    public ComponentName getName() {
        if (name == null) {
            name = IconManager.get().getName(getId());
        }
        return name;
    }

    public void setName(ComponentName name) {
        this.name = name;
    }

    public int getId() {
        if (id == -1) {
            id = IconManager.get().getID(designer, color);
        }
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDesigner() {
        return designer;
    }

    public void setDesigner(int designer) {
        this.designer = designer;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
