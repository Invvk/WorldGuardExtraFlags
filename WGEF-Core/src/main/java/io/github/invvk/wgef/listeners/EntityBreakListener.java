package io.github.invvk.wgef.listeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.DamageEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.DestroyEntityEvent;
import com.sk89q.worldguard.protection.flags.SetFlag;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;

import java.util.Objects;

public final class EntityBreakListener implements Listener {

    private final WGEFPlugin plugin;

    public EntityBreakListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(DamageEntityEvent e) {
        if (!(e.getOriginalEvent() instanceof EntityDamageByEntityEvent original)) {
            return;
        }

        if (original.getDamager() instanceof Player player) {
            e.setResult(resolveBreakResult(player, e.getEntity().getType(), e.getTarget(), WGEFlags.ALLOW_ENTITY_DAMAGE, WGEFlags.DENY_ENTITY_DAMAGE));
        } else if (original.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player player) {
            e.setResult(resolveBreakResult(player, e.getEntity().getType(), e.getTarget(), WGEFlags.ALLOW_ENTITY_DAMAGE, WGEFlags.DENY_ENTITY_DAMAGE));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDestroy(DestroyEntityEvent e) {
        final var player = e.getCause().getFirstPlayer();
        if (player == null) {
            return;
        }

        e.setResult(resolveBreakResult(player, e.getEntity().getType(), e.getTarget(), WGEFlags.ALLOW_ENTITY_DESTROY, WGEFlags.DENY_ENTITY_DESTROY));
    }


    private Event.Result resolveBreakResult(final Player player, final EntityType type, final Location location, final SetFlag<EntityType> allowFlag, final SetFlag<EntityType> denyFlag) {
        final var regions = plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(location);

        final var allowed = WGEFUtils.queryValue(player, location.getWorld(), regions.getRegions(), allowFlag);
        if (allowed != null && allowed.contains(type)) {
            return Event.Result.ALLOW;
        }

        final var denied = WGEFUtils.queryValue(player, location.getWorld(), regions.getRegions(), denyFlag);
        if (denied != null && denied.contains(type)) {
            return Event.Result.DENY;
        }

        return Event.Result.DEFAULT;
    }

}
