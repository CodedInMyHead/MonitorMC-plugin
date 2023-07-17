package com.codedinmyhead.monitormc.monitormc.listeners.common;

import com.codedinmyhead.monitormc.monitormc.gui.DashboardGUI;
import com.codedinmyhead.monitormc.monitormc.listeners.ArrowHitListener;
import com.codedinmyhead.monitormc.monitormc.listeners.PlayerJoinListener;
import com.codedinmyhead.monitormc.monitormc.listeners.SleepListener;

public enum ActivatedListeners {

    ARROW_HIT(ArrowHitListener.class),
    Player_JOIN(PlayerJoinListener.class),
    SLEEP_LISTENER(SleepListener.class),
    DASHBOARDGUI_LISTENER(DashboardGUI.class);

    private Class<?> classType;

    ActivatedListeners(Class<?> classType) {
        this.classType = classType;
    }

    public Class<?> getClassType() {
        return classType;
    }
}
