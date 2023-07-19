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
import org.bukkit.entity.Player;
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

    //Markerset für Polylines der playerpaths
    public MarkerSet playerPaths = null;

//Speicherung der playerpath-Koordinaten:
//ArrayList enthält 3 ArrayLists für koordinaten -> je eine für x, y, z
//Position 0 -> für x; Position 1 -> für y; Position 2 -> für z
    public Map<String, ArrayList<ArrayList<Double>>> collectionPlayerpaths = new HashMap();

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


            //Set für PolyLines der playerPaths
            playerPaths = markerAPI.createMarkerSet("playerPaths", "playerPaths Test Set", null, false);


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

    public void addCoordinatesToPlayerpath(Location pLoc, String pUUID){
        ArrayList<ArrayList<Double>> pPath = new ArrayList<>();
        String pName = Bukkit.getPlayer(pUUID).getName();

        if (collectionPlayerpaths.containsKey(pUUID)){
            pPath = collectionPlayerpaths.get(pUUID);

            ArrayList<Double> coords_x = pPath.get(0);
            ArrayList<Double> coords_y = pPath.get(1);
            ArrayList<Double> coords_z = pPath.get(2);

            coords_x.add(pLoc.getX());
            coords_y.add(pLoc.getY());
            coords_z.add(pLoc.getZ());

            pPath.set(0, coords_x);
            pPath.set(1, coords_y);
            pPath.set(2, coords_z);

            collectionPlayerpaths.replace(pUUID, pPath);

            //ändere entsprechenden polyLineMarker
            //erstelle PolyLineMarker wenn es erst zweite Koordinate ist
            //skip, wenn es erst erste Koordinate ist
            int coordLength = pPath.get(0).size();
            boolean createNew = false;
            if (!(coordLength == 1)) {
                if (coordLength == 2) {
                    createNew = true;
                }

                addCoordinateToPolyline(pPath, pName, createNew);
            }
        }
        //wenn erste Koordinate die von diesem Spieler gespeichert wird:
        else {
            ArrayList<Double> coords_x = new ArrayList<>();
            ArrayList<Double> coords_y = new ArrayList<>();
            ArrayList<Double> coords_z = new ArrayList<>();

            coords_x.add(pLoc.getX());
            coords_y.add(pLoc.getY());
            coords_z.add(pLoc.getZ());

            pPath.set(0, coords_x);
            pPath.set(1, coords_y);
            pPath.set(2, coords_z);

            collectionPlayerpaths.put(pUUID, pPath);
        }
    }
    private void addCoordinateToPolyline(ArrayList<ArrayList<Double>> coords, String pName, boolean createNew){
        if (playerPaths != null){
            //convert ArrayLists into arrays
            int coordLength = coords.get(0).size();

            double[] coords_x = new double[coordLength];
            for (int i = 0; i < coordLength; i++)
                coords_x[i] = coords.get(0).get(i);

            double[] coords_y = new double[coordLength];
            for (int i = 0; i < coordLength; i++)
                coords_y[i] = coords.get(1).get(i);

            double[] coords_z = new double[coordLength];
            for (int i = 0; i < coordLength; i++)
                coords_z[i] = coords.get(2).get(i);

            if (createNew){
                playerPaths.createPolyLineMarker(pName, "just a test label", true, "world", coords_x, coords_y, coords_z, false);
            }
            else{
                playerPaths.findPolyLineMarker(pName).setCornerLocations(coords_x, coords_y, coords_z);
            }

        }
    }

}
