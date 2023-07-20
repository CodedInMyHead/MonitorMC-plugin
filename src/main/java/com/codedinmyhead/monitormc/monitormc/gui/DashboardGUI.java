package com.codedinmyhead.monitormc.monitormc.gui;

import com.codedinmyhead.monitormc.monitormc.MonitorMC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Level;

public class DashboardGUI implements Listener {

    private final Inventory inv;
    private final int invSize = 36;
    private String GRAFANA_URL;
    private HashMap<Integer, UrlItem> urlMap = new HashMap<>();

    public DashboardGUI() {
        inv = Bukkit.createInventory(null, this.invSize, "Dashboards");
        initializeItemsAndUrlMap();
    }

    public void initializeItemsAndUrlMap() {
        if(MonitorMC.INSTANCE == null)
            return;

        String customUrl = MonitorMC.INSTANCE.getCustomDashboardConfig().getString("grafana-url");
        this.GRAFANA_URL = (customUrl == null) ? "https://localhost:3000" : customUrl;
        FileConfiguration configFile = MonitorMC.INSTANCE.getCustomDashboardConfig();
        ConfigurationSection dashboards = configFile.getConfigurationSection("dashboards");
        int slot = 0;
        for(String s : dashboards.getKeys(false)) {
            if(slot > this.invSize)
                break;
            String grafanaFilter = dashboards.getString(s+".grafana-filter") == null ? "" : dashboards.getString(s+".grafana-filter");
            String materialString = dashboards.getString(s+".material") == null ? "item_frame" : dashboards.getString(s+".material");
            List<String> lore = dashboards.getStringList(s+".lore");
            urlMap.put(slot, new UrlItem(grafanaFilter, Material.matchMaterial(materialString), s, lore.toArray(new String[0])));
            slot += 2;
        }

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
            p.closeInventory();
            openUrl(p, urlMap.get(slot).url);
        }

    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }

    private void openUrl(Player p, String url) {
        TextComponent linkComponent =
                Component.text("Click Me!")
                        .color(TextColor.color(19, 255, 53))
                        .clickEvent(ClickEvent.openUrl(url))
                        .hoverEvent(HoverEvent.showText(Component.text(url)));

        TextComponent bookPage =
                (TextComponent) Component.text("Click on the text below to open the dashboard:")
                        .color(TextColor.color(1,1,1))
                        .appendNewline()
                        .appendNewline()
                        .append(linkComponent);

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.addPages(bookPage);
        bookMeta.setAuthor("MonitorMC");
        bookMeta.setTitle("Dashboard Link");
        book.setItemMeta(bookMeta);
        p.openBook(book);

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
            this.url = GRAFANA_URL + "/" + grafanaFilter;
        }
    }

}
