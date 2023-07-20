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
    //nutzt uniquePathKey als key
    private Map<String, ArrayList<Location>> collectionPlayerpaths = new HashMap<>();

    //speichert, ob zu dem path aktuell ein Recording läuft (automatische samples) default: false
    //nutzt ebenfalls uniquePathKey als key
    private Map<String, Boolean> currentlyRecording = new HashMap<>();

    //Speicherung der verschiedenen pathNames zu den paths, welche ein einzelner Spieler erstellen kann
    //nutzt UUID des Spielers als key
    private Map<String, ArrayList<String>> pathNamesPerPlayer = new HashMap<>();


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Location pLoc = p.getLocation();
            String pUUID = p.getUniqueId().toString();

            //Wechsel zwischen ausgewählten Modi
            if (args[1].equals("sample")) {
                addCoordinatesToPlayerpath(pLoc, pUUID, args[0]);
            }
            else if (args[1].equals("record")) {

            }
            else if (args[1].equals("delete")){
                deleteThisPlayerpath(p, args[0]);
            }
            else if (args[1].equals("create")){
                createThisPlayerpath(p, args[0]);
            }


            sender.sendMessage("erfolgreich ausgeführt");
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (!(sender instanceof Player)) {
            return completions;
        }

        Player p = ((Player) sender).getPlayer();

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
            if (pathNamesPerPlayer.get(p.getUniqueId().toString()).contains(args[0])){
                completions.add("sample");
                completions.add("record");
                completions.add("delete");
            }
            else {
                completions.add("create");
            }
        }
        else if (args[1].equals("record")) {
            if (args.length == 3) {
                if (currentlyRecording.get(getUniquePathKey(p.getUniqueId().toString(), args[0]))){
                    completions.add("stop");
                }
                else{
                    completions.add("start");

                    if (args.length == 4) {
                        completions.add("<threshold>");
                    }
                    else if (args.length == 5) {
                        completions.add("<sample rate>");
                    }
                }
            }
        }

        return completions;
    }



    private void addCoordinatesToPlayerpath(Location pLoc, String pUUID, String pathName) {
        ArrayList<Location> pPath = new ArrayList<>();
        String pName = Bukkit.getPlayer(UUID.fromString(pUUID)).getName();
        String uniquePathKey = getUniquePathKey(pUUID, pathName);

        if (collectionPlayerpaths.containsKey(uniquePathKey)) {
            pPath = collectionPlayerpaths.get(uniquePathKey);
            pPath.add(pLoc);
            collectionPlayerpaths.replace(uniquePathKey, pPath);

            addCoordinateToPolyline(pPath, pName, pathName);
        }
    }

    private void addCoordinateToPolyline(ArrayList<Location> coords, String pName, String pathName) {
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

            MonitorMC.INSTANCE.playerPaths.findPolyLineMarker(pName + ";" + pathName).setCornerLocations(coords_x, coords_y, coords_z);
        }
    }

    private void createThisPlayerpath(Player p, String pathName){
        /**
         * checken, ob path schon existiert! (in collection playerpath)
         *
         * leeren Eintrag in collectionPlayerpaths erstellen
         * path zu pathNamesPerPlayer hinzufügen
         * path zu currentlyRecording hinzufügen und auf FALSE setzen
         * PolyLineMarker erstellen
         */
        String pUUID = p.getUniqueId().toString();
        String uniquePathKey = getUniquePathKey(pUUID, pathName);
        String pName = p.getName();
        Location pLoc = p.getLocation();

        if (collectionPlayerpaths.containsKey(uniquePathKey)){
            return;
        }

        ArrayList<Location> temporary_collectionplayerPaths = new ArrayList<>();
        collectionPlayerpaths.put(uniquePathKey, temporary_collectionplayerPaths);

        ArrayList<String> temporaryPathNames = new ArrayList<>();
        if (pathNamesPerPlayer.containsKey(pUUID)) {
            temporaryPathNames = pathNamesPerPlayer.get(pUUID);
            temporaryPathNames.add(pathName);
            pathNamesPerPlayer.replace(pUUID, temporaryPathNames);
        }
        else{
            temporaryPathNames.add(pathName);
            pathNamesPerPlayer.put(pUUID, temporaryPathNames);
        }

        currentlyRecording.put(uniquePathKey, Boolean.FALSE);

        double[] coords_x = {pLoc.getX()};
        double[] coords_y = {pLoc.getY()};
        double[] coords_z = {pLoc.getZ()};
        MonitorMC.INSTANCE.playerPaths.createPolyLineMarker(pName + ";" + pathName, pathName, true, "world", coords_x, coords_y, coords_z, false);

    }

    private void deleteThisPlayerpath(Player p, String pathName){
        //Muss gelöscht werden:
        //Koordinaten des path aus collectionPlayerpaths
        //pathName des path aus pathNamesPerPlayer für entsprechenden Spieler
        //Eintrag aus currentlyRecording Liste für entsprechenden Marker
        //PolyLine im Markerset
        String pName = p.getName();
        if (MonitorMC.INSTANCE.playerPaths.findPolyLineMarker(pName + ";" + pathName) == null){
            return;
        }

        String uniquePathKey = getUniquePathKey(p.getUniqueId().toString(), pathName);
        collectionPlayerpaths.remove(uniquePathKey);

        String pUUID = p.getUniqueId().toString();
        ArrayList<String> thosePathNames = pathNamesPerPlayer.get(pUUID);
        thosePathNames.remove(pathName);
        pathNamesPerPlayer.replace(pUUID, thosePathNames);

        currentlyRecording.remove(uniquePathKey);

        MonitorMC.INSTANCE.playerPaths.findPolyLineMarker(pName + ";" + pathName).deleteMarker();

    }

    private void startRecording(String uniquePathKey, int threshold, int sampleRate){

    }

    //Dieser UniquePathKey wird genutzt, um den Path in collectionPlayerpaths eindeutig zu identifizieren
    private String getUniquePathKey(String pUUID, String pathName){
        return pUUID + pathName;
    }


}