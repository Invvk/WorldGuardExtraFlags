package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.handlers.FlyFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.GodmodeFlagHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

@RequiredArgsConstructor
public class FlyFlagListener implements Listener {

    @Getter private final WorldGuardExtraFlagsPlugin plugin;

    @EventHandler
    public void onFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        ApplicableRegionSet regions = this.plugin.getWorldGuardCommunicator().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());

        State state = WorldGuardUtils.queryState(player, player.getWorld(), regions.getRegions(), Flags.FLY);
        if (state != null) {
            boolean val = (state == State.ALLOW);
            if (player.getAllowFlight() != val) {
                player.setAllowFlight(val);
                event.setCancelled(val);
            }
        }
    }

}
