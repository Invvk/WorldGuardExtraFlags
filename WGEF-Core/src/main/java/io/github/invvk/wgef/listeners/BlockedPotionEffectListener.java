package io.github.invvk.wgef.listeners;

import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.flags.handler.player.BlockEffectFlagHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class BlockedPotionEffectListener implements Listener {

    private final WGEFPlugin plugin;

    public BlockedPotionEffectListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlocked(EntityPotionEffectEvent event) {
        if (event.getCause() == EntityPotionEffectEvent.Cause.PLUGIN)
            return;

        if (event.getEntity() instanceof final Player player) {
            if (event.getNewEffect() == null)
                return;

            final PotionEffectType given = event.getNewEffect().getType();

            BlockEffectFlagHandler handler = plugin.getFork().getSessionManager()
                    .get(player).getHandler(BlockEffectFlagHandler.class);

            if (handler == null)
                return;

            final Set<PotionEffectType> blocked = handler.getBlockedPotions();

            if (blocked == null || blocked.isEmpty())
                return;

            if (blocked.contains(given)) {
                event.setCancelled(true);

                if (player.hasPotionEffect(given))
                    player.removePotionEffect(given);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null)
            return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem().getType().name().contains("POTION")) {
                Player player = event.getPlayer();

                BlockEffectFlagHandler handler = plugin.getFork().getSessionManager()
                        .get(player).getHandler(BlockEffectFlagHandler.class);

                if (handler == null)
                    return;

                PotionMeta meta = (PotionMeta)
                        event.getItem().getItemMeta();

                if (meta == null)
                    return;

                final PotionEffectType given = meta.getBasePotionData().getType().getEffectType();

                if (given == null)
                    return;

                final Set<PotionEffectType> blocked = handler.getBlockedPotions();

                if (blocked == null || blocked.isEmpty())
                    return;

                if (blocked.contains(given)) {
                    event.setCancelled(true);

                    if (player.hasPotionEffect(given))
                        player.removePotionEffect(given);
                }
            }
        }
    }

}
