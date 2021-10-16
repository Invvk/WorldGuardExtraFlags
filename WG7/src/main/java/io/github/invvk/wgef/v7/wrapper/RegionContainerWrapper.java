package io.github.invvk.wgef.v7.wrapper;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import io.github.invvk.wgef.abstraction.wrapper.AbstractRegionManagerWrapper;
import io.github.invvk.wgef.abstraction.wrapper.IRegionContainerWrapper;
import io.github.invvk.wgef.abstraction.wrapper.IRegionQueryWrapper;
import org.bukkit.World;

import java.util.List;
import java.util.stream.Collectors;

public class RegionContainerWrapper implements IRegionContainerWrapper {

    @Override
    public IRegionQueryWrapper createQuery() {
        return new RegionQueryWrapper(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery());
    }

    @Override
    public AbstractRegionManagerWrapper get(World world) {
        return new RegionManagerWrapper(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)));
    }

    @Override
    public AbstractRegionManagerWrapper get(com.sk89q.worldedit.world.World world) {
        return new RegionManagerWrapper(WorldGuard.getInstance().getPlatform().getRegionContainer().get(world));
    }

    @Override
    public List<AbstractRegionManagerWrapper> getLoaded() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().getLoaded()
                .stream()
                .map(RegionManagerWrapper::new)
                .collect(Collectors.toList());
    }

}
