package net.voiddustry.redvsblue.evolution;

import mindustry.content.UnitTypes;
import mindustry.type.UnitType;

public enum Evolution {
    DAGGER(UnitTypes.dagger, 1, 1, new String[] { "mace", "pulsar", "risso", "poly" }),
    NOVA(UnitTypes.nova, 1, 3, new String[] { "mace", "pulsar", "risso", "poly" }),
    MERUI(UnitTypes.merui, 1, 5, new String[] { "mace", "pulsar", "risso", "poly" }),
    FLARE(UnitTypes.flare, 1, 10, new String[] { "mace", "pulsar", "risso", "poly" }),
    MONO(UnitTypes.mono, 1, 8, new String[] { "mace", "pulsar", "risso", "poly" }),

    MACE(UnitTypes.mace, 2, 20, new String[] { "stell" }),
    PULSAR(UnitTypes.pulsar, 2, 30, new String[] { "quasar" }),
    RISSO(UnitTypes.risso, 2, 50, new String[] { "zenith" }),
    POLY(UnitTypes.poly, 2, 40, new String[] { "retusa" }),

    STELL(UnitTypes.stell, 3, 10, new String[]{}),
    QUASAR(UnitTypes.stell, 3, 60, new String[]{}),
    ATRAX(UnitTypes.stell, 3, 30, new String[]{}),
    ZENITH(UnitTypes.stell, 3, 100, new String[]{}),
    RETUSA(UnitTypes.stell, 3, 100, new String[]{});

    public final UnitType unitType;
    public final int tier, cost;
    public final String[] evolutions;

    Evolution(mindustry.type.UnitType unitType, int tier, int cost, String[] evolutions) {
        this.unitType = unitType;
        this.tier = tier;
        this.cost = cost;
        this.evolutions = evolutions;
    }
}
