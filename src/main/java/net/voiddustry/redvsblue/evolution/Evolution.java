package net.voiddustry.redvsblue.evolution;

import mindustry.content.UnitTypes;
import mindustry.type.UnitType;

import static net.voiddustry.redvsblue.util.UnitsConfig.multp;

public enum Evolution {

    NOVA(UnitTypes.nova, 1, 3*multp, new String[] { "nova", "merui", "flare", "dagger", "pulsar", "atrax", "risso" }),
    MERUI(UnitTypes.merui, 1, 5*multp, new String[] { "nova", "merui", "flare", "dagger", "pulsar", "atrax", "risso" }),
    FLARE(UnitTypes.flare, 1, 10*multp, new String[] { "nova", "merui", "flare", "dagger", "pulsar", "atrax", "risso" }),
    MONO(UnitTypes.mono, 1, 8*multp, new String[] { "poly" }),

    DAGGER(UnitTypes.dagger, 2, 5*multp, new String[] { "mace" }),
    PULSAR(UnitTypes.pulsar, 2, 30*multp, new String[] { "quasar" }),
    ATRAX(UnitTypes.atrax, 2, 30*multp, new String[] { "cleroi" }),
    RISSO(UnitTypes.risso, 2, 50*multp, new String[] { "zenith" }),
    POLY(UnitTypes.poly, 2, 40*multp, new String[] { "retusa" }),

    MACE(UnitTypes.mace, 3, 30*multp, new String[] { "fortress" }),
    QUASAR(UnitTypes.quasar, 3, 50*multp, new String[] { "vela" }),
    CLEROI(UnitTypes.cleroi, 3, 30*multp, new String[] { "spiroct" }),
    ZENITH(UnitTypes.zenith, 3, 100*multp, new String[] { "minke" }),
    RETUSA(UnitTypes.retusa, 3, 100*multp, new String[] { "oxynoe" }),

    FORTRESS(UnitTypes.fortress, 4, 30*multp, new String[] { "locus" }),
    VELA(UnitTypes.vela, 4, 190*multp, new String[] { "corvus" }),
    SPIROCT(UnitTypes.spiroct, 4, 60*multp, new String[] { "tecta" }),
    MINKE(UnitTypes.minke, 4, 100*multp, new String[] { "elude" }),
    OXYNOE(UnitTypes.oxynoe, 4, 150*multp, new String[] { "mega" }),

    LOCUS(UnitTypes.locus, 5, 50*multp, new String[] { "precept" }),
    CORVUS(UnitTypes.corvus, 5, 500*multp, new String[] { "aegires" }),
    TECTA(UnitTypes.tecta, 5, 140*multp, new String[] { "arkyid" }),
    ELUDE(UnitTypes.elude, 5, 20*multp, new String[] { "avert" }),
    MEGA(UnitTypes.mega, 5, 150*multp, new String[] { "cyerce" }),

    PRECEPT(UnitTypes.precept, 6, 50*multp, new String[] { "scepter" }),
    AEGIRES(UnitTypes.aegires, 6, 500*multp, new String[] { "navanax" }),
    ARKYID(UnitTypes.arkyid, 6, 200*multp, new String[] { "toxopid" }),
    AVERT(UnitTypes.avert, 6, 30*multp, new String[] { "bryde" }),
    CYERCE(UnitTypes.cyerce, 6, 500*multp, new String[] { "quad" }),

    SCEPTER(UnitTypes.scepter, 7, 80*multp, new String[] { "vanquish" }),
    NAVANAX(UnitTypes.navanax, 7, 500*multp, new String[] {}),
    TOXOPID(UnitTypes.toxopid, 7, 350*multp, new String[] { "nuke boy", "collaris" }),
    BRYDE(UnitTypes.bryde, 7, 200*multp, new String[] { "antumbra", "obviate" }),
    QUAD(UnitTypes.quad, 7, 200*multp, new String[] { "oct" }),

    VANQUISH(UnitTypes.vanquish, 8, 200*multp, new String[] { "reign" }),
    ANTHICUS(UnitTypes.anthicus, 8, 10*multp, new String[] {}),
    COLLARIS(UnitTypes.collaris, 8, 1300*multp, new String[] {}),
    ANTUMBRA(UnitTypes.antumbra, 8, 200*multp, new String[] { "sei" }),
    OBVIATE(UnitTypes.obviate, 8, 100*multp, new String[] { "quell" }),
    OCT(UnitTypes.oct, 8, 2000*multp, new String[] {}),

    REIGN(UnitTypes.reign, 9, 350*multp, new String[] { "conquer" }),
    SEI(UnitTypes.sei, 9, 200*multp, new String[] { "eclipse" }),
    QUELL(UnitTypes.quell, 9, 200*multp, new String[] { "disrupt" }),

    CONQUER(UnitTypes.conquer, 10, 500*multp, new String[] {}),
    ECLIPSE(UnitTypes.eclipse, 10, 300*multp, new String[] { "omura" }),
    DISRUPT(UnitTypes.disrupt, 10, 1500*multp, new String[] {}),

    OMURA(UnitTypes.omura, 11, 2000*multp, new String[] {});

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
