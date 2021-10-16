package io.github.invvk.wgef.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;

public class FrostWalkerListener implements Listener {

    private final WGEFPlugin plugin;

    public FrostWalkerListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityBlockFormEvent(EntityBlockFormEvent event) {
        BlockState newState = event.getNewState();
        if (newState.getType() == Material.FROSTED_ICE) {
            ApplicableRegionSet regions = this.plugin.getFork()
                    .getRegionContainer().createQuery().getApplicableRegions(newState.getLocation());
            Entity entity = event.getEntity();
            if (entity instanceof Player player) {
                if (WGEFUtils.isDeny(WGEFUtils.queryValue(player, player.getWorld(), regions.getRegions(), WGEFlags.FROST_WALKER))) {
                    event.setCancelled(true);
                }
            } else {
                if (WGEFUtils.isDeny(regions.queryValue(null, WGEFlags.FROST_WALKER))) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
