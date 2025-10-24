package net.voiddustry.redvsblue.evolution;

import mindustry.content.UnitTypes;
import mindustry.type.UnitType;

import static net.voiddustry.redvsblue.util.UnitsConfig.multp;

public enum Evolution {

    DAGGER(UnitTypes.dagger, 1, 2*multp, new String[] { "atrax", "mace", "elude", "flare", "nova", "mono" }),
    NOVA(UnitTypes.nova, 1, 3*multp, new String[] { "pulsar", "elude", "flare", "dagger", "mono" }),
    ELUDE(UnitTypes.elude, 1, 5*multp, new String[] { "atrax", "nova", "flare", "dagger", "mono" }),
    FLARE(UnitTypes.flare, 1, 10*multp, new String[] { "risso", "nova", "elude", "dagger", "mono" }),
    MONO(UnitTypes.mono, 1, 5*multp, new String[] { "poly", "nova", "elude", "dagger" }),

    PULSAR(UnitTypes.pulsar, 2, 10*multp, new String[] { "quasar" }),
    ATRAX(UnitTypes.atrax, 2, 18*multp, new String[] { "cleroi" }),
    RISSO(UnitTypes.risso, 2, 36*multp, new String[] { "zenith" }),
    POLY(UnitTypes.poly, 2, 21*multp, new String[] { "retusa" }),

    MACE(UnitTypes.mace, 3, 20*multp, new String[] { "fortress"}),
    QUASAR(UnitTypes.quasar, 3, 75*multp, new String[] { "vela" }),
    CLEROI(UnitTypes.cleroi, 3, 55*multp, new String[] { "spiroct" }),
    ZENITH(UnitTypes.zenith, 3, 70*multp, new String[] { "minke" }),
    RETUSA(UnitTypes.retusa, 3, 15*multp, new String[] { "oxynoe" }),

    FORTRESS(UnitTypes.fortress, 4, 35*multp, new String[] { "locus" }),
    VELA(UnitTypes.vela, 4, 180*multp, new String[] { "corvus" }),
    SPIROCT(UnitTypes.spiroct, 4, 95*multp, new String[] { "toxopid" }),
    MINKE(UnitTypes.minke, 4, 90*multp, new String[] { "avert" }),
    OXYNOE(UnitTypes.oxynoe, 4, 35*multp, new String[] { "mega" }),

    LOCUS(UnitTypes.locus, 5, 45*multp, new String[] { "precept" }),
    CORVUS(UnitTypes.corvus, 5, 360*multp, new String[] { "aegires" }),
    MEGA(UnitTypes.mega, 5, 100*multp, new String[] { "cyerce" }),

    PRECEPT(UnitTypes.precept, 6, 60*multp, new String[] { "scepter" }),
    AEGIRES(UnitTypes.aegires, 6, 450*multp, new String[] { "navanax" }),
    AVERT(UnitTypes.avert, 6, 27*multp, new String[] { "bryde" }),
    CYERCE(UnitTypes.cyerce, 6, 130*multp, new String[] { "quad" }),

    SCEPTER(UnitTypes.scepter, 7, 105*multp, new String[] { "vanquish" }),
    NAVANAX(UnitTypes.navanax, 7, 450*multp, new String[] { "flare", "elude", "dagger" }),
    TOXOPID(UnitTypes.toxopid, 7, 415*multp, new String[] { "collaris" }),
    BRYDE(UnitTypes.bryde, 7, 180*multp, new String[] { "antumbra", "obviate" }),
    QUAD(UnitTypes.quad, 7, 480*multp, new String[] { "oct" }),

    VANQUISH(UnitTypes.vanquish, 8, 250*multp, new String[] { "reign" }),
    COLLARIS(UnitTypes.collaris, 8, 1100*multp, new String[] { "nova", "flare", "dagger", "mono" }),
    ANTUMBRA(UnitTypes.antumbra, 8, 180*multp, new String[] { "eclipse" }),
    OBVIATE(UnitTypes.obviate, 8, 90*multp, new String[] { "quell" }),
    OCT(UnitTypes.oct, 8, 1500*multp, new String[] { "elude", "nova", "flare", "dagger" }),

    REIGN(UnitTypes.reign, 9, 450*multp, new String[] { "conquer" }),
    ECLIPSE(UnitTypes.eclipse, 9, 270*multp, new String[] { "sei" }),
    QUELL(UnitTypes.quell, 9, 180*multp, new String[] { "disrupt" }),

    CONQUER(UnitTypes.conquer, 10, 450*multp, new String[] { "elude", "nova", "flare", "mono" }),
    SEI(UnitTypes.sei, 10, 270*multp, new String[] { "omura" }),
    DISRUPT(UnitTypes.disrupt, 10, 900*multp, new String[] { "nova", "flare", "dagger"}),

    OMURA(UnitTypes.omura, 11, 1650*multp, new String[] { "elude", "nova", "dagger", "mono" });

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
