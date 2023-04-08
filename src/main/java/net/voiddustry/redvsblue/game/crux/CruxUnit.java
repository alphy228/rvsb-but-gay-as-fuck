package net.voiddustry.redvsblue.game.crux;

import arc.util.Timer;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import net.voiddustry.redvsblue.RedVsBluePlugin;
import net.voiddustry.redvsblue.util.Utils;

public class CruxUnit {
    public static void callSpawn(Player player) {
        
        UnitType type = ClassChooseMenu.selectedUnit.get(player.uuid());
        if (type != null) {
           
            Unit unit = type.spawn(Team.crux, RedVsBluePlugin.redSpawnX, RedVsBluePlugin.redSpawnY);
        }

        if (unit != null && !unit.dead) {
            unit.health = Integer.MAX_VALUE;

            Timer.schedule(() -> {
                if (unit.type == UnitTypes.crawler) {
                    unit.health = 10;
                    unit.addItem(Items.pyratite, 10);
                } else if (unit.type == UnitTypes.merui) {
                    unit.health = 100;
                    unit.addItem(Items.pyratite, 99);
                } else if (unit.type == UnitTypes.mace) {
                    unit.health = 130;
                } else if (unit.type == UnitTypes.dagger) {
                    unit.health = 90;
                } else {
                    unit.health = unit.type.health;
                }

            }, 2);

            player.unit(unit);
            unit.spawnedByCore = true;
        }
    }

    private static void spawnCrux() {

        UnitType unitType = ClassChooseMenu.units.keys().toSeq().get(Utils.getRandomInt(0, ClassChooseMenu.units.size)); //Utils.getRandomInt(0, ClassChooseMenu.units.size)

        Unit unit = unitType.spawn(Team.crux, RedVsBluePlugin.redSpawnX, RedVsBluePlugin.redSpawnY);

        Timer.schedule(() -> {
            if (unit.type == UnitTypes.crawler) {
                unit.health = 10;
                unit.addItem(Items.pyratite, 10);
            } else if (unit.type == UnitTypes.merui) {
                unit.health = 100;
                unit.addItem(Items.pyratite, 10);
            }
        }, 2);
    };

    public static void checkUnitCount() {
        if (Utils.gameRun) {
            int players = Groups.player.size();
            int stage = RedVsBluePlugin.stage;

            int cruxUnitsCount = Math.round((players + stage) / (float) 3);

            final int[] cruxUnits = {0};

            Groups.unit.each(u -> {
                if (u.team == Team.crux) {
                    cruxUnits[0]++;
                }
            });

            if (cruxUnitsCount > cruxUnits[0]) {
                spawnCrux();
            }
        }
    }
}
