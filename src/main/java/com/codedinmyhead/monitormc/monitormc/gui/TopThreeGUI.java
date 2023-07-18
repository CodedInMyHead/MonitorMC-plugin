package com.codedinmyhead.monitormc.monitormc.gui;

import com.codedinmyhead.monitormc.monitormc.listeners.common.ActivatedListeners;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class TopThreeGUI implements Listener {
    private final Inventory inventory;

    private int getSize() {
        int extra = ((ActivatedListeners.values().length / 9) + 1) * 9;
        return 18 + extra;
    }
    public TopThreeGUI() {
        inventory = Bukkit.createInventory(null, getSize(), "Leaderboards");
        initializeItems();
    }

    public void initializeItems() {
        // Index is starting at 0, so 4 is middle of a line (0-8)

        AtomicInteger position = new AtomicInteger(18);
        inventory.setItem(4, createGuiItem(Material.OAK_SIGN, "§6Leaderboards", "§8Below you can find all statistics", "§8that are collected.", "§8Click on one to see","§8the Top 3 Players in that Category."));
        Arrays.asList(ActivatedListeners.values()).stream().filter(entry -> entry.isTopThree()).forEach(enumEntry -> {
            inventory.setItem(position.get(), createGuiItem(enumEntry.getMaterial(), "§6" + enumEntry.getName(), Arrays.asList(enumEntry.getLore()).stream().map(element -> "§7" + element).toList().toArray(new String[0])));
            position.set(position.get() + 1);
        });
    }

    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return item;
    }

    public void openInventory(final HumanEntity entity) {
        entity.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        e.getWhoClicked().sendMessage("LOL");
        if (!e.getInventory().equals(inventory)) return;
        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();

    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
        }
    }
}
