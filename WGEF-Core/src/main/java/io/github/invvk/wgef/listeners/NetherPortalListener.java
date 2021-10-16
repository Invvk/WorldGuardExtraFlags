package io.github.invvk.wgef.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

public class NetherPortalListener implements Listener {

    private final WGEFPlugin plugin;

    public NetherPortalListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPortalCreateEvent(PortalCreateEvent event) {
        // Now, if the creator for the nether portal is found and have bypass it will ignore the rest
        if (event.getEntity() != null) {
            if (event.getEntity() instanceof Player player) {
                ApplicableRegionSet regions = this.plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
                for (ProtectedRegion region : regions) {
                    if (WGEFUtils.hasBypass(player, player.getWorld(), region, WGEFlags.NETHER_PORTALS)) {
                        return;
                    }
                }
            }
        }

        for (BlockState block : event.getBlocks()) {
            ApplicableRegionSet regions = this.plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());
            if (regions.queryValue(null, WGEFlags.NETHER_PORTALS) == StateFlag.State.DENY) {
                event.setCancelled(true);
                break;
            }
        }
    }

}
