package io.github.invvk.wgef.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class VillagerTradeListener implements Listener {

    private final WGEFPlugin plugin;

    public VillagerTradeListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Villager villager) {
            final Player player = event.getPlayer();
            final ApplicableRegionSet set =
                    plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(villager.getLocation());
            if (!set.testState(WGEFUtils.wrapPlayer(player), WGEFlags.VILLAGER_TRADE))
                event.setCancelled(true);
        }
    }

}
