package com.codedinmyhead.monitormc.monitormc.commands;

import com.codedinmyhead.monitormc.monitormc.gui.StatsGUI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DefaultStatsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("This command can only be executed by a player");
            return false;
        }
        setInventory(sender);
        return true;
    }

    public void setInventory(CommandSender sender) {
        Player p = (Player) sender;
        StatsGUI gui = new StatsGUI(p);
        StatsGUI.statsMap.put(((Player) sender).getUniqueId(), gui);
        gui.openInventory(p);
    }
}
