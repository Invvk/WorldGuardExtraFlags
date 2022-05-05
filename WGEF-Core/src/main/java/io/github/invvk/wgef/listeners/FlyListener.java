package io.github.invvk.wgef.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class FlyListener implements Listener {

    private final WGEFPlugin plugin;

    public FlyListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        ApplicableRegionSet set = plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
        StateFlag.State state = WGEFUtils.queryState(player, player.getWorld(), set.getRegions(), WGEFlags.FLY);
        if (WGEFUtils.isDeny(state)) {
            if (player.getAllowFlight()) {
                player.setAllowFlight(false);
                player.setFlying(false);
                player.teleport(player.getLocation());
            }

            event.setCancelled(true);
        }
    }

}
