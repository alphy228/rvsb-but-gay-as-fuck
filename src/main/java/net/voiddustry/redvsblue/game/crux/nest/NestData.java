package net.voiddustry.redvsblue.game.crux.nest;

import mindustry.gen.Player;
import mindustry.world.Tile;

public class NestData {
    public Tile tileOn;
    public Player owner;

    public NestData(Tile tileOn, Player owner) {
        this.tileOn = tileOn;
        this.owner = owner;
    }
}
