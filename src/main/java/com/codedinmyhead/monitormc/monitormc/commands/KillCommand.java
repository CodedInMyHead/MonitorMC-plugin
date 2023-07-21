package com.codedinmyhead.monitormc.monitormc.commands;

import com.codedinmyhead.monitormc.monitormc.listeners.ArrowKillListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;


public class KillCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int kills = ArrowKillListener.getKillCount(player);
            player.sendMessage("Your kill count: " + kills);
        } else {
            sender.sendMessage("This command can only be used by players.");
        }
        return true;
    }
}

