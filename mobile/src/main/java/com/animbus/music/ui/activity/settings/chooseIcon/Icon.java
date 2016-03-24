/*
 * Copyright (C) 2016 Substance Mobile
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
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
