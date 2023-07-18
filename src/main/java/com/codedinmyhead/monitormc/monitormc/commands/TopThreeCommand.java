package com.codedinmyhead.monitormc.monitormc.commands;

import com.codedinmyhead.monitormc.monitormc.MonitorMC;
import com.codedinmyhead.monitormc.monitormc.gui.TopThreeGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TopThreeCommand implements CommandExecutor {

    public static final Map<UUID, TopThreeGUI> inventories = new HashMap<>();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            TopThreeGUI topThreeGUI = new TopThreeGUI();
            inventories.put(player.getUniqueId(), topThreeGUI);
            topThreeGUI.openInventory(player);
            return true;
        }
        return false;
    }
}
