package net.voiddustry.redvsblue.Admin.LogTypes;

import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import net.voiddustry.redvsblue.Admin.LogEntry;

@SuppressWarnings("unused")
public class UnitKillEntry extends LogEntry {
    private final UnitType killer;
    private final UnitType victim;
    private final Team killerTeam;
    private final Team victimTeam;
    private final Tile pos;

    public UnitKillEntry(UnitType killer, UnitType victim, Team killerTeam, Team victimTeam, Tile pos) {
        this.killer = killer;
        this.victim = victim;
        this.killerTeam = killerTeam;
        this.victimTeam = victimTeam;
        this.pos = pos;
    }

    public UnitKillEntry(Unit killer, Unit victim) {
        this(killer.type(), victim.type(), killer.team(), victim.team(), victim.tileOn());
    }
}
