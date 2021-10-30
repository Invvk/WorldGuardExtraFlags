package io.github.invvk.wgef.abstraction.flags.handler.command;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import io.github.invvk.wgef.abstraction.flags.handler.AbstractFlagHandler;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class CommandOnEntryHandler extends AbstractFlagHandler<Set<String>> {

    public static final Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<CommandOnEntryHandler> {
        @Override
        public CommandOnEntryHandler create(Session session) {
            return new CommandOnEntryHandler(session, WGEFlags.COMMAND_ON_ENTRY);
        }
    }

    protected CommandOnEntryHandler(Session session, Flag<Set<String>> flag) {
        super(session, flag);
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, Set<String> value) {
        this.handleCommands(WGEFUtils.wrapPlayer(player), set);
    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet toSet, Set<String> currentValue, Set<String> lastValue, MoveType moveType) {
        this.handleCommands(WGEFUtils.wrapPlayer(localPlayer), toSet);
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> exited, Set<String> lastValue, MoveType moveType) {return true;}

    private void handleCommands(Player player, ApplicableRegionSet toSet) {
        Set<String> execute = new HashSet<>();
        for (ProtectedRegion region: toSet) {
            if (WGEFUtils.hasBypass(player, player.getWorld(), region, WGEFlags.COMMAND_ON_ENTRY))
                continue;
            Set<String> commands = region.getFlag(WGEFlags.COMMAND_ON_ENTRY);
            if (commands != null && !commands.isEmpty())
                execute.addAll(commands);
        }
        execute.forEach(e -> player.performCommand(e.substring(1).replace("%username%", player.getName())));

    }

}
