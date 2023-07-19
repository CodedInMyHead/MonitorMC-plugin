package com.codedinmyhead.monitormc.monitormc.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.block.Skull;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.units.qual.A;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsGUI implements Listener {
    private final Inventory inv;

    private Player p;

    public void setP(Player p) {
        this.p = p;
    }

    public static final Map<UUID, StatsGUI> statsMap = new HashMap<>();

    public StatsGUI() {
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 9*4, "Your Statistics");
        // Put the items into the inventory
        initializeFirstPage(p);
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
        AtomicInteger i = new AtomicInteger();
        getMobKills(getMobs(), p).forEach((k,v) -> {
            inv.setItem(i.get(), (createGuiItem(k.getName(), v)));
            if (i.get()%9 == 0) {i.set(i.getAndIncrement());}else{i.getAndAdd(2);}
        });
    }

    public List<EntityType> getMobs() {
        ArrayList<EntityType> mobs = new ArrayList<>();
        for(EntityType entity : EntityType.values()) {
            assert entity.getEntityClass() != null;
            if(entity.getEntityClass().isAssignableFrom(Monster.class)) {
                mobs.add(entity);
            }
        }
        return mobs;
    }

    public Map<EntityType, Integer> getMobKills(List<EntityType> mobs, Player p) {
        Map<EntityType, Integer> mobKills = new HashMap<>();

        for (EntityType mob : mobs) {
            mobKills.put(mob, p.getStatistic(Statistic.KILL_ENTITY, mob));
        }

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

    protected ItemStack createGuiItem(final String entityName, final int kills) {
        String material = "";

        final ItemStack item = new ItemStack(Material.LEGACY_SKULL_ITEM, 1);
//                Objects.requireNonNull(Material.getMaterial(material)), 1

        final ItemMeta meta = item.getItemMeta();

        final SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        skullMeta.setOwner("Rypex");

        // Set the name of the
        String name = String.format("§9%s Kills: %d", entityName, kills);
        meta.setDisplayName(name);

        item.setItemMeta(meta);

        return item;
    }

    public Map<UUID, EntityType> playerHeads(List<EntityType> mobs) {
        Map<UUID, EntityType> heads = new HashMap<>();



        return heads;
    }

    public OfflinePlayer getPlayerHead(Map<UUID, EntityType> playerHeads) {
        OfflinePlayer player = (OfflinePlayer) p;

        return player;
    }

    protected String createLore(final Material material, final Player p) {
        String lore = "";
        switch (material) {
            case DIAMOND_SWORD:
                int playerKills = p.getStatistic(Statistic.PLAYER_KILLS);
                int mobKills = p.getStatistic(Statistic.MOB_KILLS);
                lore = String.format("§9Player Kills: §b%d%n§9Mob Kills: %d (\033[3m§bClick §9to see all Mob specific kills\033[3m)", playerKills, mobKills);
                break;
            case SKELETON_SKULL:
                int totalDeaths = p.getStatistic(Statistic.DEATHS);
                lore = String.format("§9Total Deaths: §b%d", totalDeaths);
                break;
            case LEATHER_BOOTS:
                int distanceByFoot = p.getStatistic(Statistic.WALK_ONE_CM)
                        + p.getStatistic(Statistic.CROUCH_ONE_CM)
                        + p.getStatistic(Statistic.SPRINT_ONE_CM);
                lore = String.format("$9Distance travelled by foot: §b%d", distanceByFoot);
                break;
            default:
                lore = "§cNo lore yet";
                break;
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

        // Using slots click is the best option for your inventory click's
        p.sendMessage("You clicked at slot " + e.getRawSlot());

        if (e.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)) {
            inv.clear();
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

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        StatsGUI gui = statsMap.get(e.getPlayer().getUniqueId());
        if (gui == null) {
            return;
        }
        statsMap.remove(e.getPlayer().getUniqueId());
    }

    public ItemStack getClicketItem(InventoryClickEvent e) {
        return e.getCurrentItem();
    }
}
