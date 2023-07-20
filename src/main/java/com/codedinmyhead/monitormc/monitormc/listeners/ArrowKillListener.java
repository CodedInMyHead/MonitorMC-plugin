package com.codedinmyhead.monitormc.monitormc.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArrowKillListener implements Listener {

    private final Map<UUID, String> arrowShooterMap = new HashMap<>();
    private final Map<String, Integer> killCounts = new HashMap<>();

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.getType() == EntityType.ARROW) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player) {
                Player player = (Player) shooter;
                arrowShooterMap.put(projectile.getUniqueId(), player.getName());
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getType() == EntityType.ARROW) {
                UUID arrowUniqueId = projectile.getUniqueId();
                if (arrowShooterMap.containsKey(arrowUniqueId)) {
                    String shooterName = arrowShooterMap.get(arrowUniqueId);
                    if (event.getEntity() instanceof Player) {
                        Player victim = (Player) event.getEntity();
                        if (!victim.getName().equals(shooterName)) {
                            int currentKills = killCounts.getOrDefault(shooterName, 0);
                            killCounts.put(shooterName, currentKills + 1);
                        }
                    } else if (isHostileMob(event.getEntityType())) {
                        int currentKills = killCounts.getOrDefault(shooterName, 0);
                        killCounts.put(shooterName, currentKills + 1);
                    }
                    arrowShooterMap.remove(arrowUniqueId);
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
    public int getKillCount(Player player) {
        return killCounts.getOrDefault(player.getName(), 0);
    }
}