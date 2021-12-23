package io.github.invvk.wgef.abstraction.flags.handler;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class CollisionTeamHandler {

    public static final String TEAM_NAME = "WGEF_NO_COLLISION";

    private Team team;

    private final Player player;
    private boolean contains;

    public CollisionTeamHandler(Player player) {
        this.player = player;
    }

    public void checkTeam() {
        final Scoreboard scoreboard = this.getScoreboard();
        this.team = scoreboard.getTeam(TEAM_NAME);
        if (this.team == null) {
            this.team = scoreboard.registerNewTeam(TEAM_NAME);
        }
        this.team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    }

    public void add() {
        this.checkTeam();
        if (!this.team.hasEntry(player.getName())) {
            this.team.addEntry(player.getName());
            System.out.println("ADDED");
        }
        this.contains = true;
    }

    public void remove() {
        this.checkTeam();
        if (this.team.hasEntry(player.getName())) {
            this.team.removeEntry(player.getName());
            System.out.println("REMOVED");
        }
        this.contains = false;
    }

    public boolean isContains() {
        return contains;
    }

    public Scoreboard getScoreboard() {
        return player.getScoreboard();
    }

}
