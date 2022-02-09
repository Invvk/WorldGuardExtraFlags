package io.github.invvk.wgef.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.association.DelayedRegionOverlapAssociation;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.List;

public class BlockListenerPatch implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGrowth(StructureGrowEvent event) {
        this.handleStructure(event.getLocation(), event.getBlocks());
    }

    @EventHandler
    public void onBoneMeal(BlockFertilizeEvent event) {
        this.handleStructure(event.getBlock().getLocation(), event.getBlocks());
    }

    // Invvk's patch for issue (https://github.com/aromaa/WorldGuardExtraFlagsPlugin/issues/152)
    // Now it doesn't require a localPlayer to operate, all it needs is an origin location.
    // however, I'm suspecting that this will break 'nonplayer-protection-domains' flag
    private void handleStructure(Location origin, List<BlockState> blocks) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        DelayedRegionOverlapAssociation association = new DelayedRegionOverlapAssociation(query, BukkitAdapter.adapt(origin));

        for (BlockState block : blocks) {
            if (block == null)
                continue;

            if (!query.testBuild(BukkitAdapter.adapt(block.getLocation()),
                    association, Flags.BUILD, Flags.BLOCK_PLACE))
                block.setType(block.getWorld().getBlockAt(block.getLocation()).getType());
        }
    }

}
