package com.codedinmyhead.monitormc.monitormc;

import com.codedinmyhead.monitormc.monitormc.commands.DashboardGuiCommand;
import com.codedinmyhead.monitormc.monitormc.commands.MonitorCommand;
import com.codedinmyhead.monitormc.monitormc.commands.AccuracyBowCommand;
import com.codedinmyhead.monitormc.monitormc.commands.WaypointSet;
import com.codedinmyhead.monitormc.monitormc.gui.DashboardGUI;
import com.codedinmyhead.monitormc.monitormc.commands.TopThreeCommand;
import com.codedinmyhead.monitormc.monitormc.gui.TopThreeGUI;
import com.codedinmyhead.monitormc.monitormc.listeners.common.ActivatedListeners;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricService;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricsEnum;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;

public final class MonitorMC extends JavaPlugin {

    public static MonitorMC INSTANCE;

    public MonitorMC() {
        INSTANCE = this;
    }

    public ItemStack accuracyBow;
    public Material targetBlockMaterial = Material.RED_WOOL;

    public MarkerAPI markerAPI = null;
    public DashboardGUI dashboardGUI;
    private File customDashboardConfigFile;
    private FileConfiguration customDashboardConfig;

    public final static TopThreeGUI topThreeGUI = new TopThreeGUI();
  
    @Override
    public void onEnable() {
        Logger l = this.getLogger();
        DynmapCommonAPIListener.register(new DynmapCommonAPIListener() {
            public void apiEnabled(DynmapCommonAPI dynmapCommonAPI) {
                markerAPI = dynmapCommonAPI.getMarkerAPI();
                l.info("dymap api enabled, marker api set!");

            }
        });
    
        createAccuracyBow();

        createCustomDashboardConfigFile();
        this.dashboardGUI = new DashboardGUI();

        MetricService.getInstance().initializeMetrics(Arrays.asList(MetricsEnum.values()));

        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(dashboardGUI, this);

        Arrays.asList(ActivatedListeners.values()).forEach(entry -> {
            try {
                pluginManager.registerEvents((Listener) entry.getClassType().getDeclaredConstructor().newInstance(), this);
            } catch (Exception e) {
                getLogger().warning("ActiveListener entry " + entry + " threw an exception in initialization!");
            }
        });
    }

    public void registerCommands() {
        Bukkit.getPluginCommand("monitormc").setExecutor(new MonitorCommand());
        Bukkit.getPluginCommand("accuracybow").setExecutor(new AccuracyBowCommand());
        PluginCommand waypointCommand = Bukkit.getPluginCommand("waypoint");
        waypointCommand.setExecutor(new WaypointSet());
        waypointCommand.setTabCompleter(new WaypointSet());
        Bukkit.getPluginCommand("dashboards").setExecutor(new DashboardGuiCommand());
        Bukkit.getPluginCommand("leaderboard").setExecutor(new TopThreeCommand());
    }

    public void createAccuracyBow() {
        accuracyBow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = accuracyBow.getItemMeta();
        bowMeta.displayName(Component.text("Accuracy Bow"));
        accuracyBow.setItemMeta(bowMeta);
    }


    private void createCustomDashboardConfigFile() {
        customDashboardConfigFile = new File(getDataFolder(), "customDashboards.yml");
        if (!customDashboardConfigFile.exists()) {
            customDashboardConfigFile.getParentFile().mkdirs();
            saveResource("customDashboards.yml", true);
        }

        customDashboardConfig = new YamlConfiguration();
        try {
            customDashboardConfig.load(customDashboardConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getCustomDashboardConfig() {
        return this.customDashboardConfig;
    }

    public boolean reloadCustomDashboardConfig() {
        try {
            customDashboardConfig.load(customDashboardConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
        this.dashboardGUI = new DashboardGUI();
        return true;
    }
}
