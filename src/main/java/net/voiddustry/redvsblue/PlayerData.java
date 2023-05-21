package net.voiddustry.redvsblue;

import mindustry.game.Team;
import mindustry.gen.Player;
import mindustry.gen.Unit;

@SuppressWarnings("unused")
public class PlayerData {
    private final String name, uuid, ip;
    private int score;
    private Unit unit;
    private Team team;
    private int evolutionStage;
    private boolean canEvolve;
    private boolean canConstruct;
    private int level;
    private int exp;
    private int maxExp;

    public PlayerData(String name, String uuid, String ip, Unit unit, Team team, Integer evolutionStage, Integer level, Integer exp, Integer maxExp) {
        this.name = name;
        this.uuid = uuid;
        this.ip = ip;
        this.unit = unit;
        this.score = 15;
        this.team = team;
        this.evolutionStage = 1;
        this.canEvolve = false;
        this.canConstruct = false;
        this.level = 1;
        this.exp = 0;
        this.maxExp = 20;
    }

    public PlayerData(Player player) {
        this(player.name(), player.uuid(), player.ip(), null, player.team(), 0, 1, 0, 20);
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

    public void setCanEvolve(boolean mode) {
        this.canEvolve = mode;
    }

    public boolean isCanEvolve() {
        return canEvolve;
    }

    public void setCanConstruct(boolean mode) {
        this.canConstruct = mode;
    }

    public boolean CanConstruct() {
        return canConstruct;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void addExp(int amount) {
        this.exp += amount;
    }

    public void setExp(int amount) {
        this.exp = amount;
    }

    public int getExp() {
        return exp;
    }


    public int getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(int amount) {
        this.maxExp = amount;
    }
}
