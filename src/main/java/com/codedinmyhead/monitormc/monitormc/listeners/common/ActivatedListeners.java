package com.codedinmyhead.monitormc.monitormc.listeners.common;

import com.codedinmyhead.monitormc.monitormc.gui.DashboardGUI;
import com.codedinmyhead.monitormc.monitormc.listeners.*;
import com.codedinmyhead.monitormc.monitormc.listeners.ArrowHitListener;
import com.codedinmyhead.monitormc.monitormc.listeners.ArrowKillListener;
import com.codedinmyhead.monitormc.monitormc.listeners.PlayerJoinListener;
import com.codedinmyhead.monitormc.monitormc.listeners.SleepListener;

public enum ActivatedListeners {
    ARROW_HIT(ArrowHitListener.class),
    Player_JOIN(PlayerJoinListener.class),
    SLEEP_LISTENER(SleepListener.class),
    DASHBOARDGUI_LISTENER(DashboardGUI.class),
    LEADERBOARD_GUI(LeaderboardGUIListener.class),
    CHAT_NUMBER_SUM(ChatNumberListener.class),
    DIAMONDS_MINED(DiamondOreMined.class),
    ARROW_KILL(ArrowKillListener .class);

    private final Class<?> classType;

    ActivatedListeners(Class<?> classType) {
        this.classType = classType;
    }

    public Class<?> getClassType() {
        return classType;
    }
}
