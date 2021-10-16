package io.github.invvk.wgef.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final WGEFPlugin plugin;

    public DeathListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        ApplicableRegionSet regions = this.plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());

        Boolean keepInventory = WGEFUtils.queryValue(player, player.getWorld(), regions.getRegions(), WGEFlags.KEEP_INVENTORY);
        if (keepInventory != null) {
            event.setKeepInventory(keepInventory);

            if (keepInventory) {
                event.getDrops().clear();
            }
        }

        Boolean keepExp = WGEFUtils.queryValue(player, player.getWorld(), regions.getRegions(), WGEFlags.KEEP_EXP);
        if (keepExp != null) {
            event.setKeepLevel(keepExp);

            if (keepExp) {
                event.setDroppedExp(0);
            }
        }
    }

}
