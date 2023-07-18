package com.codedinmyhead.monitormc.monitormc.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class StatsGUI implements Listener {
    private final Inventory inv;

    public StatsGUI() {
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 36, "Default Stats");

        // Put the items into the inventory
//        initializeFirstPage();
    }

    // You can call this whenever you want to put the items in
    public void initializeFirstPage(Player p) {
        inv.setItem(0, (createGuiItem(Material.SKELETON_SKULL, "§cDeaths", p)));
        inv.setItem(2, (createGuiItem(Material.DIAMOND_SWORD, "§aKills", p)));
        inv.setItem(4, (createGuiItem(Material.COAL, "", p)));
        inv.setItem(6, (createGuiItem(Material.COAL, "", p)));
        inv.setItem(8, (createGuiItem(Material.COAL, "", p)));
    }

    public void initializeMobKills(Player p) {
        int i = 0;
        for (EntityType mob : getMobs()) {
            String mobName = mob.getEntityClass().getName();
            String itemName = mobName + "_head";
            inv.setItem(i, (createGuiItem(Material.getMaterial(itemName), "", p)));
        }
    }

    public ArrayList<EntityType> getMobs() {
        ArrayList<EntityType> mobs = new ArrayList<>();
        for(EntityType entity : EntityType.values()) {
            assert entity.getEntityClass() != null;
            if(entity.getEntityClass().isAssignableFrom(Monster.class)) {
                mobs.add(entity);
            }
        }
        return mobs;
    }

    public HashMap<EntityType, Integer> getMobKills(ArrayList<EntityType> mobs, Player p) {
        HashMap<EntityType, Integer> mobKills = new HashMap<>();



        return mobKills;
    }

    // Nice little method to create a gui item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, final String name, Player p) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(createLore(material, p)));

        item.setItemMeta(meta);

        return item;
    }

    protected String createLore(final Material material, final Player p) {
        String lore = "";
        switch (material) {
            case DIAMOND_SWORD:
                int playerKills = p.getStatistic(Statistic.PLAYER_KILLS);
                lore = String.format("§9Player Kills: §b%2d%n§bClick §9to see all Mob kills", playerKills);
                break;
            case SKELETON_SKULL:
                int totalDeaths = p.getStatistic(Statistic.DEATHS);
                lore = String.format("§9Total Deaths: §b%2d", totalDeaths);
                break;
            case LEATHER_BOOTS:
                int distanceByFoot = p.getStatistic(Statistic.WALK_ONE_CM)
                        + p.getStatistic(Statistic.CROUCH_ONE_CM)
                        + p.getStatistic(Statistic.SPRINT_ONE_CM);
                lore = String.format("$9Distance travelled by foot: §b%2d", distanceByFoot);
        }
        return lore;
    }

    // You can open the inventory with this
    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();

        // Using slots click is a best option for your inventory click's
        p.sendMessage("You clicked at slot " + e.getRawSlot());

        if (e.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)) {
            initializeMobKills(p);
        }
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }

    public ItemStack getClicketItem(InventoryClickEvent e) {
        return e.getCurrentItem();
    }
}
