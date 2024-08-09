package net.voiddustry.redvsblue.evolution;

import mindustry.content.UnitTypes;
import mindustry.type.UnitType;

public enum UnitEvolutions {
    dagger("units.dagger", UnitTypes.dagger, null, 150, 0, 3, null, new String[]{"novager", "magger"});

    public String key;
    public UnitType first;
    public UnitType second;
    public int maxHp;
    public int armor;
    public int cost;
    public String ability;
    public String[] evolvesTo;

    UnitEvolutions(String key, UnitType first, UnitType second, int maxHp, int armor, int cost, String ability, String[] evolvesTo) {
        if (second == null) {
            second = first;
        }

        this.key = key;
        this.first = first;
        this.second = second;
        this.maxHp = maxHp;
        this.armor = armor;
        this.cost = cost;
        this.ability = ability;
        this.evolvesTo = evolvesTo;
    }
}
