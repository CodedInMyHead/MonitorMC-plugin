package com.codedinmyhead.monitormc.monitormc.listener;

import org.bukkit.Material;
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
            if(hitBlock.getType().equals(Material.RED_WOOL)) {
                if(a.getShooter() instanceof Player) {
                    Player p = (Player) a.getShooter();
                    p.sendMessage("You hit a red wool block!");
                }
            }
        }
    }

}
