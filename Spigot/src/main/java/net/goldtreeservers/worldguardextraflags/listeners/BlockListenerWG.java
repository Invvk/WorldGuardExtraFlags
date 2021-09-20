package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.Set;

@RequiredArgsConstructor
public class BlockListenerWG implements Listener {
    @Getter
    private final WorldGuardExtraFlagsPlugin plugin;

    //TODO: Figure out something better for this
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBlockPlaceEvent(PlaceBlockEvent event) {
        Event.Result originalResult = event.getResult();
        Object cause = event.getCause().getRootCause();

        if (cause instanceof Player) {
            Player player = (Player) cause;

            for (Block block : event.getBlocks()) {
                Material type = block.getType();
                if (type == Material.AIR) //Workaround for https://github.com/aromaa/WorldGuardExtraFlagsPlugin/issues/12
                {
                    type = event.getEffectiveMaterial();
                }

                ApplicableRegionSet regions = this.plugin.getWorldGuardCommunicator().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());

                Set<Material> state = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.ALLOW_BLOCK_PLACE);
                if (state != null && state.contains(type)) {
                    event.setResult(Event.Result.ALLOW);
                } else {
                    Set<Material> state2 = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.DENY_BLOCK_PLACE);
                    if (state2 != null && state2.contains(type)) {
                        event.setResult(Event.Result.DENY);
					} else {
                        event.setResult(originalResult);
					}
					return;
				}
            }
        }
    }

    //TODO: Figure out something better for this
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBlockBreakEvent(BreakBlockEvent event) {
        Event.Result originalResult = event.getResult();
        Object cause = event.getCause().getRootCause();

        if (cause instanceof Player) {
            Player player = (Player) cause;

            for (Block block : event.getBlocks()) {
                ApplicableRegionSet regions = this.plugin.getWorldGuardCommunicator().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());

                Set<Material> state = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.ALLOW_BLOCK_BREAK);
                if (state != null && state.contains(block.getType())) {
                    event.setResult(Event.Result.ALLOW);
                } else {
                    Set<Material> state2 = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.DENY_BLOCK_BREAK);
                    if (state2 != null && state2.contains(block.getType())) {
                        event.setResult(Event.Result.DENY);
					} else {
                        event.setResult(originalResult);
					}
					return;
				}
            }
        }
    }

    // Invvk's patch for issue (https://github.com/aromaa/WorldGuardExtraFlagsPlugin/issues/152)
    // Still not sure if the patch is good to go yet, only tested with Spruce 4x4 and Oak sapling
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onGrowth(StructureGrowEvent event) {
        Player player = event.getPlayer();
        LocalPlayer player1 = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();

		for (BlockState block: event.getBlocks()) {
            if (block == null)
                continue;
            if (!query.testBuild(BukkitAdapter.adapt(block.getLocation()),
                    player1, com.sk89q.worldguard.protection.flags.Flags.BUILD)) {
                if (block.getType() == Material.GRASS_BLOCK)
                    continue;

                if (block.getType() == Material.PODZOL) {
                    block.setType(player.getWorld().getBlockAt(block.getLocation()).getType());
                    continue;
                }

                block.setType(Material.AIR);
            }
		}
	}

}
