package io.github.invvk.wgef.listeners;

import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.flags.handler.player.FlySpeedFlagHandler;
import io.github.invvk.wgef.abstraction.flags.handler.player.WalkSpeedFlagHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This listener aims to fix problems related to walk/fly speed
 */
public class SpeedListener implements Listener {

    private final WGEFPlugin plugin;
    public SpeedListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFlyQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        FlySpeedFlagHandler speed = plugin.getFork().getSessionManager().get(player)
                .getHandler(FlySpeedFlagHandler.class);
        if (speed == null || speed.getOriginalSpeed() == null)
            return;
        player.setFlySpeed(speed.getOriginalSpeed());
    }

    @EventHandler
    public void onWalkQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        WalkSpeedFlagHandler speed = plugin.getFork().getSessionManager().get(player)
                .getHandler(WalkSpeedFlagHandler.class);
        if (speed == null || speed.getOriginalSpeed() == null)
            return;
        player.setWalkSpeed(speed.getOriginalSpeed());

    }

}
