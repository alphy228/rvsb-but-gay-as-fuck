package net.voiddustry.redvsblue.evolution;

import mindustry.content.UnitTypes;
import mindustry.type.UnitType;

import static net.voiddustry.redvsblue.util.UnitsConfig.multp;

public enum Evolution {

    DAGGER(UnitTypes.dagger, 1, 2*multp, new String[] { "atrax", "mace", "elude", "flare", "nova", "mono" }, 1),
    NOVA(UnitTypes.nova, 1, 3*multp, new String[] { "pulsar", "elude", "flare", "dagger", "mono" }), 1,
    ELUDE(UnitTypes.elude, 1, 10*multp, new String[] { "atrax", "nova", "flare", "dagger", "mono" }, 1),
    FLARE(UnitTypes.flare, 1, 6*multp, new String[] { "risso", "nova", "elude", "dagger", "mono" }, 1),
    MONO(UnitTypes.mono, 1, 5*multp, new String[] { "poly", "nova", "elude", "dagger" }, 1),

    PULSAR(UnitTypes.pulsar, 2, 25*multp, new String[] { "quasar" }, 1),
    ATRAX(UnitTypes.atrax, 2, 18*multp, new String[] { "cleroi" }, 1),
    RISSO(UnitTypes.risso, 2, 45*multp, new String[] { "zenith" }, 3),
    POLY(UnitTypes.poly, 2, 21*multp, new String[] { "retusa" }, 2),

    MACE(UnitTypes.mace, 3, 20*multp, new String[] { "fortress"}, 2),
    QUASAR(UnitTypes.quasar, 3, 60*multp, new String[] { "vela" }, 3),
    CLEROI(UnitTypes.cleroi, 3, 65*multp, new String[] { "spiroct" }, 3),
    ZENITH(UnitTypes.zenith, 3, 70*multp, new String[] { "minke" }, 3),
    RETUSA(UnitTypes.retusa, 3, 15*multp, new String[] { "oxynoe" }, 3),

    FORTRESS(UnitTypes.fortress, 4, 48*multp, new String[] { "locus" }, 3),
    VELA(UnitTypes.vela, 4, 170*multp, new String[] { "corvus" }, 6),
    SPIROCT(UnitTypes.spiroct, 4, 95*multp, new String[] { "toxopid" }, 4),
    MINKE(UnitTypes.minke, 4, 90*multp, new String[] { "avert" }, 3),
    OXYNOE(UnitTypes.oxynoe, 4, 35*multp, new String[] { "mega" }, 3),

    LOCUS(UnitTypes.locus, 5, 55*multp, new String[] { "precept" }, 4),
    CORVUS(UnitTypes.corvus, 5, 300*multp, new String[] { "aegires" }, 7),
    MEGA(UnitTypes.mega, 5, 100*multp, new String[] { "cyerce" }, 4),

    PRECEPT(UnitTypes.precept, 6, 60*multp, new String[] { "scepter" }, 4),
    AEGIRES(UnitTypes.aegires, 6, 450*multp, new String[] { "navanax" }, 8),
    AVERT(UnitTypes.avert, 6, 27*multp, new String[] { "bryde" , "obviate" }, 4),
    CYERCE(UnitTypes.cyerce, 6, 150*multp, new String[] { "quad" }, 5),

    SCEPTER(UnitTypes.scepter, 7, 105*multp, new String[] { "vanquish" }, 6),
    NAVANAX(UnitTypes.navanax, 7, 450*multp, new String[] { "flare", "elude", "dagger" }, , 9),
    TOXOPID(UnitTypes.toxopid, 7, 415*multp, new String[] { "collaris" }, 8),
    BRYDE(UnitTypes.bryde, 7, 180*multp, new String[] { "antumbra" }, 5),
    OBVIATE(UnitTypes.obviate, 7, 90*multp, new String[] { "quell" }, 5),
    QUAD(UnitTypes.quad, 7, 450*multp, new String[] { "oct" }, 7),

    VANQUISH(UnitTypes.vanquish, 8, 250*multp, new String[] { "reign" }, 7),
    COLLARIS(UnitTypes.collaris, 8, 1170*multp, new String[] { "nova", "flare", "dagger", "mono" }, 7),
    ANTUMBRA(UnitTypes.antumbra, 8, 180*multp, new String[] { "eclipse" }, 6),
    OCT(UnitTypes.oct, 8, 1500*multp, new String[] { "elude", "nova", "flare", "dagger" }, 9),
    QUELL(UnitTypes.quell, 8, 180*multp, new String[] { "disrupt" }, 6),

    REIGN(UnitTypes.reign, 9, 450*multp, new String[] { "conquer" }, 8),
    ECLIPSE(UnitTypes.eclipse, 9, 270*multp, new String[] { "sei" }, 6),
    DISRUPT(UnitTypes.disrupt, 9, 800*multp, new String[] { "nova", "flare", "dagger"}, 8),


    CONQUER(UnitTypes.conquer, 10, 450*multp, new String[] { "elude", "nova", "flare", "mono" }, 8),
    SEI(UnitTypes.sei, 10, 270*multp, new String[] { "omura" }, 8),
    
    OMURA(UnitTypes.omura, 11, 1650*multp, new String[] { "elude", "nova", "dagger", "mono" }, 9);

    public final UnitType unitType;
    public final int tier, cost;
    public final String[] evolutions;
    public final int stage;

    Evolution(mindustry.type.UnitType unitType, int tier, int cost, String[] evolutions) {
        this.unitType = unitType;
        this.tier = tier;
        this.cost = cost;
        this.evolutions = evolutions;
        this.stage = stage;
    }

}
