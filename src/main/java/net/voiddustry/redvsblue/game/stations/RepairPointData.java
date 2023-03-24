package net.voiddustry.redvsblue.game.stations;

import mindustry.gen.Player;
import mindustry.world.Tile;

public class RepairPointData {
    private final Player owner;
    private final Tile tileOn;

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