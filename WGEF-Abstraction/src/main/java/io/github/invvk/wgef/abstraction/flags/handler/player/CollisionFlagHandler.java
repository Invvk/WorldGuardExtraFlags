package io.github.invvk.wgef.abstraction.flags.handler.player;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import io.github.invvk.wgef.abstraction.flags.handler.AbstractFlagHandler;
import io.github.invvk.wgef.abstraction.flags.handler.CollisionTeamHandler;
import org.bukkit.entity.Player;

import java.util.Set;

public class CollisionFlagHandler extends AbstractFlagHandler<Boolean> {

    public static final CollisionFlagHandler.Factory FACTORY = new CollisionFlagHandler.Factory();

    private CollisionTeamHandler teamHandler;

    public static class Factory extends Handler.Factory<CollisionFlagHandler> {
        @Override
        public CollisionFlagHandler create(Session session) {
            return new CollisionFlagHandler(session);
        }
    }

    protected CollisionFlagHandler(Session session) {
        super(session, WGEFlags.DISABLE_COLLISION);
    }

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet set, Boolean value) {
        final Player player = WGEFUtils.wrapPlayer(localPlayer);
        this.teamHandler = new CollisionTeamHandler(player);
        Boolean collision = WGEFUtils.queryValue(player, player.getWorld(), set.getRegions(), WGEFlags.DISABLE_COLLISION);

        if (collision != null && collision)
            this.teamHandler.add();
    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet set, Boolean currentValue, Boolean lastValue, MoveType moveType) {
        final Player player = WGEFUtils.wrapPlayer(localPlayer);
        Boolean collision = WGEFUtils.queryValue(player, player.getWorld(), set.getRegions(), WGEFlags.DISABLE_COLLISION);

        if (collision != null && collision)
            this.teamHandler.add();
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet set, Set<ProtectedRegion> exited, Boolean lastValue, MoveType moveType) {
        Player player = WGEFUtils.wrapPlayer(localPlayer);
        this.teamHandler.remove();
        return true;
    }
}
