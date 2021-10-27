package io.github.invvk.wgef.v7.worldedit.fawe;

import com.fastasyncworldedit.core.regions.FaweMask;
import com.fastasyncworldedit.core.regions.FaweMaskManager;
import com.fastasyncworldedit.core.regions.RegionWrapper;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.AbstractRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.wrapper.AbstractRegionManagerWrapper;
import io.github.invvk.wgef.abstraction.wrapper.IRegionContainerWrapper;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

public class FAWEManager extends FaweMaskManager {

    public FAWEManager(Plugin plugin) {
        super(plugin.getName());
    }

    public ProtectedRegion getRegion(LocalPlayer player, Location location) {
        IRegionContainerWrapper regionContainer = WGEFUtils.getFork().getRegionContainer();
        if (regionContainer == null)
            return null;
        AbstractRegionManagerWrapper regionManager = regionContainer.get(player.getWorld());
        if (regionManager == null)
            return null;
        final ProtectedRegion region = regionManager.getRegion("__global__");
        if (region != null)
            return region;
        final ApplicableRegionSet regions = regionManager.getApplicableRegions(BlockVector3.at(
                location.getX(),
                location.getY(),
                location.getZ()
        ));
        if (!regions.testState(player, Flags.BUILD, Flags.BLOCK_PLACE, Flags.BLOCK_BREAK)) {
            return null;
        }
        for (ProtectedRegion test : regions) {
            if (isAllowed(player, test)) {
                return test;
            }
        }
        return null;
    }

    @SuppressWarnings({"deprecation"})
    public boolean isAllowed(LocalPlayer localplayer, ProtectedRegion region) {
        //Check if player is the owner of the region, the region's ID contains the player's name (why?), or if the region's owners contains "*".
        if (region.isOwner(localplayer) || region.isOwner(localplayer.getName())) {
            return true;
        } else if (region.getId().toLowerCase(Locale.ROOT).equals(localplayer.getName().toLowerCase(Locale.ROOT))) {
            return true;
        } else if (region.getId().toLowerCase(Locale.ROOT).contains(localplayer.getName().toLowerCase(Locale.ROOT) + "//")) {
            return true;
        } else if (region.isOwner("*")) {
            return true;
        }

        //Check if the player has the FAWE permission for editing in WG regions as member, then checking member status.
        if (localplayer.hasPermission("fawe.worldguard.member")) {
            if (region.isMember(localplayer) || region.isMember(localplayer.getName())) {
                return true;
            } else {
                return region.isMember("*");
            }
        }
        return false;
    }

    @Override
    public FaweMask getMask(Player wePlayer, MaskType type, boolean isWhiteListed) {
        final ProtectedRegion region = this.getRegion(WorldGuardPlugin.inst().wrapPlayer(BukkitAdapter.adapt(wePlayer)),
                BukkitAdapter.adapt(wePlayer.getLocation()));
        return new FaweMask(adapt(region)) {
            @Override
            public boolean isValid(Player player, MaskType type) {
                return true;
            }
        };
    }

    private static class AdaptedRegion extends AbstractRegion {

        private final ProtectedRegion region;

        public AdaptedRegion(ProtectedRegion region) {
            super(null);
            this.region = region;
        }

        @Override
        public BlockVector3 getMinimumPoint() {
            return region.getMinimumPoint();
        }

        @Override
        public BlockVector3 getMaximumPoint() {
            return region.getMaximumPoint();
        }

        @Override
        public void expand(BlockVector3... changes) {
            throw new UnsupportedOperationException("Region is immutable");
        }

        @Override
        public void contract(BlockVector3... changes) {
            throw new UnsupportedOperationException("Region is immutable");
        }

        @Override
        public boolean contains(BlockVector3 position) {
            return region.contains(position);
        }

    }

    @SuppressWarnings("deprecation")
    private static Region adapt(ProtectedRegion region) {
        if (region instanceof ProtectedCuboidRegion) {
            return new CuboidRegion(region.getMinimumPoint(), region.getMaximumPoint());
        }
        if (region instanceof GlobalProtectedRegion) {
            return RegionWrapper.GLOBAL();
        }
        if (region instanceof ProtectedPolygonalRegion casted) {
            BlockVector3 max = region.getMaximumPoint();
            BlockVector3 min = region.getMinimumPoint();
            return new Polygonal2DRegion(null, casted.getPoints(), min.getBlockY(), max.getBlockY());
        }
        return new FAWEManager.AdaptedRegion(region);
    }
}
