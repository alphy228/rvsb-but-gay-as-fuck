package net.voiddustry.redvsblue.domain;

import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class PlayerBoss {
    public Player player;
    public Unit unit;
    public UnitType unitType;

    public PlayerBoss(Player player) {
        this.player = player;
        this.unit = player.unit();
        this.unitType = player.unit().type;
    }
}
