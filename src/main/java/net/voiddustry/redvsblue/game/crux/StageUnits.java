package net.voiddustry.redvsblue.game.crux;

import arc.struct.ObjectMap;
import mindustry.content.UnitTypes;
import mindustry.type.UnitType;

public class StageUnits {
    //String - Key
    public final static ObjectMap<UnitType, String> firstStage = ObjectMap.of(
            UnitTypes.crawler, "units.crux.menu.crawler",
            UnitTypes.dagger, "units.crux.menu.dagger"
    );

    public final static ObjectMap<UnitType, String> secondStage = ObjectMap.of(
            UnitTypes.crawler, "units.crux.menu.crawler",
            UnitTypes.dagger, "units.crux.menu.dagger",
            UnitTypes.merui, "units.crux.menu.merui"
    );

    public final static ObjectMap<UnitType, String> thirdStage = ObjectMap.of(
            UnitTypes.crawler, "units.crux.menu.crawler",
            UnitTypes.merui, "units.crux.menu.merui",
            UnitTypes.dagger, "units.crux.menu.dagger",
            UnitTypes.mace, "units.crux.menu.mace"
    );

    public final static ObjectMap<UnitType, String> fourthAndFifrhStage = ObjectMap.of(
            UnitTypes.crawler, "units.crux.menu.crawler",
            UnitTypes.merui, "units.crux.menu.merui",
            UnitTypes.dagger, "units.crux.menu.dagger",
            UnitTypes.mace, "units.crux.menu.mace",
            UnitTypes.stell, "units.crux.menu.stell"
    );

    public final static ObjectMap<UnitType, String> sixthStage = ObjectMap.of(
            UnitTypes.elude, "units.crux.menu.elude",
            UnitTypes.merui, "units.crux.menu.merui",
            UnitTypes.dagger, "units.crux.menu.dagger",
            UnitTypes.nova, "units.crux.menu.nova",
            UnitTypes.cleroi, "units.crux.menu.cleroi"
    );
}
