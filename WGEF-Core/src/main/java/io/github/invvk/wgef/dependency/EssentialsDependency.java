package io.github.invvk.wgef.dependency;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import io.github.invvk.wgef.abstraction.IManager;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.dependencies.IEssentialsDependency;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EssentialsDependency implements IEssentialsDependency {

    private final Essentials essentials;

    private boolean isEnabled;

    public static EssentialsDependency load(IManager iManager) {
        if (Bukkit.getPluginManager().getPlugin("Essentials") == null)
            return null;
        return new EssentialsDependency(iManager);
    }

    private EssentialsDependency(IManager iManager) {
        this.essentials = Essentials.getPlugin(Essentials.class);

        // Register flag
        WGEFUtils.getFork().getFlagReg().register(WGEFlags.GOD_MODE);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean isDependencyPresent() {
        return Bukkit.getPluginManager().getPlugin("Essentials") != null;
    }

    @Override
    public Essentials get() {
        return essentials;
    }

    @Override
    public User getUser(Player player) {
        return essentials.getUser(player);
    }

    @Override
    public User getUser(LocalPlayer player) {
        return essentials.getUser(((BukkitPlayer)player).getPlayer());
    }

}
