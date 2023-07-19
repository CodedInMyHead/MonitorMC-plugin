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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class DashboardGUI implements Listener {

    private final Inventory inv;

    private String GRAFANA_URL;
    private HashMap<Integer, UrlItem> urlMap = new HashMap<>();

    public DashboardGUI() {
        inv = Bukkit.createInventory(null, 9*4, "Dashboards");
        initializeItemsAndUrlMap();
    }

    public void initializeItemsAndUrlMap() {
        if(MonitorMC.INSTANCE == null)
            return;
        System.out.println("TEST TEST TEST TEST");

        String customUrl = MonitorMC.INSTANCE.getCustomDashboardConfig().getString("grafana-url");
        this.GRAFANA_URL = (customUrl == null) ? "https://localhost:3000" : customUrl;
        System.out.println("URL: " + this.GRAFANA_URL);
        FileConfiguration configFile = MonitorMC.INSTANCE.getCustomDashboardConfig();
//        List<String> dashboards = configFile.getStringList("dashboards");
        ConfigurationSection dashboards = configFile.getConfigurationSection("dashboards");
        for(String s : dashboards.getKeys(false)) {
            String grafanaFilter = dashboards.getString(s+".grafana-filter");
            MonitorMC.INSTANCE.getLogger().log(Level.INFO, grafanaFilter);
        }
//        for(int i = 0; i < dashboards.getKeys(false).size(); i++) {
//            String grafanaFilter = dashboards.get(dashboards.getKeys(false).ge)
//            String materialString = configFile.getString(dashboards.get(i)+"."+"material");
//            System.out.println("MATERIAL: " +materialString);
//            List<String> lore = configFile.getStringList(dashboards.get(i)+"."+"lore");
//            urlMap.put(i, new UrlItem(grafanaFilter, Material.matchMaterial(materialString), dashboards.get(i), lore.toArray(new String[0])));
//        }

//        urlMap.put(0, new UrlItem("deaths/all", Material.CREEPER_HEAD, "Deaths", "lore1", "lore2"));
//        urlMap.put(2, new UrlItem("dashboard/new?orgId=1&editPanel=1", Material.ZOMBIE_HEAD, "Test Dashboard"));

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
