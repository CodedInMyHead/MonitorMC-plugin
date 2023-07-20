package com.codedinmyhead.monitormc.monitormc.listeners;

import com.codedinmyhead.monitormc.monitormc.commands.TopThreeCommand;
import com.codedinmyhead.monitormc.monitormc.common.Mode;
import com.codedinmyhead.monitormc.monitormc.gui.TopThreeGUI;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class LeaderboardGUIListener implements Listener {

    public Inventory getInventory(final UUID uuid) {
        final TopThreeGUI gui = TopThreeCommand.inventories.get(uuid);
        if (gui == null) return null;
        return gui.getInventory();
    }

    public TopThreeGUI getGUI(final UUID uuid) {
        final TopThreeGUI gui = TopThreeCommand.inventories.get(uuid);
        return gui;
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent e) {
        if (e.getInventory().equals(getInventory(e.getWhoClicked().getUniqueId()))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        final UUID uuid = e.getPlayer().getUniqueId();
        TopThreeCommand.inventories.remove(uuid);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {

        final Inventory inventory = getInventory(e.getWhoClicked().getUniqueId());

        if (!e.getInventory().equals(inventory)) return;
        e.setCancelled(true);
        final TopThreeGUI gui = getGUI(e.getWhoClicked().getUniqueId());


        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        if (clickedItem.getType() == Material.BARRIER) {
            if (gui.mode == Mode.DEFAULT) {
                gui.closeInventory(e.getWhoClicked());
            } else {
                inventory.clear();
                gui.defaultScreen(inventory);
            }
            return;
        }

        if (clickedItem.getType() == Material.COMPARATOR) {
            if (gui.mode == Mode.BEST) {
                gui.mode = Mode.WORST;
                gui.legacyMode = Mode.WORST;
                ItemMeta meta = clickedItem.getItemMeta();
                meta.setDisplayName("§2Current Mode: Worst");
                clickedItem.setItemMeta(meta);

            } else {
                gui.mode = Mode.BEST;
                gui.legacyMode = Mode.BEST;
                ItemMeta meta = clickedItem.getItemMeta();
                meta.setDisplayName("§2Current Mode: Best");
                clickedItem.setItemMeta(meta);
            }
        }

        if (clickedItem.getType() == Material.SUNFLOWER) {
            inventory.clear();
            gui.leaderboardScreen(e, inventory);
        }

        if (clickedItem.getType() != Material.OAK_SIGN && clickedItem.getType() != Material.BARRIER && clickedItem.getType() != Material.IRON_BLOCK && clickedItem.getType() != Material.GOLD_BLOCK && clickedItem.getType() != Material.COPPER_BLOCK && clickedItem.getType() != Material.COMPARATOR && clickedItem.getType() != Material.SUNFLOWER) {
            inventory.clear();
            gui.leaderboardScreen(e, inventory);
        }
    }
}
