package com.codedinmyhead.monitormc.monitormc;

import com.codedinmyhead.monitormc.monitormc.commands.DefaultStatsCommand;
import com.codedinmyhead.monitormc.monitormc.commands.MonitorCommand;
import com.codedinmyhead.monitormc.monitormc.commands.AccuracyBowCommand;
import com.codedinmyhead.monitormc.monitormc.commands.TopThreeCommand;
import com.codedinmyhead.monitormc.monitormc.gui.StatsGUI;
import com.codedinmyhead.monitormc.monitormc.gui.TopThreeGUI;
import com.codedinmyhead.monitormc.monitormc.listeners.common.ActivatedListeners;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricService;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricsEnum;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class MonitorMC extends JavaPlugin {

    public static MonitorMC INSTANCE;

    public MonitorMC() {
        INSTANCE = this;
    }

    public ItemStack accuracyBow;
    public Material targetBlockMaterial = Material.RED_WOOL;

    public final static TopThreeGUI topThreeGUI = new TopThreeGUI();

    public StatsGUI statsGui = new StatsGUI();

    @Override
    public void onEnable() {
        registerEvents();
        registerCommands();
        createAccuracyBow();

        MetricService.getInstance().initializeMetrics(Arrays.asList(MetricsEnum.values()));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        // Below entry is not in enum because a reference to the topThreeGUI object is needed
        // and only event register functionality is required
        pluginManager.registerEvents(topThreeGUI, this);

        pluginManager.registerEvents(statsGui, this);

        Arrays.asList(ActivatedListeners.values()).stream().filter(e -> e.isExternal()).forEach(entry -> {
            try {
                pluginManager.registerEvents((Listener) entry.getClassType().getDeclaredConstructor().newInstance(), this);
            } catch (Exception e) {}
        });
    }

    public void registerCommands() {

        Bukkit.getPluginCommand("monitormc").setExecutor(new MonitorCommand());
        Bukkit.getPluginCommand("accuracybow").setExecutor(new AccuracyBowCommand());
        Bukkit.getPluginCommand("leaderboard").setExecutor(new TopThreeCommand());
        Bukkit.getPluginCommand("stats").setExecutor(new DefaultStatsCommand());
    }

    public void createAccuracyBow() {
        accuracyBow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = accuracyBow.getItemMeta();
        bowMeta.displayName(Component.text("Accuracy Bow"));
        accuracyBow.setItemMeta(bowMeta);
    }

}
