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
import io.github.invvk.wgef.abstraction.flags.data.BooleanVal;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;

public class FlyFlagHandler extends AbstractFlagHandler<StateFlag.State> {

    public static Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<FlyFlagHandler> {

        @Override
        public FlyFlagHandler create(Session session) {
            return new FlyFlagHandler(session);
        }
    }

    private BooleanVal data;

    protected FlyFlagHandler(Session session) {
        super(session, WGEFlags.FLY);
    }

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet set, StateFlag.State value) {
        Player player = WGEFUtils.wrapPlayer(localPlayer);
        StateFlag.State state = WGEFUtils.queryValue(player, player.getWorld(), set.getRegions(), WGEFlags.FLY);
        this.handleValue(player, state);
    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet toSet, StateFlag.State currentValue, StateFlag.State lastValue, MoveType moveType) {
        Player player = WGEFUtils.wrapPlayer(localPlayer);
        StateFlag.State state = WGEFUtils.queryValue(player, player.getWorld(), toSet.getRegions(), WGEFlags.FLY);
        this.handleValue(player, state);
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> exited, StateFlag.State lastValue, MoveType moveType) {
        Player player = WGEFUtils.wrapPlayer(localPlayer);
        StateFlag.State state = WGEFUtils.queryValue(player, player.getWorld(), toSet.getRegions(), WGEFlags.FLY);
        this.handleValue(player, state);
        return true;
    }

    public BooleanVal getData() {
        return Optional.ofNullable(data)
                .orElse(new BooleanVal(false));
    }

    public void handleValue(Player player, StateFlag.State state) {
        if (state != null) {
            if (state == StateFlag.State.ALLOW)
                return;

            if (data == null)
                this.data = new BooleanVal(player.getAllowFlight());

            player.setAllowFlight(false);
            player.setFlying(false);
            return;
        }
        if (this.data != null) {
            boolean result = this.data.getDefault();
            player.setAllowFlight(result);
            player.setFlying(result);
            this.data = null;
        }
    }

}
