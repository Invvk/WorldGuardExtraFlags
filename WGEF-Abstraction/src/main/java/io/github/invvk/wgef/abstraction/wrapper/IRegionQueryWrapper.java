package io.github.invvk.wgef.abstraction.wrapper;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;

public interface IRegionQueryWrapper {

    RegionQuery getRegionQuery();
    ApplicableRegionSet getApplicableRegions(Location location);

}
