package io.github.invvk.wgef.v7.worldedit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import org.bukkit.Bukkit;

public class WGEFExtentHandler extends AbstractDelegateExtent {
    protected final World weWorld;

    protected final org.bukkit.World world;
    protected final org.bukkit.entity.Player player;

    public WGEFExtentHandler(World world, Extent extent, Player player) {
        super(extent);

        this.weWorld = world;

        if (world instanceof BukkitWorld) {
            this.world = ((BukkitWorld) world).getWorld();
        } else {
            this.world = Bukkit.getWorld(world.getName());
        }

        this.player = Bukkit.getPlayer(player.getUniqueId());
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
        ApplicableRegionSet regions = WGEFUtils.getFork().getRegionContainer().get(this.weWorld)
                .getApplicableRegions(location);
        StateFlag.State state = WGEFUtils.queryState(this.player, this.world, regions.getRegions(), WGEFlags.WORLD_EDIT);
        if (WGEFUtils.isDeny(state))
            return super.setBlock(location, block);
        return false;
    }

}
