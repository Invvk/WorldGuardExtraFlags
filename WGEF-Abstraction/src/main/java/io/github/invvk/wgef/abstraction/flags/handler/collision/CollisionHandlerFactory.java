package io.github.invvk.wgef.abstraction.flags.handler.collision;

import io.github.invvk.wgef.abstraction.flags.handler.ICollisionHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CollisionHandlerFactory {

    private static final boolean TAB_ENABLED;

    static {
        TAB_ENABLED = Bukkit.getPluginManager().getPlugin("TAB") != null;
    }

    public static ICollisionHandler construct(Player player) {
        return TAB_ENABLED ? new TABCollisionHandler(player) : new BukkitCollisionHandler(player);
    }

}
