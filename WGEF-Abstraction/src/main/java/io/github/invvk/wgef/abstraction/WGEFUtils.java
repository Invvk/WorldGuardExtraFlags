package io.github.invvk.wgef.abstraction;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.FlagValueCalculator;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.util.NormativeOrders;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WGEFUtils {

    private static IWGFork fork;

    public static IWGFork getFork() {
        return fork;
    }

    public static void setFork(IWGFork communicator) {
        WGEFUtils.fork = communicator;
    }

    public static LocalPlayer wrapPlayer(Player player) {
        return WGEFUtils.getFork().wrap(player);
    }

    public static Player wrapPlayer(LocalPlayer player) {
        return ((BukkitPlayer) player).getPlayer();
    }

    public static boolean hasBypass(Player player, World world, ProtectedRegion region, Flag<?> flag) {
        if (player.hasMetadata("NPC"))
            return true;
        return player.hasPermission("worldguard.region.bypass." + world.getName() + "." + region.getId() + "." + flag.getName());
    }

    public static boolean isPartOfRegion(Player bukkitPlayer, ApplicableRegionSet set) {
        final LocalPlayer player = wrapPlayer(bukkitPlayer);
        for (ProtectedRegion region: set)
            if (region.isMember(player) || region.isOwner(player))
                return true;
        return false;
    }

    public static StateFlag.State queryState(Player player, World world, Set<ProtectedRegion> regions, StateFlag flag) {
        return WGEFUtils.createFlagValueCalculator(player, world, regions, flag)
                .queryState(WGEFUtils.wrapPlayer(player), flag);
    }

    public static <T> T queryValue(Player player, World world, Set<ProtectedRegion> regions, Flag<T> flag) {
        return WGEFUtils.createFlagValueCalculator(player, world, regions, flag).queryValue(WGEFUtils.wrapPlayer(player), flag);
    }

    public static <T> T queryValueUnchecked(Player player, World world, Set<ProtectedRegion> regions, Flag<T> flag) {
        return WGEFUtils.createFlagValueCalculatorUnchecked(world, regions, flag).queryValue(WGEFUtils.wrapPlayer(player), flag);
    }

    public static <T> FlagValueCalculator createFlagValueCalculator(Player player, World world, Set<ProtectedRegion> regions, Flag<T> flag) {
        final List<ProtectedRegion> checkForRegions = new ArrayList<>();
        regions.forEach(region -> {
            if (!WGEFUtils.hasBypass(player, world, region, flag))
                checkForRegions.add(region);
        });

        if (checkForRegions.size() != 1)
            NormativeOrders.sort(checkForRegions);

        ProtectedRegion global = WGEFUtils.getFork().getRegionContainer().get(world).getRegion(ProtectedRegion.GLOBAL_REGION);
        if (global != null) {
            if (WGEFUtils.hasBypass(player, world, global, flag))
                global = null;
        }

        return new FlagValueCalculator(checkForRegions, global);
    }

    public static <T> FlagValueCalculator createFlagValueCalculatorUnchecked(World world, Set<ProtectedRegion> regions, Flag<T> flag) {
        final List<ProtectedRegion> checkForRegions = List.copyOf(regions);
        ProtectedRegion global = WGEFUtils.getFork().getRegionContainer().get(world).getRegion(ProtectedRegion.GLOBAL_REGION);
        return new FlagValueCalculator(checkForRegions, global);
    }

    public static boolean isDeny(StateFlag.State state) {
        return state == StateFlag.State.DENY;
    }

    public static boolean isFAWEPresent() {
        try {
            Class.forName("com.fastasyncworldedit.core.regions.FaweMaskManager");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public static boolean isPAPIPresent() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

}
