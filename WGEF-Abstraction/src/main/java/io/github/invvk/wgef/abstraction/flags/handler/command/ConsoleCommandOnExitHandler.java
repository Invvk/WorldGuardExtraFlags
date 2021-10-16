package io.github.invvk.wgef.abstraction.flags.handler.command;

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

import java.util.HashSet;
import java.util.Set;

public class ConsoleCommandOnExitHandler extends AbstractFlagHandler<Set<String>> {

    public static ConsoleCommandOnExitHandler.Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<ConsoleCommandOnExitHandler> {

        @Override
        public ConsoleCommandOnExitHandler create(Session session) {
            return new ConsoleCommandOnExitHandler(session);
        }
    }

    protected ConsoleCommandOnExitHandler(Session session) {
        super(session, WGEFlags.CONSOLE_COMMAND_ON_EXIT);
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> exited, Set<String> lastValue, MoveType moveType) {
        Player player = WGEFUtils.wrapPlayer(localPlayer);
        Set<String> execute = new HashSet<>();
        for (ProtectedRegion region: exited) {
            if (WGEFUtils.hasBypass(player, player.getWorld(), region, WGEFlags.CONSOLE_COMMAND_ON_EXIT))
                continue;
            Set<String> commands = region.getFlag(WGEFlags.CONSOLE_COMMAND_ON_EXIT);
            if (commands != null && !commands.isEmpty())
                execute.addAll(commands);
        }
        execute.forEach(e -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                e.substring(1).replace("%username%", player.getName())));
        return true;
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<String> currentValue, Set<String> lastValue, MoveType moveType) {
        return true;
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, Set<String> value) {}
}
