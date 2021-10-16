package io.github.invvk.wgef.abstraction.dependencies;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.sk89q.worldguard.LocalPlayer;
import org.bukkit.entity.Player;

public interface IEssentialsDependency extends DependencyStatus {

    Essentials get();
    User getUser(Player player);
    User getUser(LocalPlayer player);
}
