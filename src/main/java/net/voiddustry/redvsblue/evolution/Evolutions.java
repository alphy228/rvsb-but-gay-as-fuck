package net.voiddustry.redvsblue.evolution;


import arc.struct.ObjectMap;

@SuppressWarnings("unused")
public class Evolutions {
    public final static ObjectMap<String, Evolution> evolutions = ObjectMap.of(
        "nova", Evolution.NOVA,

        "mace", Evolution.MACE,
        "pulsar", Evolution.PULSAR,
        "risso", Evolution.RISSO,
        "poly", Evolution.POLY,

        "stell", Evolution.STELL,
        "quasar", Evolution.QUASAR,
        "atrax", Evolution.ATRAX,
        "zenith", Evolution.ZENITH,
        "retusa", Evolution.RETUSA
    );
}
