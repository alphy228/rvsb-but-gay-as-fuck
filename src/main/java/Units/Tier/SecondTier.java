package Units.Tier;

import mindustry.content.UnitTypes;

import mindustry.type.UnitType;

@SuppressWarnings("unused")
public enum SecondTier {

    NACE(UnitTypes.mace, 20),
    PULSAR(UnitTypes.pulsar, 30),
    RISSO(UnitTypes.risso, 50),
    POLY(UnitTypes.poly, 40),

    debug(UnitTypes.dagger, 0);

    public final UnitType unitType;
    public final int cost;

    SecondTier(UnitType unitType, int cost) {
        this.unitType = unitType;
        this.cost = cost;
    }
}
