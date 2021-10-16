package io.github.invvk.wgef.listeners.essentials;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import net.ess3.api.IUser;
import net.ess3.api.events.GodStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GodModeListener implements Listener {

    @EventHandler
    public void onGodMode(GodStatusChangeEvent event) {
        IUser user = event.getAffected();
        Player player = user.getBase();
        ApplicableRegionSet toSet = WGEFUtils.getFork().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
        StateFlag.State state = WGEFUtils.queryState(player, player.getWorld(), toSet.getRegions(), WGEFlags.GOD_MODE);
        if (WGEFUtils.isDeny(state))
            event.setCancelled(true);
    }

}
