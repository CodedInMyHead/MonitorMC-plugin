package com.codedinmyhead.monitormc.monitormc.commands;

import com.codedinmyhead.monitormc.monitormc.MonitorMC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PolyLineMarker;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerpathCommand implements CommandExecutor, TabCompleter {

    private final MarkerAPI markerAPI = MonitorMC.INSTANCE.markerAPI;

    //Speicherung der playerpath-Koordinaten:
    //ArrayList enthält 3 ArrayLists für koordinaten -> je eine für x, y, z
    //Position 0 -> für x; Position 1 -> für y; Position 2 -> für z
    private Map<String, ArrayList<Location>> collectionPlayerpaths = new HashMap<>();

    //Speicherung der verschiedenen pathNames zu den paths, welche ein einzelner Spieler erstellen kann
    private Map<String, ArrayList<String>> pathNamesPerPlayer = new HashMap<>();


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Location pLoc = p.getLocation();
            String pUUID = p.getUniqueId().toString();

            //Wechsel zwischen ausgewählten Modi
            switch (args[1]){
                case "sample":
                    addCoordinatesToPlayerpath(pLoc, pUUID, args[0]);

                case "record":

                case "delete":
                    deleteThisPlayerpath(p, args[0]);
            }



            sender.sendMessage("Position gespeichert");
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (!(sender instanceof Player)) {
            return completions;
        }

        if (args.length == 1) {
            String UUIDofPlayerAsString = ((Player) sender).getPlayer().getUniqueId().toString();
            if (pathNamesPerPlayer.containsKey(UUIDofPlayerAsString)){
                ArrayList<String> PlayerPathNames = pathNamesPerPlayer.get(UUIDofPlayerAsString);
                for (int i = 0; i < PlayerPathNames.size(); i++){
                    completions.add(PlayerPathNames.get(i));
                }
            }
            else {
                completions.add("<name>");
            }
        }
        else if (args.length == 2) {
            completions.add("sample");
            completions.add("record");
        }
        else if (args[1].equals("record")) {
            if (args.length == 3) {
                completions.add("start");
                completions.add("stop");
            }
            else if (args.length == 4) {
                completions.add("<threshold>");
            }
            else if (args.length == 5) {
                completions.add("<sample rate>");
            }
        }

        return completions;
    }



    private void addCoordinatesToPlayerpath(Location pLoc, String pUUID, String pathName) {
        ArrayList<Location> pPath = new ArrayList<>();
        String pName = Bukkit.getPlayer(UUID.fromString(pUUID)).getName();
        String uniquePathKey = pUUID + pathName;

        if (collectionPlayerpaths.containsKey(uniquePathKey)) {
            pPath = collectionPlayerpaths.get(uniquePathKey);
            pPath.add(pLoc);
            collectionPlayerpaths.replace(uniquePathKey, pPath);

            //ändere entsprechenden polyLineMarker
            //erstelle PolyLineMarker wenn es erst zweite Koordinate ist
            //skip, wenn es erst erste Koordinate ist
            int coordLength = pPath.size();
            boolean createNew = false;
            if (!(coordLength == 1)) {
                if (coordLength == 2) {
                    createNew = true;
                }

                addCoordinateToPolyline(pPath, pName, pathName, createNew);
            }
        }
        //wenn erste Koordinate die von diesen Spielerpath gespeichert wird:
        else {
            pPath.add(pLoc);
            collectionPlayerpaths.put(uniquePathKey, pPath);
            ArrayList<String> temporaryPathNames = new ArrayList<>();

            //füge pathName zu Pathnameliste (pathNamesPerPlayer) des Spielers hinzu
            if (pathNamesPerPlayer.containsKey(pUUID)) {
                temporaryPathNames = pathNamesPerPlayer.get(pUUID);
                temporaryPathNames.add(pathName);
                pathNamesPerPlayer.replace(pUUID, temporaryPathNames);
            }
            else{
                temporaryPathNames.add(pathName);
                pathNamesPerPlayer.put(pUUID, temporaryPathNames);
            }

        }
    }

    private void addCoordinateToPolyline(ArrayList<Location> coords, String pName, String pathName, boolean createNew) {
        if (MonitorMC.INSTANCE.playerPaths != null) {
            //konvertiere Locations aus ArrayLists in 3 arrays
            int coordLength = coords.size();

            double[] coords_x = new double[coordLength];
            double[] coords_y = new double[coordLength];
            double[] coords_z = new double[coordLength];

            for (int i = 0; i < coordLength; i++) {
                coords_x[i] = coords.get(i).getX();
                coords_y[i] = coords.get(i).getY();
                coords_z[i] = coords.get(i).getZ();
            }

            if (createNew) {
                MonitorMC.INSTANCE.playerPaths.createPolyLineMarker(pName + ";" + pathName, pathName, true, "world", coords_x, coords_y, coords_z, false);
            } else {
                MonitorMC.INSTANCE.playerPaths.findPolyLineMarker(pName + ";" + pathName).setCornerLocations(coords_x, coords_y, coords_z);
            }

        }
    }

    private void deleteThisPlayerpath(Player p, String pathName){
        //Muss gelöscht werden:
        //Koordinaten des path aus collectionPlayerpaths
        //pathName des path aus pathNamesPerPlayer für entsprechenden Spieler
        //PolyLine im Markerset
        String pName = p.getName();
        if (MonitorMC.INSTANCE.playerPaths.findPolyLineMarker(pName + ";" + pathName) == null){
            return;
        }

        String uniquePathKey = p.getUniqueId().toString() + ";" + pathName;
        collectionPlayerpaths.remove(uniquePathKey);

        String pUUID = p.getUniqueId().toString();
        ArrayList<String> thosePathNames = pathNamesPerPlayer.get(pUUID);
        thosePathNames.remove(pathName);
        pathNamesPerPlayer.replace(pUUID, thosePathNames);




            MonitorMC.INSTANCE.playerPaths.findPolyLineMarker(pName + ";" + pathName).deleteMarker();

    }


}