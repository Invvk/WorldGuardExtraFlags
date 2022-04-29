package io.github.invvk.wgef.abstraction.flags.handler.collision;

import io.github.invvk.wgef.abstraction.flags.handler.ICollisionHandler;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class BukkitCollisionHandler implements ICollisionHandler {

    private final Player player;
    private Team team;

    public BukkitCollisionHandler(Player player) {
        this.player = player;
    }

    private void checkTeam() {
        final Scoreboard scoreboard = player.getScoreboard();
        this.team = scoreboard.getTeam(TEAM_NAME);
        if (this.team == null) {
            this.team = scoreboard.registerNewTeam(TEAM_NAME);
        }
        this.team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    }

    @Override
    public void add() {
        this.checkTeam();
        if (!this.team.hasEntry(player.getName()))
            this.team.addEntry(player.getName());
    }

    @Override
    public void remove() {
        this.checkTeam();
        if (this.team.hasEntry(player.getName()))
            this.team.removeEntry(player.getName());
    }

}
