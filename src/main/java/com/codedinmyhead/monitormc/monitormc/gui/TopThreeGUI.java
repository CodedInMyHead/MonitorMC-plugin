package com.codedinmyhead.monitormc.monitormc.gui;

import com.codedinmyhead.monitormc.monitormc.commands.TopThreeCommand;
import com.codedinmyhead.monitormc.monitormc.common.Mode;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricService;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricsEnum;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
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
public class TopThreeGUI implements Listener {
    private final Inventory inventory;
    private Mode mode = Mode.DEFAULT;

    private final Map<Integer, MetricsEnum> enumMapping = new HashMap<>();

    private int getSize() {
        int extra = ((MetricsEnum.values().length / 7) + 1) * 9;
        return 36 + extra;
    }
    public TopThreeGUI() {
        inventory = Bukkit.createInventory(null, getSize(), "Leaderboards");
        defaultScreen();
    }

    public void defaultScreen() {
        defaultScreen(inventory);
    }

    public void defaultScreen(final Inventory inventory) {
        // Index is starting at 0, so 4 is middle of a line (0-8)
        mode = Mode.DEFAULT;

        AtomicInteger position = new AtomicInteger(18);
        inventory.setItem(4, createGuiItem(Material.OAK_SIGN, "§6Leaderboards", "§8Below you can find all statistics", "§8that are collected.", "§8Click on one to see","§8the stats for that Category."));
        inventory.setItem(getSize() - 9, createGuiItem(Material.BARRIER, "§cBack", "§8Click here to close", "§8this inventory."));

        Arrays.stream(MetricsEnum.values()).filter(MetricsEnum::isLeaderboard).forEach(enumEntry -> {
            if (position.get() % 9 == 8) {
                position.set(position.get() + 2);
            } else if (position.get() % 9 == 0) {
                position.set(position.get() + 1);
            }
            inventory.setItem(position.get(), createGuiItem(enumEntry.getMaterial(), "§6" + enumEntry.getName() ,
                    Arrays.stream(enumEntry.getLore())
                    .map(element -> "§7" + element)
                    .toList()
                    .toArray(new String[0])));
            enumMapping.put(position.get(), enumEntry);
            position.set(position.get() + 1);
        });
    }

    private ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return item;
    }

    public void closeInventory(final HumanEntity entity) {
        entity.closeInventory();
    }

    public void openInventory(final HumanEntity entity) {
        entity.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        final TopThreeGUI gui = TopThreeCommand.inventories.get(e.getWhoClicked().getUniqueId());
        if (gui == null) return;
        if (!e.getInventory().equals(gui.inventory)) return;
        e.setCancelled(true);


        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        if (clickedItem.getType() == Material.BARRIER) {
            if (mode == Mode.DEFAULT) {
                closeInventory(e.getWhoClicked());
            } else {
                gui.inventory.clear();
                defaultScreen(gui.inventory);
            }
            return;
        }

        if (clickedItem.getType() != Material.OAK_SIGN && clickedItem.getType() != Material.BARRIER && clickedItem.getType() != Material.IRON_BLOCK && clickedItem.getType() != Material.GOLD_BLOCK && clickedItem.getType() != Material.COPPER_BLOCK) {
            gui.inventory.clear();
            leaderboardScreen(e, gui.inventory);
        }
    }
    
    
    public void leaderboardScreen(final InventoryClickEvent e, final Inventory inventory) {
        mode = Mode.BEST;

        MetricService service = MetricService.getInstance();
        MetricsEnum metric = enumMapping.get(e.getSlot());
        Map<String, Integer> playerScores = service.getPlayerSpecificMetric(metric);

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(playerScores.entrySet());

        sortedEntries.sort((e1, e2) -> {
            int pointsComparison = e2.getValue().compareTo(e1.getValue()); // Descending order of points

            if (pointsComparison == 0) {
                return e1.getKey().compareTo(e2.getKey()); // Ascending order of names if points are the same
            }

            return pointsComparison;
        });

        int[] placesScores = new int[3];
        String[] placesNames = new String[3];

        sortedEntries.forEach(System.out::println);

        if (mode == Mode.BEST) {
            if (sortedEntries.size() > 0) {
                placesScores[0] = sortedEntries.get(0).getValue();
                placesNames[0] = sortedEntries.get(0).getKey();
            } else {
                placesNames[0] = "N/A";
            }
            if (sortedEntries.size() > 1) {
                placesScores[1] = sortedEntries.get(1).getValue();
                placesNames[1] = sortedEntries.get(1).getKey();
            } else {
                placesNames[1] = "N/A";
            }
            if (sortedEntries.size() > 2) {
                placesScores[2] = sortedEntries.get(2).getValue();
                placesNames[2] = sortedEntries.get(2).getKey();
            } else {
                placesNames[2] = "N/A";
            }
        } else if (mode == Mode.WORST){
            if (sortedEntries.size() > 0) {
                placesScores[0] = sortedEntries.get(sortedEntries.size()-1).getValue();
                placesNames[0] = sortedEntries.get(sortedEntries.size()-1).getKey();
            } else {
                placesNames[0] = "N/A";
            }
            if (sortedEntries.size() > 1) {
                placesScores[1] = sortedEntries.get(sortedEntries.size()-2).getValue();
                placesNames[1] = sortedEntries.get(sortedEntries.size()-2).getKey();
            } else {
                placesNames[1] = "N/A";
            }
            if (sortedEntries.size() > 2) {
                placesScores[2] = sortedEntries.get(sortedEntries.size()-3).getValue();
                placesNames[2] = sortedEntries.get(sortedEntries.size()-3).getKey();
            } else {
                placesNames[2] = "N/A";
            }
         } else {
            e.getWhoClicked().sendMessage("FATAL ERROR");
        }


        final String title = mode == Mode.BEST ? "top" : "bottom";
        final String scoreColor1 = placesScores[0] == 0 ? "§c" : "§2";
        final String scoreColor2 = placesScores[1] == 0 ? "§c" : "§2";
        final String scoreColor3 = placesScores[2] == 0 ? "§c" : "§2";

        inventory.setItem(4, createGuiItem(Material.OAK_SIGN, "§6"+"Leaderboards", "§8Below you can see the", "§8" + title + " 3 players in the category", "§8"+ metric.getName()));

        inventory.setItem(20, createGuiItem(Material.IRON_BLOCK, "§a"+placesNames[1], "§72nd Place", "§8Score: " + scoreColor2 + placesScores[1]));
        inventory.setItem(22, createGuiItem(Material.GOLD_BLOCK, "§a"+placesNames[0], "§61st Place", "§8Score: " + scoreColor1 + placesScores[0]));
        inventory.setItem(24, createGuiItem(Material.COPPER_BLOCK, "§a"+placesNames[2], "§83rd Place", "§8Score: " + scoreColor3 + placesScores[2]));

        inventory.setItem(getSize() - 9, createGuiItem(Material.BARRIER, "§cBack", "§8Click here to close", "§8this inventory."));
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent e) {
        final TopThreeGUI gui = TopThreeCommand.inventories.get(e.getWhoClicked().getUniqueId());
        if (gui == null) return;
        if (e.getInventory().equals(gui.inventory)) {
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
