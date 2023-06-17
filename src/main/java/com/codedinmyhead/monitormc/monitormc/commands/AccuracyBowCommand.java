package com.codedinmyhead.monitormc.monitormc.commands;

import com.codedinmyhead.monitormc.monitormc.MonitorMC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AccuracyBowCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            p.getInventory().addItem(MonitorMC.INSTANCE.accuracyBow);
            p.sendMessage("Obtained Accuracy Bow! Shot at " + MonitorMC.INSTANCE.targetBlockMaterial.name() + " to track your accuracy!");
        }
        return false;
    }
}
