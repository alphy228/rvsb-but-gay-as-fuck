package net.voiddustry.redvsblue;

import mindustry.game.Team;
import mindustry.gen.Player;

@SuppressWarnings("unused")
public class PlayerData {
    private final String name, uuid, ip;
    private int score;
    private Team team;

    public PlayerData(String name, String uuid, String ip, Team team) {
        this.name = name;
        this.uuid = uuid;
        this.ip = ip;
        this.score = 10;
        this.team = team;
    }

    public PlayerData(Player player) {
        this(player.name(), player.uuid(), player.ip(), player.team());
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getIp() {
        return ip;
    }

    public int getScore() {
        return score;
    }

    public Team getTeam() {
        return team;
    }

    public int addScore(int amount) {
        return score += amount;
    }

    public int subtractScore(int amount) {
        return score -= amount;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
