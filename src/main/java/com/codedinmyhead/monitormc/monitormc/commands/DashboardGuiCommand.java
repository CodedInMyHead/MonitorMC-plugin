package com.codedinmyhead.monitormc.monitormc.commands;

import com.codedinmyhead.monitormc.monitormc.MonitorMC;
import com.codedinmyhead.monitormc.monitormc.gui.DashboardGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardGuiCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only for Players!");
            return false;
        }

        Player p = (Player) commandSender;

        if(args.length == 1) {
            if("reload".equals(args[0])) {
                if(MonitorMC.INSTANCE.reloadCustomDashboardConfig()) {
                    p.sendMessage(Component.text("Config reloaded!").color(TextColor.color(19, 255, 53)));
                } else {
                    p.sendMessage(Component.text("Error while reloading the config! Check the console for more details").color(TextColor.color(255, 30, 33)));
                }

                return true;
            }
        }



        MonitorMC.INSTANCE.dashboardGUI.openInventory(p);


        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        completions.add("reload");
        return completions;
    }
}
