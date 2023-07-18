package com.codedinmyhead.monitormc.monitormc.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DashboardGUI implements Listener {

    private final Inventory inv;

    //TODO: Change lol
    private final String GRAFANA_URL = "localhost:3000";
    private HashMap<Integer, UrlItem> urlMap = new HashMap<>();

    public DashboardGUI() {
        inv = Bukkit.createInventory(null, 9*4, "Dashboards");

        initializeItemsAndUrlMap();
    }

    public void initializeItemsAndUrlMap() {
        urlMap.put(0, new UrlItem("deaths/all", Material.CREEPER_HEAD, "Deaths", "lore1", "lore2"));
        urlMap.put(2, new UrlItem("dashboard/new?orgId=1&editPanel=1", Material.ZOMBIE_HEAD, "Test Dashboard"));

        urlMap.entrySet().forEach(e -> {
            UrlItem ui = e.getValue();
            inv.setItem(e.getKey(), createGuiItem(ui.itemMaterial, ui.itemName, ui.lore));
        });
    }


    protected ItemStack createGuiItem(final Material material, final String name, final List<String> lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();

        int slot = e.getRawSlot();
        if(urlMap.containsKey(slot)) {
            openUrl(p, urlMap.get(slot).url);
            p.closeInventory();
        }

    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }

    //TODO send link with a book (needs packets)
    private void openUrl(Player p, String url) {
        TextComponent t = Component.text("Click here to open the Dashboard!");
        t.clickEvent(ClickEvent.openUrl(url));
        p.sendMessage(t);
    }

    private class UrlItem {
        private Material itemMaterial;
        private String itemName;
        private List<String> lore;
        private String url;

        private UrlItem(String grafanaFilter, Material itemMaterial, String itemName, String... lore) {
            this.itemMaterial = itemMaterial;
            this.itemName = itemName;
            this.lore = Arrays.asList(lore);
            this.url = GRAFANA_URL + grafanaFilter;
        }
    }

}
