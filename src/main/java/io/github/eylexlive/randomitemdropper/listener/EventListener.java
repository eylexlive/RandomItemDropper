package io.github.eylexlive.randomitemdropper.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {
    @EventHandler
    public void handleJoinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final boolean isDev = player.getName().equals("UmutErarslan_") || player.getName().equals("_Luckk_");
        if (isDev)
            player.sendMessage("§aThis server using the Random Item Dropper.");
    }
}
