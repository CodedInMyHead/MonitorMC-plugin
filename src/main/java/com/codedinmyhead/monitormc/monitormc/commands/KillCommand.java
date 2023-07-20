package com.codedinmyhead.monitormc.monitormc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class KillCommand implements CommandExecutor {

    private HashMap<String, Integer> killCounts;

    public KillCommand(HashMap<String, Integer> killCounts) {
        this.killCounts = killCounts;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerName = player.getName();
            int kills = killCounts.getOrDefault(playerName, 0);
            player.sendMessage("Your kill count: " + kills);
        } else {
            sender.sendMessage("This command can only be used by players.");
        }
        return true;
    }
}
