package net.voiddustry.redvsblue.evolution;

import mindustry.content.UnitTypes;
import mindustry.type.UnitType;

public enum Evolution {
    NOVA(UnitTypes.nova, 1, 3, new String[] { "nova", "merui", "flare", "dagger", "pulsar", "atrax", "risso","poly" }),
    MERUI(UnitTypes.merui, 1, 5, new String[] { "nova", "merui", "flare", "dagger", "pulsar", "atrax", "risso", "poly" }),
    FLARE(UnitTypes.flare, 1, 10, new String[] { "nova", "merui", "flare", "dagger", "pulsar", "atrax", "risso", "poly" }),
    MONO(UnitTypes.mono, 1, 8, new String[] { "nova", "merui", "flare", "dagger", "pulsar", "atrax", "risso", "poly" }),

    DAGGER(UnitTypes.dagger, 2, 5, new String[] { "mace" }),
    PULSAR(UnitTypes.pulsar, 2, 30, new String[] { "quasar" }),
    ATRAX(UnitTypes.atrax, 2, 30, new String[] { "cleroi" }),
    RISSO(UnitTypes.risso, 2, 50, new String[] { "zenith" }),
    POLY(UnitTypes.poly, 2, 40, new String[] { "retusa" }),

    MACE(UnitTypes.mace, 3, 30, new String[] { "fortress" }),
    QUASAR(UnitTypes.quasar, 3, 50, new String[] { "vela" }),
    CLEROI(UnitTypes.cleroi, 3, 30, new String[] { "spiroct" }),
    ZENITH(UnitTypes.zenith, 3, 100, new String[] { "minke" }),
    RETUSA(UnitTypes.retusa, 3, 100, new String[] { "oxynoe" }),

    FORTRESS(UnitTypes.fortress, 4, 30, new String[] { "locus" }),
    VELA(UnitTypes.vela, 4, 190, new String[] { "corvus" }),
    SPIROCT(UnitTypes.spiroct, 4, 60, new String[] { "tecta" }),
    MINKE(UnitTypes.minke, 4, 100, new String[] { "elude" }),
    OXYNOE(UnitTypes.oxynoe, 4, 150, new String[] { "mega" }),

    LOCUS(UnitTypes.locus, 5, 50, new String[] { "precept" }),
    CORVUS(UnitTypes.corvus, 5, 500, new String[] { "aegires" }),
    TECTA(UnitTypes.tecta, 5, 140, new String[] { "arkyid" }),
    ELUDE(UnitTypes.elude, 5, 20, new String[] { "avert" }),
    MEGA(UnitTypes.mega, 5, 150, new String[] { "cyerce" }),

    PRECEPT(UnitTypes.precept, 6, 50, new String[] { "scepter" }),
    AEGIRES(UnitTypes.aegires, 6, 500, new String[] { "navanax" }),
    ARKYID(UnitTypes.arkyid, 6, 200, new String[] { "toxopid" }),
    AVERT(UnitTypes.avert, 6, 30, new String[] { "bryde" }),
    CYERCE(UnitTypes.cyerce, 6, 500, new String[] { "quad" }),

    SCEPTER(UnitTypes.scepter, 7, 80, new String[] { "vanquish" }),
    NAVANAX(UnitTypes.navanax, 7, 500, new String[] {}),
    TOXOPID(UnitTypes.toxopid, 7, 350, new String[] { "nuke boy", "collaris" }),
    BRYDE(UnitTypes.bryde, 7, 200, new String[] { "antumbra", "obviate" }),
    QUAD(UnitTypes.quad, 7, 200, new String[] { "oct" }),

    VANQUISH(UnitTypes.vanquish, 8, 200, new String[] { "reign" }),
    ANTHICUS(UnitTypes.anthicus, 8, 0, new String[] {}),
    COLLARIS(UnitTypes.collaris, 8, 1300, new String[] {}),
    ANTUMBRA(UnitTypes.antumbra, 8, 200, new String[] { "sei" }),
    OBVIATE(UnitTypes.obviate, 8, 100, new String[] { "quell" }),
    OCT(UnitTypes.oct, 8, 2000, new String[] {}),

    REIGN(UnitTypes.reign, 9, 350, new String[] { "conquer" }),
    SEI(UnitTypes.sei, 9, 200, new String[] { "eclipse" }),
    QUELL(UnitTypes.quell, 9, 200, new String[] { "disrupt" }),

    CONQUER(UnitTypes.conquer, 10, 500, new String[] {}),
    ECLIPSE(UnitTypes.eclipse, 10, 300, new String[] { "omura" }),
    DISRUPT(UnitTypes.disrupt, 10, 1500, new String[] {}),

    OMURA(UnitTypes.omura, 11, 2000, new String[] {});

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
