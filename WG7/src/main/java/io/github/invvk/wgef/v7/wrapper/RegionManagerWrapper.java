package io.github.invvk.wgef.v7.wrapper;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import io.github.invvk.wgef.abstraction.wrapper.AbstractRegionManagerWrapper;
import org.bukkit.Location;

public class RegionManagerWrapper extends AbstractRegionManagerWrapper {

    public RegionManagerWrapper(RegionManager regionManager) {
        super(regionManager);
    }

    @Override
    public ApplicableRegionSet getApplicableRegions(Location location) {
        return regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
    }

    @Override
    public ApplicableRegionSet getApplicableRegions(BlockVector3 location) {
        return regionManager.getApplicableRegions(location);
    }

}
