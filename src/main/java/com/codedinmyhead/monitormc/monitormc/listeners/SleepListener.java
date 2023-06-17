package com.codedinmyhead.monitormc.monitormc.listeners;

import com.codedinmyhead.monitormc.monitormc.monitoring.MetricService;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricsEnum;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SleepListener implements Listener {

    final MetricService metricService = MetricService.getInstance();

    @EventHandler
    public void onPlayerSleep(final PlayerDeepSleepEvent event) {
        metricService.incrementCounter(MetricsEnum.TIMES_SLEPT, event.getPlayer().getName());
    }
}
