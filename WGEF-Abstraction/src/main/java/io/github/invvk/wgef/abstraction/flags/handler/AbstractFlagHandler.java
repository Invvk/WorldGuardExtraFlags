package io.github.invvk.wgef.abstraction.flags.handler;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import java.util.Set;

public abstract class AbstractFlagHandler<T> extends Handler {

    private final Flag<T> flag;
    private T lastValue;

    protected AbstractFlagHandler(Session session, Flag<T> flag) {
        super(session);
        this.flag = flag;
    }

    @Override
    public final void initialize(LocalPlayer player, Location current, ApplicableRegionSet set) {
        lastValue = set.queryValue(player, flag);
        onInitialValue(player, set, lastValue);
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        if (entered.isEmpty() && exited.isEmpty()
                && from.getExtent().equals(to.getExtent())) { // sets don't include global regions - check if those changed
            return true; // no changes to flags if regions didn't change
        }

        T currentValue = toSet.queryValue(player, flag);
        boolean allowed = true;

        if (currentValue == null && lastValue != null) {
            allowed = onAbsentValue(player, from, to, toSet, exited, lastValue, moveType);
        } else if (currentValue != null && currentValue != lastValue) {
            allowed = onSetValue(player, from, to, toSet, currentValue, lastValue, moveType);
        }

        if (allowed) {
            lastValue = currentValue;
        }

        return allowed;
    }

    protected abstract void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet set, T value);

    protected abstract boolean onSetValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet set, T currentValue, T lastValue, MoveType moveType);

    protected abstract boolean onAbsentValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet set, Set<ProtectedRegion> exited, T lastValue, MoveType moveType);

}
