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

public class DefaultStats implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("This command can only be executed by a player");
            return false;
        }

        HashMap<Integer, Material> firstPage = new HashMap<>();
        firstPage.put(0, Material.SKELETON_SKULL);
        firstPage.put(2, Material.DIAMOND_SWORD);
        firstPage.put(4, Material.COAL);
        firstPage.put(6, Material.COAL);
        firstPage.put(8, Material.COAL);

        return true;
    }

    public void setInventory(CommandSender sender) {
        Player p = (Player) sender;
        StatsGUI gui = new StatsGUI();

        gui.initializeFirstPage(p);
        gui.openInventory(p);


    }
}
