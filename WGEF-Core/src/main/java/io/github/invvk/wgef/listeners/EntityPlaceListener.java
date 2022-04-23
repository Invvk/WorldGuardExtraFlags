package io.github.invvk.wgef.listeners;

import com.sk89q.worldguard.bukkit.event.Handleable;
import com.sk89q.worldguard.bukkit.event.entity.SpawnEntityEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

public class EntityPlaceListener implements Listener {

    private final WGEFPlugin plugin;

    public EntityPlaceListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityPlacement(EntityPlaceEvent e) {
        handleEntityEvent(e.getPlayer(), e, e.getEntity().getType(), e.getEntity().getLocation());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityHang(HangingPlaceEvent e) {
        handleEntityEvent(e.getPlayer(), e, e.getEntity().getType(), e.getEntity().getLocation());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntitySpawn(SpawnEntityEvent e) {
        if (!(e.getOriginalEvent() instanceof PlayerInteractEvent original) || original.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        handleEntityEvent(original.getPlayer(), e, e.getEffectiveType(), e.getTarget());
    }

    private void handleEntityEvent(Player playerWhoPlaced, Cancellable event, EntityType entityType, Location location) {
        ApplicableRegionSet regions = plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(location);

        Set<EntityType> allowedEntityPlacements = WGEFUtils.queryValue(playerWhoPlaced, location.getWorld(), regions.getRegions(), WGEFlags.ALLOW_ENTITY_PLACE);
        if (allowedEntityPlacements != null) {
            if (!allowedEntityPlacements.contains(entityType)) {
                event.setCancelled(true);
                playerWhoPlaced.updateInventory();
                return;
            } else if (event instanceof Handleable handleable) {
                handleable.setResult(Event.Result.ALLOW);
                return;
            }
        }


        Set<EntityType> deniedEntityPlacements = WGEFUtils.queryValue(playerWhoPlaced, location.getWorld(), regions.getRegions(), WGEFlags.DENY_ENTITY_PLACE);
        if (deniedEntityPlacements != null && deniedEntityPlacements.contains(entityType)) {
            event.setCancelled(true);
            playerWhoPlaced.updateInventory();
        }
    }

}

