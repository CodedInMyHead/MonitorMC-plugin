package com.codedinmyhead.monitormc.monitormc.gui;

import com.codedinmyhead.monitormc.monitormc.commands.TopThreeCommand;
import com.codedinmyhead.monitormc.monitormc.listeners.common.ActivatedListeners;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TopThreeGUI implements Listener {
    private final Inventory inventory;

    private int getSize() {
        int extra = ((ActivatedListeners.values().length / 9) + 1) * 9;
        return 27 + extra;
    }
    public TopThreeGUI() {
        inventory = Bukkit.createInventory(null, getSize(), "Leaderboards");
        defaultScreen();
    }

    public void defaultScreen() {
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
        final TopThreeGUI gui = TopThreeCommand.inventories.get(e.getWhoClicked().getUniqueId());
        if (gui == null) return;
        if (!e.getInventory().equals(gui)) return;
        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        leaderboardScreen(e);
    }

    public void leaderboardScreen(final InventoryClickEvent e) {

        MetricService service = MetricService.getInstance();

        inventory.setItem(4, createGuiItem(Material.OAK_SIGN, "§6"+e.getCurrentItem().displayName(), "§8Below you can see the top 3 players", "in this category"));
        inventory.setItem(10, createGuiItem(Material.IRON_BLOCK, "§6"+e.getCurrentItem().displayName(), "§8Below you can see the top 3 players", "in this category"));
        inventory.setItem(12, createGuiItem(Material.GOLD_BLOCK, "§6"+e.getCurrentItem().displayName(), "§8Below you can see the top 3 players", "in this category"));
        inventory.setItem(14, createGuiItem(Material.COPPER_BLOCK, "§6"+e.getCurrentItem().displayName(), "§8Below you can see the top 3 players", "in this category"));
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        final TopThreeGUI gui = TopThreeCommand.inventories.get(e.getWhoClicked().getUniqueId());
        if (gui == null) return;
        if (e.getInventory().equals(gui)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        final UUID uuid = e.getPlayer().getUniqueId();
        final TopThreeGUI gui = TopThreeCommand.inventories.get(uuid);
        if (gui == null) return;
        TopThreeCommand.inventories.remove(uuid);
    }
}
