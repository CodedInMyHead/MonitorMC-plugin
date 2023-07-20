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
import org.dynmap.markers.MarkerSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerpathCommand implements CommandExecutor, TabCompleter {

    //Speicherung der playerpath-Koordinaten:
    //ArrayList enthält 3 ArrayLists für koordinaten -> je eine für x, y, z
    //Position 0 -> für x; Position 1 -> für y; Position 2 -> für z
    private Map<String, ArrayList<Location>> collectionPlayerpaths = new HashMap();


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Location pLoc = p.getLocation();
            String pUUID = p.getUniqueId().toString();

//            MonitorMC.INSTANCE.addCoordinatesToPlayerpath(pLoc, pUUID);
            addCoordinatesToPlayerpath(pLoc, pUUID);


            sender.sendMessage("Position gespeichert. Argumente: " + args[0]);
        }

        return false;
    }

    private void addCoordinatesToPlayerpath(Location pLoc, String pUUID) {
        ArrayList<Location> pPath = new ArrayList<>();
        String pName = Bukkit.getPlayer(UUID.fromString(pUUID)).getName();

        if (collectionPlayerpaths.containsKey(pUUID)) {
            pPath = collectionPlayerpaths.get(pUUID);
            pPath.add(pLoc);
            collectionPlayerpaths.replace(pUUID, pPath);

            //ändere entsprechenden polyLineMarker
            //erstelle PolyLineMarker wenn es erst zweite Koordinate ist
            //skip, wenn es erst erste Koordinate ist
            int coordLength = pPath.size();
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
            pPath.add(pLoc);
            collectionPlayerpaths.put(pUUID, pPath);
        }
    }

    private void addCoordinateToPolyline(ArrayList<Location> coords, String pName, boolean createNew) {
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
                MonitorMC.INSTANCE.playerPaths.createPolyLineMarker(pName, "just a test label", true, "world", coords_x, coords_y, coords_z, false);
            } else {
                MonitorMC.INSTANCE.playerPaths.findPolyLineMarker(pName).setCornerLocations(coords_x, coords_y, coords_z);
            }

        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (!(sender instanceof Player)) {
            return completions;
        }

        if (args.length == 1) {
            completions.add("<name>");
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
}