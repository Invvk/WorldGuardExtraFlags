package io.github.invvk.wgef.abstraction.flags.handler.essentials;

import com.earth2me.essentials.User;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import io.github.invvk.wgef.abstraction.IManager;
import io.github.invvk.wgef.abstraction.dependencies.IEssentialsDependency;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import io.github.invvk.wgef.abstraction.flags.handler.AbstractFlagHandler;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import org.bukkit.entity.Player;

import java.util.Set;

public class GodModeHandler extends AbstractFlagHandler<StateFlag.State> {

    public static Factory FACTORY(IManager manager) {
        return new Factory(manager);
    }

    private final IManager manager;

    public static class Factory extends Handler.Factory<GodModeHandler> {

        private final IManager manager;
        public Factory(IManager manager) {
            this.manager = manager;
        }

        @Override
        public GodModeHandler create(Session session) {
            return new GodModeHandler(session, manager);
        }
    }

    protected GodModeHandler(Session session, IManager manager) {
        super(session, WGEFlags.GOD_MODE);
        this.manager = manager;
    }

    private Boolean original, isEnabled;

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet set, StateFlag.State value) {
        this.updateGodMode(localPlayer, set.getRegions());
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, StateFlag.State currentValue, StateFlag.State lastValue, MoveType moveType) {
        this.updateGodMode(player, toSet.getRegions());
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> exited, StateFlag.State lastValue, MoveType moveType) {
        this.updateGodMode(player, toSet.getRegions());
        return true;
    }

    private void updateGodMode(LocalPlayer localPlayer, Set<ProtectedRegion> toSet) {
        Player player = WGEFUtils.wrapPlayer(localPlayer);

        if (manager.getEssentials().isEmpty())
            return;

        IEssentialsDependency essentials = manager.getEssentials().get();

        User user = essentials.getUser(localPlayer);

        StateFlag.State state = WGEFUtils.queryState(player, player.getWorld(), toSet, WGEFlags.GOD_MODE);

        if (state == null) {
            if (original != null){
                user.setGodModeEnabled(original);
                original = null;
            }
            return;
        }

        if (original == null)
            original = user.isGodModeEnabled();

        this.isEnabled = state == StateFlag.State.ALLOW;

        if (user.isGodModeEnabled())
            user.setGodModeEnabled(isEnabled);
    }

    public Boolean isEnabled() {
        return this.isEnabled;
    }

}
