package com.codedinmyhead.monitormc.monitormc.listeners.common;

import com.codedinmyhead.monitormc.monitormc.gui.TopThreeGUI;
import com.codedinmyhead.monitormc.monitormc.listeners.ArrowHitListener;
import com.codedinmyhead.monitormc.monitormc.listeners.PlayerJoinListener;
import com.codedinmyhead.monitormc.monitormc.listeners.SleepListener;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum ActivatedListeners {

    // ClassType is for initialization, other values only relevant if isTopThree is true.
    // They will then be used to show statistics in TopThree GUI.

    ARROW_HIT(ArrowHitListener.class, Material.ARROW, "Times targets hit with Arrows", new String[]{"Count of Arrows that have been fired","and hit the red wool."}, true, true),
    Player_JOIN(PlayerJoinListener.class, Material.OAK_LOG, "Times joined", new String[]{"How many times a person joined the server."}, true, true),
    SLEEP_LISTENER(SleepListener.class, Material.RED_BED, "Times slept", new String[]{"How long a player has slept"}, true, true);

    private Class<?> classType;
    private  String name;
    private String[] lore;
    private boolean isTopThree;
    private Material material;
    private boolean isExternal;

    ActivatedListeners(Class<?> classType, Material material, String name, String[] lore, boolean isTopThree, boolean isExternal) {
        this.classType = classType;
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.isTopThree = isTopThree;
        this.isExternal = isExternal;
    }

    public Class<?> getClassType() {
        return classType;
    }
}
