package Units.Tier;

import mindustry.content.UnitTypes;

import mindustry.type.UnitType;

@SuppressWarnings("unused")
public enum FirstTier {

    DAGGER(UnitTypes.dagger, 1),
    NOVA(UnitTypes.nova, 3),
    MERUI(UnitTypes.merui, 5),
    FLARE(UnitTypes.flare, 10),
    MONO(UnitTypes.mono, 8),

    debug(UnitTypes.dagger, 0);

    public final UnitType unitType;
    public final int cost;

    FirstTier(UnitType unitType, int cost) {
        this.unitType = unitType;
        this.cost = cost;
    }
}
