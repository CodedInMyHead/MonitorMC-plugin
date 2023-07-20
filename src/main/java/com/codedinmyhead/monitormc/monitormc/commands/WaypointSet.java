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
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WaypointSet implements CommandExecutor, TabCompleter {
    private final MarkerAPI markerAPI = MonitorMC.INSTANCE.markerAPI;


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            setMessage("This command can only be executed by players.",ChatColor.RED,sender);
            return true;
        }

        if (args.length < 1 || args.length > 5) {
            setMessage("Error: Invalid Input. If you need help type /waypoint help",ChatColor.RED,sender);
            return true;
        }
        createWaypoint(args, sender);

        return true;
    }



    public void createWaypoint( String[] args, CommandSender sender) {

        Player player = (Player) sender;
        MarkerSet helge = markerAPI.getMarkerSet(player.getName());
        String waypointName = player.getName() + "'s Waypoints";
        if (helge == null) {
            helge = markerAPI.createMarkerSet(player.getName(), waypointName, null, false);
        }

        String waypoint = args[0].toLowerCase();

        if (waypoint.equals("create")) {
            if (args.length == 2 || args.length == 3) {
                setWaypointsWithoutCord(helge, args[1], player, args);
                setMessage("Waypoint set",ChatColor.GREEN,sender);
            } else if (args.length == 4 || args.length == 5) {
                try {
                    setWaypointWithCord(helge, args, sender);
                    setMessage("Waypoint set",ChatColor.GREEN,sender);
                } catch (NumberFormatException e) {
                    setMessage("Invalid input: Please try again with the right input",ChatColor.RED,sender);
                    }
            } else {
                setMessage("Error: Invalid Input. If you need help type /waypoint help",ChatColor.RED,sender);
            }
        } else if (waypoint.equals("help")) {
            setMessageConCat("/waypoint create <name>- \ncreates the waypoint at the current location- \n/waypoint create <name> <x> <y>- \ncreates custom waypoint on the map- \n/waypoint <name>- \nreturns the name of the waypoints with the cords- \n/waypoint create <name> <x> <y> <iconName>- \nsets the waypoint with a custom icon and cords- \n/waypoint create <name> <iconName>- \ncreates Waypoint with a custom icon at the current location- \n/waypoint icon- \nreturns all the available icons", sender);
        } else if (waypoint.equals("icon")) {
            showIcon(sender);
        } else {
            findWaypoint(helge, waypoint, sender);
        }
    }


    public void findWaypoint(MarkerSet set, String waypoint, CommandSender sender) {
        Marker bob = set.findMarker(waypoint);
        if (bob != null) {
            String name = bob.getLabel().replace("<div>", "").replace("</div>", "");
            sender.sendMessage(name + ": " + bob.getX() + " " + bob.getZ());
        } else {
            setMessage("Error: \"Waypoint doesn't exist. Create one by using the command /waypoint create <name> or /waypoint create <name> <x> <y>\"",ChatColor.RED,sender);
        }
    }



// ... (your imports and class definition)

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
            completions.add("icon");
            completions.add("help");
            List<String> names = new ArrayList<>();
            for (Marker marker : markerSet.getMarkers()) {
                names.add(marker.getLabel().replace("<div>", "").replace("</div>", ""));
            }
            completions.addAll(names);
        } else if (isNameTrue(args) && args[0].equals("create")) {
            completions.add("<name>");

        } else if (isCordsWithIconsTrue(args) && args[0].equals("create")) {

            for (Icon value : Icon.values()) {
                enumMap.put(value.name().toLowerCase(), value);
                completions.add(value.name().toLowerCase());
            }
        } else if (isCordsTrue(args) && args[0].equals("create")) {
            completions.add("<x> <y>");

        }

        return completions;
    }


    public void setWaypointWithCord(MarkerSet set, String[] args, CommandSender sender) throws NumberFormatException {
        List<String> names = new ArrayList<>();
        for (Marker marker : set.getMarkers()) {
            names.add(marker.getLabel().replace("<div>", "").replace("</div>", ""));

        }
        String iconName = "";

        if (args.length == 4) {
            iconName = "pin";
        } else if(args.length == 5) {
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
        String tempname = args[1];
        for (int i = 0; i < names.size(); i++) {
            if(tempname.equals(names.get(i))){
                setMessage("Error: Name exists, please try again",ChatColor.RED, sender);
                throw new NumberFormatException();

            }

        }
        String htmlLabel = "<div>" + tempname + "</div>";
        set.createMarker(args[1], htmlLabel, true, "world", x, 20, y, icon, false);
    }

    public void setWaypointsWithoutCord(MarkerSet set, String name, CommandSender sender, String[] args) {
        List<String> names = new ArrayList<>();
        for (Marker marker : set.getMarkers()) {
            names.add(marker.getLabel().replace("<div>", "").replace("</div>", ""));

        }

        String iconName = "";

        if (args.length == 2) {
            iconName = "pin";
        } else if (args.length == 3){
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
        String tempname = args[1];
        for (int i = 0; i < names.size(); i++) {
            if(tempname.equals(names.get(i))){
                setMessage("Error: Name exists, please try again",ChatColor.RED, sender);
                throw new NumberFormatException();

            }

        }
        String htmlLabel = "<div>" + name + "</div>";
        set.createMarker(name, htmlLabel, true, "world", x, 20, z, icon, false);
    }

    public void showIcon(CommandSender sender) {
        Player p = (Player) sender;

        List<String> list = new ArrayList<>();
        list.add("The available Icons:");
        for (Icon value : Icon.values()) {
            list.add(value.name().toLowerCase());
        }
        ChatColor[] rainbowColors = {
                ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW,
                ChatColor.GREEN, ChatColor.AQUA, ChatColor.BLUE,
                ChatColor.LIGHT_PURPLE
        };
        StringBuilder message = new StringBuilder();;
        for (int i = 0; i < list.size(); i++) {
            ChatColor color = rainbowColors[i % rainbowColors.length];
            message.append(color).append(list.get(i)).append("\n");
        }
        p.sendMessage(message.toString());
    }

    public boolean isNameTrue(String[] args) { //name
        return args.length == 2 && args[0].equalsIgnoreCase("create");
    }

    public boolean isCordsTrue(String[] args) { // name cords
        return args.length == 3 && args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase(args[1]);
    }

    public boolean isCordsWithIconsTrue(String[] args) { //name cords icon
        return args.length == 5 && args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase(args[1]) && args[2].equalsIgnoreCase(args[2])&& args[3].equalsIgnoreCase(args[3]);
    }
    public void setMessage(String msg, ChatColor color, CommandSender sender){

        Player player = (Player) sender;
        TextComponent suggestionComponent = new TextComponent(msg);
        suggestionComponent.setColor(color.asBungee());
        player.spigot().sendMessage(suggestionComponent);
    }
    public void setMessageConCat(String msg, CommandSender sender){
        Player player = (Player) sender;
        String[] mesaga = msg.split("-");
        ChatColor blue = ChatColor.DARK_AQUA;
        ChatColor blueButLighter = ChatColor.AQUA;
        StringBuilder message = new StringBuilder();
        List<String> words = Arrays.asList(mesaga);

        for (int i = 0; i < words.size(); i++) {
            message.append(i % 2 == 0 ? blueButLighter : blue).append(words.get(i)).append(" ");
        }

        player.sendMessage(message.toString());
    }
}