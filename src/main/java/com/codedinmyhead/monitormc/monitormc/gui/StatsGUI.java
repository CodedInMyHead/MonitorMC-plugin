package com.codedinmyhead.monitormc.monitormc.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
            inv.setItem(i.get(), (createGuiItem(k, v)));
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
        meta.setLore(createLore(material, p));

        item.setItemMeta(meta);

        return item;
    }

    protected ItemStack createGuiItem(final EntityType entity, final int kills) {
        final ItemStack item = new ItemStack(mobHeads().get(entity), 1);
//                Objects.requireNonNull(Material.getMaterial(material)), 1

        final ItemMeta meta = item.getItemMeta();

        // Set the name of the
        String name = String.format("§9%s Kills: %d", entity.getName(), kills);
        meta.setDisplayName(name);

        item.setItemMeta(meta);

        return item;
    }

    public Map<EntityType, Material> mobHeads() {
        Map<EntityType, Material> heads = new HashMap<>();

        heads.put(EntityType.BLAZE, Material.BLAZE_ROD);
        heads.put(EntityType.CREEPER, Material.CREEPER_HEAD);
        heads.put(EntityType.DROWNED, Material.NAUTILUS_SHELL);
        heads.put(EntityType.ENDERMAN, Material.ENDER_PEARL);
        heads.put(EntityType.ENDER_DRAGON, Material.DRAGON_HEAD);
        heads.put(EntityType.ELDER_GUARDIAN, Material.SPONGE);
        heads.put(EntityType.EVOKER, Material.TOTEM_OF_UNDYING);
        heads.put(EntityType.GHAST, Material.GHAST_TEAR);
        heads.put(EntityType.GUARDIAN, Material.PRISMARINE_SHARD);
        heads.put(EntityType.HOGLIN, Material.WARPED_FUNGUS);
        heads.put(EntityType.HUSK, Material.DEAD_BUSH);
        heads.put(EntityType.MAGMA_CUBE, Material.MAGMA_CREAM);
        heads.put(EntityType.PHANTOM, Material.PHANTOM_MEMBRANE);
        heads.put(EntityType.PIGLIN, Material.PIGLIN_HEAD);
        heads.put(EntityType.PIGLIN_BRUTE, Material.GOLDEN_AXE);
        heads.put(EntityType.PILLAGER, Material.CROSSBOW);
        heads.put(EntityType.RAVAGER, Material.SADDLE);
        heads.put(EntityType.SHULKER, Material.SHULKER_BOX);
        heads.put(EntityType.SILVERFISH, Material.INFESTED_CRACKED_STONE_BRICKS);
        heads.put(EntityType.SKELETON, Material.SKELETON_SKULL);
        heads.put(EntityType.SLIME, Material.SLIME_BALL);
        heads.put(EntityType.STRAY, Material.TIPPED_ARROW);
        heads.put(EntityType.VEX, Material.IRON_SWORD);
        heads.put(EntityType.VINDICATOR, Material.EMERALD);
        heads.put(EntityType.WARDEN, Material.SCULK_CATALYST);
        heads.put(EntityType.WITCH, Material.SPLASH_POTION);
        heads.put(EntityType.WITHER, Material.NETHER_STAR);
        heads.put(EntityType.WITHER_SKELETON, Material.WITHER_SKELETON_SKULL);
        heads.put(EntityType.ZOGLIN, Material.PORKCHOP);
        heads.put(EntityType.ZOMBIE, Material.ZOMBIE_HEAD);
        heads.put(EntityType.ZOMBIFIED_PIGLIN, Material.GOLD_NUGGET);
        heads.put(EntityType.ZOMBIE_VILLAGER, Material.ROTTEN_FLESH);

        return heads;
    }

    protected List<String> createLore(final Material material, final Player p) {
        List<String> lore = new ArrayList<>();
        switch (material) {
            case DIAMOND_SWORD -> {
                int playerKills = p.getStatistic(Statistic.PLAYER_KILLS);
                int mobKills = p.getStatistic(Statistic.MOB_KILLS);
                lore.add(String.format("§9Player Kills: §b%d", playerKills));
                lore.add(String.format("§9Mob Kills: %d (\033[3m§bClick §9to see all Mob specific kills\033[3m)", mobKills));
            }
            case SKELETON_SKULL -> {
                int totalDeaths = p.getStatistic(Statistic.DEATHS);
                lore.add(String.format("§9Total Deaths: §b%d", totalDeaths));
            }
            case LEATHER_BOOTS -> {
                int distanceByFoot = p.getStatistic(Statistic.WALK_ONE_CM)
                        + p.getStatistic(Statistic.CROUCH_ONE_CM)
                        + p.getStatistic(Statistic.SPRINT_ONE_CM);
                lore.add(String.format("$9Distance travelled by foot: §b%d", distanceByFoot));
            }
            default -> lore.add("§cNo lore yet");
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

        final Player player = (Player) e.getWhoClicked();

        // Using slots click is the best option for your inventory click's
        p.sendMessage("You clicked at slot " + e.getRawSlot());

        if (e.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)) {
            inv.clear();
            initializeMobKills(player);
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

    public ItemStack getClickedItem(InventoryClickEvent e) {
        return e.getCurrentItem();
    }
}
