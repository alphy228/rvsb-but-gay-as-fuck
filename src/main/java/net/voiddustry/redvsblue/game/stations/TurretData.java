package net.voiddustry.redvsblue.game.stations;

import mindustry.gen.Player;
import mindustry.world.Tile;

public class TurretData {
    private final Player owner;
    private final Tile tileOn;
    private int ammoClips;

    public TurretData(Player owner, Tile tileOn) {
        this.owner = owner;
        this.tileOn = tileOn;
        this.ammoClips = 1;
    }

    public Player getOwner() {
        return owner;
    }

    public Tile getTileOn() {
        return tileOn;
    }

    public int getClips() {
        return ammoClips;
    }

    public void setAmmoClips(int amount) {
        this.ammoClips = amount;
    }

    public void addAmmoClips(int amount) {
        this.ammoClips += amount;
    }

    public void removetAmmoCLips(int amount) {
        this.ammoClips -= amount;
    }
}
