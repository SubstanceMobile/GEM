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
