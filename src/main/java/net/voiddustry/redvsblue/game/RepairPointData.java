package net.voiddustry.redvsblue.game;

import mindustry.gen.Player;
import mindustry.world.Tile;

public class RepairPointData {
    private Player owner;
    private Tile tileOn;

    public RepairPointData(Player owner, Tile tileOn) {
        this.owner = owner;
        this.tileOn = tileOn;
    }

    public Player getOwner() {
        return owner;
    }

    public Tile getTileOn() {
        return tileOn;
    }
}