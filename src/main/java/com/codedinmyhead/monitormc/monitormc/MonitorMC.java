package com.codedinmyhead.monitormc.monitormc;

import com.codedinmyhead.monitormc.monitormc.commands.MonitorCommand;
import com.codedinmyhead.monitormc.monitormc.commands.AccuracyBowCommand;
import com.codedinmyhead.monitormc.monitormc.commands.PlayerpathCommand;
import com.codedinmyhead.monitormc.monitormc.listeners.common.ActivatedListeners;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricService;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricsEnum;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.logging.Logger;

public final class MonitorMC extends JavaPlugin {

    public static MonitorMC INSTANCE;

    public MonitorMC() {
        INSTANCE = this;
    }

    public ItemStack accuracyBow;
    public Material targetBlockMaterial = Material.RED_WOOL;

    public MarkerAPI markerAPI = null;

//Speicherung der playerpath-Koordinaten:
//ArrayList für KoordinatenArrays (Arrays je mit länge von 3 -> x, y, z Koordinate)
    public Map<UUID, ArrayList<double[]>> collectionPlayerpaths = new HashMap();
//    public ArrayList<double[]> playerpath = new ArrayList<>();

    @Override
    public void onEnable() {
        registerEvents();
        registerCommands();

        createAccuracyBow();

        MetricService.getInstance().initializeMetrics(Arrays.asList(MetricsEnum.values()));

        Logger l = this.getLogger();
        DynmapCommonAPIListener.register(new DynmapCommonAPIListener() {
            public void apiEnabled(DynmapCommonAPI dynmapCommonAPI) {
                markerAPI = dynmapCommonAPI.getMarkerAPI();
                l.info("dymap api enabled, marker api set!");

            }
        });


        if(markerAPI != null) {
            MarkerSet set = markerAPI.createMarkerSet("setId", "Test Set", null, false);
            MarkerIcon icon = markerAPI.getMarkerIcon("building");
            String htmlLabel = "<div>Hello World</div>";
            Marker marker = set.createMarker("uniqueMarkerId", htmlLabel, true,
                    "world", 10, 20, 30, icon, false);

//            MarkerSet set1 = markerAPI.createMarkerSet("setId1", "Ole Set", null, false);
            //Koordinaten: {x1, x2}, {y1, y2}
            AreaMarker areaMarker = set.createAreaMarker("areaMarkerId1", "Hallo Micha! (👉ﾟヮﾟ)👉", true, "world", new double[] {10, 20}, new double[] {30, 40}, true);
            areaMarker.setFillStyle(1, 0xd428c3);


        } else {
            this.getLogger().warning("MarkerAPI is null!");
        }



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
        Bukkit.getPluginCommand("accuracybow").setExecutor(new AccuracyBowCommand());
        Bukkit.getPluginCommand("path").setExecutor(new PlayerpathCommand());
    }

    public void createAccuracyBow() {
        accuracyBow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = accuracyBow.getItemMeta();
        bowMeta.displayName(Component.text("Accuracy Bow"));
        accuracyBow.setItemMeta(bowMeta);
    }

    public void addCoordinatesToPlayerpath(Location pLoc, UUID pUUID){
        double[] coords = new double[3];
        ArrayList<double[]> pPath = new ArrayList<>();

        coords[0] = pLoc.getX();
        coords[1] = pLoc.getY();
        coords[2] = pLoc.getZ();


        if (collectionPlayerpaths.containsKey(pUUID)){
            pPath = collectionPlayerpaths.get(pUUID);
            pPath.add(coords);
            collectionPlayerpaths.replace(pUUID, pPath);
        }
        else {
            pPath.add(coords);
            collectionPlayerpaths.put(pUUID, pPath);
        }
    }

}
