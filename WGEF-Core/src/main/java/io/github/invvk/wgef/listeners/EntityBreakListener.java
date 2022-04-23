package io.github.invvk.wgef.listeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import com.sk89q.worldguard.bukkit.event.Handleable;
import com.sk89q.worldguard.bukkit.event.entity.DamageEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.DestroyEntityEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.SetFlag;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;

import java.util.Set;

public final class EntityBreakListener implements Listener {

    private final WGEFPlugin plugin;

    public EntityBreakListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(DamageEntityEvent e) {
        if (!(e.getOriginalEvent() instanceof EntityDamageByEntityEvent original) || !(original.getDamager() instanceof Player damager)) {
            return;
        }

        handleEntityEvent(WGEFlags.ALLOW_ENTITY_DAMAGE, WGEFlags.DENY_ENTITY_DAMAGE, damager, e, e.getEntity().getType(), e.getTarget());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDestroy(DestroyEntityEvent e) {
        final var cause = e.getCause().getFirstPlayer();
        if (cause == null) {
            return;
        }

        handleEntityEvent(WGEFlags.ALLOW_ENTITY_DESTROY, WGEFlags.DENY_ENTITY_DESTROY, cause, e, e.getEntity().getType(), e.getTarget());
    }

    private void handleEntityEvent(SetFlag<EntityType> allowFlag, SetFlag<EntityType> denyFlag, Player playerWhoPlaced, Cancellable event, EntityType entityType, Location location) {
        ApplicableRegionSet regions = plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(location);

        Set<EntityType> allowed = WGEFUtils.queryValue(playerWhoPlaced, location.getWorld(), regions.getRegions(), allowFlag);
        if (allowed != null) {
            if (!allowed.contains(entityType)) {
                event.setCancelled(true);
                return;
            } else if (event instanceof Handleable handleable) {
                handleable.setResult(Event.Result.ALLOW);
                return;
            }
        }


        Set<EntityType> denied = WGEFUtils.queryValue(playerWhoPlaced, location.getWorld(), regions.getRegions(), denyFlag);
        if (denied != null && denied.contains(entityType)) {
            event.setCancelled(true);
        }
    }

}
