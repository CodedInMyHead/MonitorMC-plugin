package com.codedinmyhead.monitormc.monitormc;

import com.codedinmyhead.monitormc.monitormc.commands.MonitorCommand;
import com.codedinmyhead.monitormc.monitormc.listeners.ArrowHitListener;
import com.codedinmyhead.monitormc.monitormc.listeners.PlayerJoinListener;
import com.codedinmyhead.monitormc.monitormc.listeners.common.ActivatedListeners;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricService;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricsEnum;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class MonitorMC extends JavaPlugin {

    public static MonitorMC INSTANCE;

    public MonitorMC() {
        INSTANCE = this;
    }

    public MetricService metricService;

    @Override
    public void onEnable() {
        registerEvents();
        registerCommands();
        MetricService.getInstance().initializeMetrics(Arrays.asList(MetricsEnum.values()));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        Arrays.asList(ActivatedListeners.values()).forEach(entry -> {
            try {
                pluginManager.registerEvents((Listener) entry.getClassType().getDeclaredConstructor().newInstance(), this);

            } catch (Exception e) {}
        });
    }

    public void registerCommands() {

        Bukkit.getPluginCommand("monitormc").setExecutor(new MonitorCommand());
    }


}
