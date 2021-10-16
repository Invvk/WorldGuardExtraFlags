package io.github.invvk.wgef.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class GlideListener implements Listener {

    private final WGEFPlugin plugin;

    public GlideListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGlide(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof final Player player) {
            ApplicableRegionSet set = plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
            StateFlag.State state = WGEFUtils.queryState(player, player.getWorld(), set.getRegions(), WGEFlags.GLIDE);
            if (WGEFUtils.isDeny(state)) {
                if (!event.isGliding()) {
                    return;
                }

                event.setCancelled(true);
                player.teleport(player.getLocation());
            }
        }
    }

}
