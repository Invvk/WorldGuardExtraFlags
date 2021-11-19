package io.github.invvk.wgef.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

import java.util.Set;

public class EntityPlaceListener implements Listener {

    private final WGEFPlugin plugin;

    public EntityPlaceListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityPlacement(EntityPlaceEvent e) {
        handleEntityEvent(e.getPlayer(), e, e.getEntity());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityHang(HangingPlaceEvent e) {
        handleEntityEvent(e.getPlayer(), e, e.getEntity());
    }

    private void handleEntityEvent(Player playerWhoPlaced, Cancellable event, Entity entity) {
        EntityType entityType = entity.getType();
        ApplicableRegionSet regions = plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(entity.getLocation());

        Set<EntityType> allowedEntityPlacements = WGEFUtils.queryValue(playerWhoPlaced, entity.getWorld(), regions.getRegions(), WGEFlags.ALLOW_ENTITY_PLACE);
        if (allowedEntityPlacements != null && !allowedEntityPlacements.contains(entity.getType())) {
            event.setCancelled(true);
            playerWhoPlaced.updateInventory();
        }

        Set<EntityType> deniedEntityPlacements = WGEFUtils.queryValue(playerWhoPlaced, entity.getWorld(), regions.getRegions(), WGEFlags.DENY_ENTITY_PLACE);
        if (deniedEntityPlacements != null && deniedEntityPlacements.contains(entity.getType())) {
            event.setCancelled(true);
            playerWhoPlaced.updateInventory();
        }
    }

}

