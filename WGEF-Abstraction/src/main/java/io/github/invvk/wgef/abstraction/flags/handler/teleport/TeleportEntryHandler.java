package io.github.invvk.wgef.abstraction.flags.handler.teleport;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import io.github.invvk.wgef.abstraction.flags.handler.AbstractFlagHandler;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class TeleportEntryHandler extends AbstractFlagHandler<Location> {

    public static Factory FACTORY (JavaPlugin plugin) {
        return new Factory(plugin);
    }

    private final JavaPlugin plugin;

    public static class Factory extends Handler.Factory<TeleportEntryHandler> {

        private final JavaPlugin plugin;
        public Factory(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public TeleportEntryHandler create(Session session) {
            return new TeleportEntryHandler(plugin, session);
        }
    }

    protected TeleportEntryHandler(JavaPlugin plugin, Session session) {
        super(session, WGEFlags.TELEPORT_ON_ENTRY);
        this.plugin = plugin;
    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet toSet, Location currentValue, Location lastValue, MoveType moveType) {
        final Player player = WGEFUtils.wrapPlayer(localPlayer);
        final Location location = WGEFUtils.queryValueUnchecked(player, player.getWorld(), toSet.getRegions(), WGEFlags.TELEPORT_ON_ENTRY);
        if (location != null)
            Bukkit.getScheduler().runTaskLater(plugin, () -> localPlayer.teleport(location, "", ""), 1);
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> exited, Location lastValue, MoveType moveType) {
        return true;
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, Location value) {}
}
