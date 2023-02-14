package net.voiddustry.redvsblue;

import mindustry.game.Team;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

@SuppressWarnings("unused")
public class PlayerData {
    private final String name, uuid, ip;
    private int score;
    private Unit unit;
    private Team team;
    private int evolutionStage;

    public PlayerData(String name, String uuid, String ip, Unit unit, Team team, Integer evolutionStage) {
        this.name = name;
        this.uuid = uuid;
        this.ip = ip;
        this.unit = unit;
        this.score = 10;
        this.team = team;
        this.evolutionStage = 1;
    }

    public PlayerData(Player player) {
        this(player.name(), player.uuid(), player.ip(), null, player.team(), 0);
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

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setEvolutionStage(int stage) {
        this.evolutionStage = stage;
    }

    public int getEvolutionStage() {
        return evolutionStage;
    }
}
