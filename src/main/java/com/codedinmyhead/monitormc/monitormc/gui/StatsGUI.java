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

    public static final Map<UUID, StatsGUI> statsMap = new HashMap<>();

    public StatsGUI(Player p) {
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 9*6, "Your Statistics");
        // Put the items into the inventory

        if (p != null) {
            initializeFirstPage(p);
        }
    }

    // You can call this whenever you want to put the items in
    public void initializeFirstPage(Player p) {
        AtomicInteger n = new AtomicInteger();
        if (statsItems().size() > 9*5/2){
            inv.setItem(0, createGuiItem(Material.PAPER, "BACK", null));
            inv.setItem(8, createGuiItem(Material.PAPER, "NEXT", null));
        }
        n.set(10);
        statsItems().forEach((k,v) -> {
            inv.setItem(n.get(), createGuiItem(k, v, p));
            n.getAndAdd(2);
        });
    }

    public void reinitializeFirstPage(Player p, Inventory i) {
        AtomicInteger n = new AtomicInteger();
        n.set(10);
        if (statsItems().size() > 9*5/2){
            i.setItem(0, createGuiItem(Material.PAPER, "BACK", null));
            i.setItem(8, createGuiItem(Material.PAPER, "NEXT", null));
        }
        statsItems().forEach((k,v) -> {
            i.setItem(n.get(), createGuiItem(k, v, p));
            n.getAndAdd(2);
        });
    }

    public Map<Material, String> statsItems(){
        Map<Material, String> items = new LinkedHashMap<>();
        items.put(Material.DIAMOND_SWORD, "§dDeaths");
        items.put(Material.SKELETON_SKULL, "§dKills");
        items.put(Material.ELYTRA, "§dDistance Travelled");
        items.put(Material.COAL, "not yet");
        return items;
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

    protected List<String> createLore(final Material material, final Player p) {
        List<String> lore = new ArrayList<>();
        switch (material) {
            case PAPER -> {
                lore = null;
            }
            case GRAY_STAINED_GLASS_PANE -> {
                lore = null;
            }
            case DIAMOND_SWORD -> {
                int playerKills = p.getStatistic(Statistic.PLAYER_KILLS);
                int mobKills = p.getStatistic(Statistic.MOB_KILLS);
                lore.add(String.format("§1Player Kills: §b%d", playerKills));
                lore.add(String.format("§1Mob Kills: %d (§bClick §1to see all Mob specific kills)", mobKills));
            }
            case SKELETON_SKULL -> {
                int totalDeaths = p.getStatistic(Statistic.DEATHS);
                lore.add(String.format("§1Total Deaths: §b%d", totalDeaths));
            }
            case ELYTRA -> {
                int distanceByFoot = p.getStatistic(Statistic.WALK_ONE_CM)
                        + p.getStatistic(Statistic.CROUCH_ONE_CM)
                        + p.getStatistic(Statistic.SPRINT_ONE_CM);
                int totalDistance = 0;
                for (Statistic s : Statistic.values()){
                    if (s.name().contains("_ONE_CM")){
                        totalDistance += p.getStatistic(s);
                    }
                }
                lore.add(String.format("§1Total: §b%d §1Blocks", totalDistance));
                lore.add(String.format("§1By foot: §b%d §1Blocks", distanceByFoot));
            }
            default -> lore.add("§cNo lore yet");
        }
        return lore;
    }

    public void InitializeMobPage(Player p, Inventory i, int page) {
        i.setItem(0, createGuiItem(Material.PAPER, "BACK", null));
        for (int k = 1; k<8; k++){
            i.setItem(k, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, null, null));
        }
        if (mobs().size() - (22*(page-1)) > 22){
            i.setItem(8, createGuiItem(Material.PAPER, "NEXT", null));
        }
        AtomicInteger n = new AtomicInteger();
        n.set(10);
        for (int j = (page-1)*22; j < page*22 && j < mobs().size(); j++) {
            EntityType ent = mobs().get(j);
            int kills = getMobKills(mobs(), p).get(mobs().get(j));
            i.setItem(n.get(), (createGuiItem(ent, kills)));
            n.getAndAdd(2);
        }
    }

    public int mobPage = 1;

    public Map<EntityType, Material> mobHeads() {
        Map<EntityType, Material> heads = new LinkedHashMap<>();

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

    public List<EntityType> mobs() {
        List<EntityType> mobs = new ArrayList<>();

        mobHeads().forEach((k,v) -> mobs.add(k));
        return mobs;
    }

    public Map<EntityType, Integer> getMobKills(List<EntityType> mobs, Player p) {
        Map<EntityType, Integer> mobKills = new HashMap<>();

        for (EntityType mob : mobs) {
            mobKills.put(mob, p.getStatistic(Statistic.KILL_ENTITY, mob));
        }

        return mobKills;
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



    // You can open the inventory with this
    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        StatsGUI gui = statsMap.get(e.getWhoClicked().getUniqueId());
        if (gui == null || !e.getInventory().equals(gui.inv)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player player = (Player) e.getWhoClicked();


        if (e.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)) {
            statsMap.get(e.getWhoClicked().getUniqueId()).inv.clear();
            InitializeMobPage(player, statsMap.get(e.getWhoClicked().getUniqueId()).inv, mobPage = 1);
        }
        if (e.getRawSlot() == 8 ){
            statsMap.get(e.getWhoClicked().getUniqueId()).inv.clear();
            InitializeMobPage(player, statsMap.get(e.getWhoClicked().getUniqueId()).inv, ++mobPage);
        }
        if (e.getRawSlot() == 0 ){
            if(mobPage > 0)
                mobPage -= 1;
            statsMap.get(e.getWhoClicked().getUniqueId()).inv.clear();
            if (mobPage < 1){
                reinitializeFirstPage(player, statsMap.get(e.getWhoClicked().getUniqueId()).inv);
            } else {
                InitializeMobPage(player, statsMap.get(e.getWhoClicked().getUniqueId()).inv, mobPage);
            }

        }
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        StatsGUI gui = statsMap.get(e.getWhoClicked().getUniqueId());
        if (gui == null || e.getInventory().equals(gui.inv)) {
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
