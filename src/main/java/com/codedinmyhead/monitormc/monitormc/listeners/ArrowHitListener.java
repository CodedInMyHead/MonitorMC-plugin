package com.codedinmyhead.monitormc.monitormc.listeners;

import com.codedinmyhead.monitormc.monitormc.MonitorMC;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricsEnum;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;


public class ArrowHitListener implements Listener {

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if(event.getEntity() instanceof Arrow ) {
            Arrow a = (Arrow) event.getEntity();
            Block hitBlock = event.getHitBlock();

            if(a.getShooter() instanceof Player) {
                Player p = (Player) a.getShooter();
                if(countShotAsTry(p)) {
                    if(hitBlock.getType().equals(MonitorMC.INSTANCE.targetBlockMaterial)) {
                        MonitorMC.INSTANCE.metricService.incrementCounter(MetricsEnum.ARROW_HIT);
                        p.sendMessage("You hit a target!");
                        p.playNote(p.getLocation(), Instrument.BELL, Note.natural(1, Note.Tone.E));
                    } else {
                        MonitorMC.INSTANCE.metricService.incrementCounter(MetricsEnum.ARROW_MISS);
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
