package io.github.invvk.wgef.abstraction;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import io.github.invvk.wgef.abstraction.wrapper.AbstractSessionManagerWrapper;
import io.github.invvk.wgef.abstraction.wrapper.IRegionContainerWrapper;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface IWGFork {

     void enable(final JavaPlugin plugin);

    FlagRegistry getFlagReg();
    AbstractSessionManagerWrapper getSessionManager();
    IRegionContainerWrapper getRegionContainer();

    LocalPlayer wrap(Player player);

    <T> SetFlag<T> getCustomSetFlag(String name, Flag<T> flag);

}
