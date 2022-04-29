package io.github.invvk.wgef.abstraction.flags.handler.collision;

import io.github.invvk.wgef.abstraction.flags.handler.ICollisionHandler;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import org.bukkit.entity.Player;

public class TABCollisionHandler implements ICollisionHandler {

    private final TabPlayer player;

    public TABCollisionHandler(Player player) {
        this.player = TabAPI.getInstance().getPlayer(player.getUniqueId());
    }

    @Override
    public void add() {
        TabAPI.getInstance().getTeamManager()
                .setCollisionRule(player, false);
    }

    @Override
    public void remove() {
        TabAPI.getInstance().getTeamManager()
                .setCollisionRule(player, true);
    }

}
