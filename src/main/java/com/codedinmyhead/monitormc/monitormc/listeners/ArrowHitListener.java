package com.codedinmyhead.monitormc.monitormc.listeners;

import com.codedinmyhead.monitormc.monitormc.MonitorMC;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricService;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricsEnum;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;


public class ArrowHitListener implements Listener {

    final MetricService metricService = MetricService.getInstance();

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if(event.getEntity() instanceof Arrow a) {
            Block hitBlock = event.getHitBlock();

            if(a.getShooter() instanceof Player p) {
                if(countShotAsTry(p)) {
                    if(hitBlock != null && hitBlock.getType().equals(MonitorMC.INSTANCE.targetBlockMaterial)) {
                        metricService.incrementCounter(MetricsEnum.ARROW_HIT, p.getName());
                        p.sendMessage("You hit a target!");
                        p.playNote(p.getLocation(), Instrument.BELL, Note.natural(1, Note.Tone.E));
                    } else {
                        metricService.incrementCounter(MetricsEnum.ARROW_MISS, p.getName());
                        p.sendMessage("You missed a target!");
                    }
                }
            }
        }
    }

    private boolean countShotAsTry(Player p) {
        return p.getInventory().getItemInMainHand().equals(MonitorMC.INSTANCE.accuracyBow);
    }

}
