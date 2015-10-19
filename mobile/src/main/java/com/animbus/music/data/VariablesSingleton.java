package com.animbus.music.data;

import com.animbus.music.ui.mainScreen.MainScreen;

/**
 * Created by Adrian on 7/15/2015.
 */
public class VariablesSingleton {
    private static final VariablesSingleton instance = new VariablesSingleton();

    public static VariablesSingleton get() {
        return instance;
    }

    private VariablesSingleton() {
    }

    public boolean activated;
    public MainScreen settingsMyLib;
}
