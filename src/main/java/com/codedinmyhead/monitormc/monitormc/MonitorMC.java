package com.codedinmyhead.monitormc.monitormc;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class MonitorMC extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Logger logger = this.getLogger();
        logger.info("STARTUP");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Logger logger = this.getLogger();
        logger.info("SHUTDOWN");
    }
}
