package com.codedinmyhead.monitormc.monitormc;

import com.codedinmyhead.monitormc.monitormc.commands.MonitorCommand;
import com.codedinmyhead.monitormc.monitormc.listener.ArrowHitListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MonitorMC extends JavaPlugin {

    public static MonitorMC INSTANCE;

    public MonitorMC() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ArrowHitListener(), this);
    }

    public void registerCommands() {

        Bukkit.getPluginCommand("monitormc").setExecutor(new MonitorCommand());
    }


}
