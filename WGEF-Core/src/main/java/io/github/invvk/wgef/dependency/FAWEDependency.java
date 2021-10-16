package io.github.invvk.wgef.dependency;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import io.github.invvk.wgef.abstraction.dependencies.IFAWEDependency;
import io.github.invvk.wgef.v7.worldedit.fawe.FAWEManager;
import io.github.invvk.wgef.v7.worldedit.WGEFExtentHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FAWEDependency implements IFAWEDependency {

    private final boolean enabled;

    @Nullable
    public static FAWEDependency load(Plugin plugin) {
        if (Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") == null)
            return null;
        return new FAWEDependency(plugin);
    }

    private FAWEDependency(Plugin plugin) {
        FaweAPI.addMaskManager(new FAWEManager(plugin));
        enabled = true;

        this.injectOption();
        this.cancelIntersectingExtents();
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isDependencyPresent() {
        return Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null;
    }

    public void injectOption() {
        // attempting to inject option 'allowed-plugins' automatically so the user doesn't have to.
        FileConfiguration weConfig = WorldEditPlugin.getInstance().getConfig();

        final String allowed_plugins_path = "extent.allowed-plugins";
        final String classpath = WGEFExtentHandler.class.getSimpleName();

        final List<String> allowed_plugins = weConfig.getStringList(allowed_plugins_path);

        if (allowed_plugins.contains(classpath))
            return;

        allowed_plugins.add(classpath);
        weConfig.set(allowed_plugins_path, allowed_plugins);
        WorldEditPlugin.getInstance().saveConfig();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fawe reload");
    }

    @Override
    public void cancelIntersectingExtents() {}
}
