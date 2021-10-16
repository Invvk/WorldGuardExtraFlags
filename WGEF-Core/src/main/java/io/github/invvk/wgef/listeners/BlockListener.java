package io.github.invvk.wgef.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.event.Handleable;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.DelayedRegionOverlapAssociation;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.List;
import java.util.Set;

public class BlockListener implements Listener {

    private final WGEFPlugin plugin;

    public BlockListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlaceEvent(PlaceBlockEvent event) {
        handleBlockEvent(event, event.getCause().getRootCause(), event.getEffectiveMaterial(), event.getBlocks());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreakEvent(BreakBlockEvent event) {
        handleBlockEvent(event, event.getCause().getRootCause(), event.getEffectiveMaterial(), event.getBlocks());
    }

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

        for (BlockState block: blocks) {
            if (block == null)
                continue;

            if (!query.testBuild(BukkitAdapter.adapt(block.getLocation()),
                    association, Flags.BUILD, Flags.BLOCK_PLACE))
                block.setType(block.getWorld().getBlockAt(block.getLocation()).getType());
        }
    }

    // Special thanks to @OnyxianSoul
    private void handleBlockEvent(Handleable event, Object cause, Material effectiveMaterial, List<Block> affectedBlocks) {
        if (cause instanceof Player player) {
            for (Block block : affectedBlocks) {
                Material type = block.getType();
                if (type == Material.AIR)
                    type = effectiveMaterial;  //Workaround for https://github.com/aromaa/WorldGuardExtraFlagsPlugin/issues/12

                ApplicableRegionSet regions = plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());

                Set<Material> allowBlockPlaceMaterials = WGEFUtils.queryValue(player, player.getWorld(), regions.getRegions(), WGEFlags.ALLOW_BLOCK_PLACE);
                if (allowBlockPlaceMaterials != null && !allowBlockPlaceMaterials.contains(type)) { //If there is a list of allowed materials, and it doesn't contain this block, deny the placement
                    event.setResult(Event.Result.DENY);
                }

                Set<Material> denyBlockPlaceMaterials = WGEFUtils.queryValue(player, player.getWorld(), regions.getRegions(), WGEFlags.DENY_BLOCK_PLACE); //If there is a list of denied materials, and it includes this block, deny the placement
                if (denyBlockPlaceMaterials != null && denyBlockPlaceMaterials.contains(type)) {
                    event.setResult(Event.Result.DENY);
                    return;
                }
            }
        }
    }

}
