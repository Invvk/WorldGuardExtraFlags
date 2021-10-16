package io.github.invvk.wgef.abstraction.wrapper;

import org.bukkit.World;

import java.util.List;

public interface IRegionContainerWrapper {


    IRegionQueryWrapper createQuery();
    AbstractRegionManagerWrapper get(World world);
    AbstractRegionManagerWrapper get(com.sk89q.worldedit.world.World world);

    List<AbstractRegionManagerWrapper> getLoaded();

}
