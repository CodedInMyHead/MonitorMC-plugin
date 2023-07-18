package com.codedinmyhead.monitormc.monitormc.commands;

import com.codedinmyhead.monitormc.monitormc.MonitorMC;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;
import org.jetbrains.annotations.NotNull;

public class WaypointSet implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        Player p = (Player) sender;
        String playerName = p.getName();
        MarkerAPI markerAPI = MonitorMC.INSTANCE.markerAPI;
        MarkerSet helge = markerAPI.getMarkerSet(playerName);
        if(helge == null) {
            MarkerSet set = markerAPI.createMarkerSet(playerName, "Custom Waypoints", null, false);
        }
        findWaypoint(helge,args[0],args,markerAPI,sender);
        return true;
    }
    public boolean findWaypoint (MarkerSet set, String waypoint, String[] args, MarkerAPI markerAPI, CommandSender sender){
        Marker bob = set.findMarker(waypoint);
        if(bob != null){
            sender.sendMessage(bob.getLabel() + ": " + bob.getX() + " " + bob.getY());
        }
        if(bob == null){
            sender.sendMessage("Waypoint doesn't exist. Create one by using the command /waypoint create <name> or /waypoint create <name> <x> <y>");
        }
        if(args.length == 0) {
            sender.sendMessage("Error: Please enter a valid Command.If u need help type: /waypoint help");
        }
        String name = args[0];
        if(name == "create"){
            if(args.length == 2){
                setWaypointsWithoutCord(markerAPI,set,args[1],sender);
                sender.sendMessage("Waypoint set");
            }
            else if(args.length == 4){
                setWaypointWithCord(markerAPI,set,args);
                sender.sendMessage("Waypoint set");
            }
            else{
                sender.sendMessage("Error: Please enter a valid Command.If u need help type: /waypoint help");
                return false;
            }
        }
        if(name == "help"){
            sender.sendMessage("/waypoint create <name> (creates the waypoint of the current location) \n/waypoint create <name> <x> <y> (creates custom waypoint on the map) \n/waypoint <name> (returns the name of the waypoints with the cords)");

        }

        return true;
    }

    public void setWaypointWithCord (MarkerAPI markerAPI, MarkerSet set, String[] args) {

        MarkerIcon icon = markerAPI.getMarkerIcon("default");

        double x = Double.parseDouble(args[2]);
        double y = Double.parseDouble(args[3]);
        String htmlLabel = "<div>" + args[1] + "</div>";

        Marker marker = set.createMarker(args[1], htmlLabel, true,
                "world", x, 20, y, icon, false);

    }
    public void setWaypointsWithoutCord (MarkerAPI markerAPI, MarkerSet set, String name, CommandSender sender){

        MarkerIcon icon = markerAPI.getMarkerIcon("default");

        Player p = (Player) sender;
        Location l = p.getLocation();

        double x = l.getBlockX();
        double y = l.getBlockY();
        String htmlLabel = "<div>" + name + "</div>";

        Marker marker = set.createMarker(name, htmlLabel, true,
                "world", x, 20, y, icon, false);



// ole approved
    }
}
