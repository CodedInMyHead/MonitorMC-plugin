package com.codedinmyhead.monitormc.monitormc.commands;

import com.codedinmyhead.monitormc.monitormc.MonitorMC;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerpathCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args){
        if (sender instanceof Player){
            Player p = (Player) sender;
            Location pLoc = p.getLocation();
            UUID pUUID = p.getUniqueId();

            MonitorMC.INSTANCE.addCoordinatesToPlayerpath(pLoc, pUUID);

            sender.sendMessage("Position gespeichert");
        }

        return false;
    }
}
