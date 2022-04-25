package io.github.invvk.wgef.listeners;

import com.sk89q.worldguard.bukkit.event.entity.SpawnEntityEvent;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EntityPlaceListener implements Listener {

    private final WGEFPlugin plugin;

    public EntityPlaceListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityPlacement(EntityPlaceEvent e) {
        final var player = e.getPlayer();
        final var result = resolvePlaceResult(player, e.getEntity().getType(), e.getEntity().getLocation());

        if (result == Event.Result.DENY) {
            e.setCancelled(true);

            if (player != null) {
                player.updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityHang(HangingPlaceEvent e) {
        final var player = e.getPlayer();
        final var result = resolvePlaceResult(player, e.getEntity().getType(), e.getEntity().getLocation());

        if (result == Event.Result.DENY) {
            e.setCancelled(true);

            if (player != null) {
                player.updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntitySpawn(SpawnEntityEvent e) {
        if (!(e.getOriginalEvent() instanceof PlayerInteractEvent original) || original.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        e.setResult(resolvePlaceResult(original.getPlayer(), e.getEffectiveType(), e.getTarget()));
    }

    private Event.Result resolvePlaceResult(final Player player, final EntityType type, final Location location) {
        final var regions = plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(location);

        final var allowed = WGEFUtils.queryValue(player, location.getWorld(), regions.getRegions(), WGEFlags.ALLOW_ENTITY_PLACE);
        if (allowed != null && allowed.contains(type)) {
            return Event.Result.ALLOW;
        }

        final var denied = WGEFUtils.queryValue(player, location.getWorld(), regions.getRegions(), WGEFlags.DENY_ENTITY_PLACE);
        if (denied != null && denied.contains(type)) {
            return Event.Result.DENY;
        }

        return Event.Result.DENY;
    }

}

