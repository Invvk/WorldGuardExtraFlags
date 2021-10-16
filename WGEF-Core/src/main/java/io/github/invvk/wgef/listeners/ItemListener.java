package io.github.invvk.wgef.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ItemListener implements Listener {

    private final WGEFPlugin plugin;

    public ItemListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDurability(PlayerItemDamageEvent event) {
        final Player player = event.getPlayer();
        final ApplicableRegionSet regions = this.plugin.getFork()
                .getRegionContainer().createQuery().getApplicableRegions(player.getLocation());

        if (WGEFUtils.isDeny(WGEFUtils.queryState(player, player.getWorld(), regions.getRegions(),
                WGEFlags.ITEM_DURABILITY)))
            event.setCancelled(true);
    }

}
