package com.codedinmyhead.monitormc.monitormc.listeners;

import com.codedinmyhead.monitormc.monitormc.monitoring.MetricService;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricsEnum;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class DiamondOreMined implements Listener {

    @EventHandler
    public void onMined(final BlockBreakEvent event) {
        if (event.getPlayer() == null) return;
        if (event.getBlock().getType() == Material.DIAMOND_ORE || event.getBlock().getType() == Material.DEEPSLATE_DIAMOND_ORE) {
            MetricService.getInstance().incrementCounter(MetricsEnum.DIAMONDS_MINED, event.getPlayer().getName());
        }
    }
}
