package io.github.invvk.wgef.listeners;

import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

import java.util.Set;

public class BlockListener implements Listener {

    private final WGEFPlugin plugin;

    public BlockListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBlockPlaceEvent(PlaceBlockEvent event) {
        Event.Result originalResult = event.getResult();
        Object cause = event.getCause().getRootCause();
        if (cause instanceof Player player) {
            for (Block block : event.getBlocks()) {
                Material type = block.getType();
                if (type == Material.AIR)
                    type = event.getEffectiveMaterial();

                ApplicableRegionSet regions = this.plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());

                Set<Material> state = WGEFUtils.queryValue(player, player.getWorld(), regions.getRegions(), WGEFlags.ALLOW_BLOCK_PLACE);
                if (state != null && state.contains(type)) {
                    event.setResult(Event.Result.ALLOW);
                } else {
                    Set<Material> state2 = WGEFUtils.queryValue(player, player.getWorld(), regions.getRegions(), WGEFlags.DENY_BLOCK_PLACE);
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBlockBreakEvent(BreakBlockEvent event) {
        Event.Result originalResult = event.getResult();
        Object cause = event.getCause().getRootCause();

        if (cause instanceof Player player) {

            for (Block block : event.getBlocks()) {
                ApplicableRegionSet regions = this.plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());

                Set<Material> state = WGEFUtils.queryValue(player, player.getWorld(), regions.getRegions(), WGEFlags.ALLOW_BLOCK_BREAK);
                if (state != null && state.contains(block.getType())) {
                    event.setResult(Event.Result.ALLOW);
                } else {
                    Set<Material> state2 = WGEFUtils.queryValue(player, player.getWorld(), regions.getRegions(), WGEFlags.DENY_BLOCK_BREAK);
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
  
    // [Invvk] merge conflicts
    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        Player player = event.getPlayer();

        ApplicableRegionSet regions = this.plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(event.getBlock().getLocation());

        Set<Material> state = WGEFUtils.queryValue(player, player.getWorld(), regions.getRegions(), WGEFlags.ALLOWED_BLOCK_DROPS);
        if (!event.getItems().removeIf(item -> state != null && !state.contains(item.getItemStack().getType()))) {
            Set<Material> state2 = WGEFUtils.queryValue(player, player.getWorld(), regions.getRegions(), WGEFlags.BLOCKED_BLOCK_DROPS);
            event.getItems().removeIf(item -> state2 != null && state2.contains(item.getItemStack().getType()));
        }
    }

}
