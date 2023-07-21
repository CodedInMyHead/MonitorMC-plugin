package com.codedinmyhead.monitormc.monitormc.listeners;

import com.codedinmyhead.monitormc.monitormc.MonitorMC;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricService;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricsEnum;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArrowKillListener implements Listener {

    private final Map<UUID, String> arrowShooterMap = new HashMap<>();
    private final Map<String, Integer> killCounts = new HashMap<>();

//    @EventHandler
//    public void onProjectileLaunch(ProjectileLaunchEvent event) {
//        Projectile projectile = event.getEntity();
//        if (projectile.getType() == EntityType.ARROW) {
//            ProjectileSource shooter = projectile.getShooter();
//            if (shooter instanceof Player) {
//                Player player = (Player) shooter;
//                arrowShooterMap.put(projectile.getUniqueId(), player.getName());
//            }
//        }
//    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent e) {

        if(e.getEntity().getKiller() instanceof Player) {
            Player p = e.getEntity().getKiller();
            if(e.getEntity() instanceof Player || isHostileMob(e.getEntity().getType())) {
                if(e.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                    int currentKills = MonitorMC.INSTANCE.killCounts.getOrDefault(p.getName(), 0);
                    MonitorMC.INSTANCE.killCounts.put(p.getName(), currentKills + 1);
                    MetricService.getInstance().incrementCounter(MetricsEnum.HOSTILE_MOBS_KILLED, p.getName());
                }

            }

        }
    }

    private boolean isHostileMob(EntityType entityType) {
        switch (entityType) {
            case ZOMBIE:
            case SKELETON:
            case CREEPER:
            case SPIDER:
            case BLAZE:
            case WITCH:
            case SILVERFISH:
                return true;
            default:
                return false;
        }
    }

    public static int getKillCount(Player player) {
        return MonitorMC.INSTANCE.killCounts.getOrDefault(player.getName(), 0);
    }

    public void resetKillCount(Player player) {
        MonitorMC.INSTANCE.killCounts.put(player.getName(), 0);
    }
}