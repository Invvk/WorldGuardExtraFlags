package io.github.invvk.wgef.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class VillagerTradeListener implements Listener {

    private final WGEFPlugin plugin;

    public VillagerTradeListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager villager) {
            final Player player = event.getPlayer();
            final ApplicableRegionSet set =
                    plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(villager.getLocation());

            if (WGEFUtils.isPartOfRegion(player, set))
                return;

            final StateFlag.State state = WGEFUtils.queryState(player, player.getWorld(), set.getRegions(), WGEFlags.VILLAGER_TRADE);
            if (WGEFUtils.isDeny(state))
                event.setCancelled(true);
        }
    }

}
