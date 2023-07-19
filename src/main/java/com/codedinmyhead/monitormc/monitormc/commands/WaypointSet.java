package com.codedinmyhead.monitormc.monitormc.commands;

import com.codedinmyhead.monitormc.monitormc.MonitorMC;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaypointSet implements CommandExecutor, TabCompleter {
    private final MarkerAPI markerAPI = MonitorMC.INSTANCE.markerAPI;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;
        MarkerSet helge = markerAPI.getMarkerSet(player.getName());
        if (helge == null) {
            helge = markerAPI.createMarkerSet(player.getName(), player.getName(), null, false);
        }

        if (args.length < 1) {
            sender.sendMessage("Error: Invalid Input. If you need help type /waypoint help");
            return true;
        }

        String waypoint = args[0].toLowerCase();
        if (waypoint.equals("create")) {
            if (args.length == 2 || args.length == 3) {
                setWaypointsWithoutCord(helge, args[1], player, args);
                sender.sendMessage("Waypoint set");
            } else if (args.length == 4 || args.length == 5) {
                try {
                    setWaypointWithCord(helge, args);
                    sender.sendMessage("Waypoint set");
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid input. Please try again.");
                }
            } else {
                sender.sendMessage("Error: Invalid Input. If you need help type /waypoint help");
            }
        } else if (waypoint.equals("help")) {
            sender.sendMessage("/waypoint create <name>\ncreates the waypoint at the current location\n/waypoint create <name> <x> <y>\ncreates custom waypoint on the map\n/waypoint <name>\nreturns the name of the waypoints with the cords\n/waypoint create <name> <x> <y> <iconName>\nsets the waypoint with a custom icon and cords\n/waypoint create <name> <icon>\ncreates Waypoint with a custom icon at the current location\n/waypoint icon\nreturns all the available icons");
        } else if (waypoint.equals("icon")) {
            showIcon(sender);
        } else {
            findWaypoint(helge, waypoint, sender);
        }

        return true;
    }

    public void createWaypoint(MarkerSet set, String waypoint, String[] args, CommandSender sender) {
        String name = args[0];
        switch (name) {
            case "create":
                if (args.length == 2 || args.length == 3) {
                    setWaypointsWithoutCord(set, args[1], sender, args);
                    sender.sendMessage("Waypoint set");
                } else if (args.length == 4 || args.length == 5) {
                    try {
                        setWaypointWithCord(set, args);
                        sender.sendMessage("Waypoint set");
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Invagit lid input. Please try again.");
                    }
                } else {
                    sender.sendMessage("Error: Invalid Input. If you need help type /waypoint help");
                }
                break;
            case "help":
                sender.sendMessage("/waypoint create <name>\ncreates the waypoint at the current location\n/waypoint create <name> <x> <y>\ncreates custom waypoint on the map\n/waypoint <name>\nreturns the name of the waypoints with the cords\n/waypoint create <name> <x> <y> <iconName>\nsets the waypoint with a custom icon and cords\n/waypoint create <name> <icon>\ncreates Waypoint with a custom icon at the current location\n/waypoint icon\nreturns all the available icons");
                break;
            case "icon":
                showIcon(sender);
                break;
            default:
                findWaypoint(set, waypoint, sender);
        }
    }

    public void findWaypoint(MarkerSet set, String waypoint, CommandSender sender) {
        Marker bob = set.findMarker(waypoint);
        if (bob != null) {
            String name = bob.getLabel().replace("<div>", "").replace("</div>", "");
            sender.sendMessage(name + ": " + bob.getX() + " " + bob.getZ());
        } else {
            sender.sendMessage("Waypoint doesn't exist. Create one by using the command /waypoint create <name> or /waypoint create <name> <x> <y>");
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        Map<String, Icon> enumMap = new HashMap<>();
        if (!(sender instanceof Player)) {
            return completions;
        }

        Player player = (Player) sender;
        MarkerSet markerSet = markerAPI.getMarkerSet(player.getName());
        if (markerSet == null) {
            markerSet = markerAPI.createMarkerSet(player.getName(), player.getName(), null, false);
        }

        if (args.length == 1) {
            completions.add("create");
            completions.add("help");
            completions.add("icon");
            List<String> names = new ArrayList<>();
            for (Marker marker : markerSet.getMarkers()) {
                names.add(marker.getLabel());
                completions.add(marker.getLabel().replace("<div>", "").replace("</div>", ""));
            }
        } else if (isNameTrue(args)) {
            completions.add("<name>");
        } else if (isCordsWithIconsTrue(args)) {
            for (Icon value : Icon.values()) {
                enumMap.put(value.name(), value);
                completions.add(value.name());
            }
        } else if (isCordsTrue(args)) {
            for (Icon value : Icon.values()) {
                enumMap.put(value.name(), value);
                completions.add(value.name());
            }
            completions.add("<x> <y>");
        }

        return completions;
    }

    public void setWaypointWithCord(MarkerSet set, String[] args) throws NumberFormatException {
        String iconName = "";

        if (args.length == 4) {
            iconName = "pin";
        } else if(args.length == 3) {
            Map<String, Icon> enumMap = new HashMap<>();
            for (Icon value : Icon.values()) {
                enumMap.put(value.name(), value);
            }
            Icon matchingEnum = enumMap.get(args[4].toUpperCase());

            if (matchingEnum != null) {
                iconName = String.valueOf(matchingEnum);
            }
        }

        MarkerIcon icon = markerAPI.getMarkerIcon(iconName.toLowerCase());
        double x = Double.parseDouble(args[2]);
        double y = Double.parseDouble(args[3]);
        String htmlLabel = "<div>" + args[1] + "</div>";
        set.createMarker(args[1], htmlLabel, true, "world", x, 20, y, icon, false);
    }

    public void setWaypointsWithoutCord(MarkerSet set, String name, CommandSender sender, String[] args) {
        String iconName = "";

        if (args.length == 2) {
            iconName = "pin";
        } else {
            Map<String, Icon> enumMap = new HashMap<>();
            for (Icon value : Icon.values()) {
                enumMap.put(value.name(), value);
            }
            Icon matchingEnum = enumMap.get(args[2].toUpperCase());

            if (matchingEnum != null) {
                iconName = String.valueOf(matchingEnum);
            }
        }

        MarkerIcon icon = markerAPI.getMarkerIcon(iconName.toLowerCase());
        Player p = (Player) sender;
        Location l = p.getLocation();
        double x = l.getBlockX();
        double z = l.getBlockZ();
        String htmlLabel = "<div>" + name + "</div>";
        set.createMarker(name, htmlLabel, true, "world", x, 20, z, icon, false);
    }

    public void showIcon(CommandSender sender) {
        sender.sendMessage("The available Icons:\n");
        for (Icon value : Icon.values()) {
            sender.sendMessage(value.name() + "\n");
        }
    }

    public boolean isNameTrue(String[] args) { //name
        return args.length == 2 && args[0].equalsIgnoreCase(args[0]);
    }

    public boolean isCordsTrue(String[] args) { // name cords
        return args.length == 3 && args[0].equalsIgnoreCase(args[0]) && args[1].equalsIgnoreCase(args[1]);
    }

    public boolean isCordsWithIconsTrue(String[] args) {
        return args.length == 5 && args[0].equalsIgnoreCase("create");
    }

    public MarkerSet setSet(@NotNull CommandSender sender) {
        Player p = (Player) sender;
        String playerName = p.getName();
        MarkerSet helge = markerAPI.getMarkerSet(playerName);
        if (helge == null) {
            helge = markerAPI.createMarkerSet(playerName, playerName, null, false);
        }
        return helge;
    }
}