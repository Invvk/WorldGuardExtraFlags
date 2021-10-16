package io.github.invvk.wgef.abstraction.flags.handler.player;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import io.github.invvk.wgef.abstraction.flags.handler.AbstractFlagHandler;
import org.bukkit.entity.Player;

import java.util.Set;

public class GlideFlagHandler extends AbstractFlagHandler<StateFlag.State> {

    public static Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<GlideFlagHandler> {
        @Override
        public GlideFlagHandler create(Session session) {
            return new GlideFlagHandler(session);
        }
    }

    protected GlideFlagHandler(Session session) {
        super(session, WGEFlags.GLIDE);
    }

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet set, StateFlag.State value) {
        Player player = WGEFUtils.wrapPlayer(localPlayer);
        StateFlag.State state = WGEFUtils.queryValue(player, player.getWorld(), set.getRegions(), WGEFlags.GLIDE);
        this.handleValue(player, state);
    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet toSet, StateFlag.State currentValue, StateFlag.State lastValue, MoveType moveType) {
        Player player = WGEFUtils.wrapPlayer(localPlayer);
        StateFlag.State state = WGEFUtils.queryValue(player, player.getWorld(), toSet.getRegions(), WGEFlags.GLIDE);
        this.handleValue(player, state);
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> exited, StateFlag.State lastValue, MoveType moveType) {
        Player player = WGEFUtils.wrapPlayer(localPlayer);
        StateFlag.State state = WGEFUtils.queryValue(player, player.getWorld(), toSet.getRegions(), WGEFlags.GLIDE);
        this.handleValue(player, state);
        return true;
    }

    @Override
    public void tick(LocalPlayer localPlayer, ApplicableRegionSet set) {
        Player player = WGEFUtils.wrapPlayer(localPlayer);
        StateFlag.State state = WGEFUtils.queryValue(player, player.getWorld(), set.getRegions(), WGEFlags.GLIDE);
        this.handleValue(player, state);
    }

    public void handleValue(Player player, StateFlag.State state) {
        if (state != null) {
            if (state == StateFlag.State.ALLOW)
                return;

            if (player.isGliding())
                player.setGliding(false);
        }
    }

}
