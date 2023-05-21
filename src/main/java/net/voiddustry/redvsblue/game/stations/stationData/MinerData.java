package net.voiddustry.redvsblue.game.stations.stationData;

import mindustry.gen.Player;
import mindustry.world.Tile;

public class MinerData {
    private Player owner;
    private Tile tileOn;
    private int exp;
    private int maxExp;
    private int lvl;


    public MinerData(Player owner, Tile tileOn, Integer exp, Integer maxExp, Integer lvl) {
        this.owner = owner;
        this.tileOn = tileOn;
        this.exp = exp;
        this.maxExp = maxExp;
        this.lvl = lvl;
    }

    public MinerData(Player owner, Tile tileOn) {
        this(owner, tileOn, 0, 15, 1);
    }

    public Player getOwner() {
        return owner;
    }

    public Tile getTileOn() {
        return tileOn;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int amount) {
        this.exp = amount;
    }

    public void addExp(int amount) {
        this.exp += amount;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(int amount) {
        this.maxExp = amount;
    }

    public void setLvl(int amount) {
        this.lvl = amount;
    }

    public void addLvl() {
        this.lvl++;
    }

    public int getLvl() {
        return lvl;
    }

}
