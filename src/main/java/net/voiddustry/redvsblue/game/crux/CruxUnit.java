package net.voiddustry.redvsblue.game.crux;

import arc.util.Timer;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import net.voiddustry.redvsblue.RedVsBluePlugin;
import net.voiddustry.redvsblue.util.Utils;

import java.util.Random;

public class CruxUnit {
    public static void callSpawn(Player player) {
        
        UnitType type = ClassChooseMenu.selectedUnit.get(player.uuid());

        if (type != null) {
            Tile cruxSpawn = RedVsBluePlugin.redSpawns.random();

            if (cruxSpawn != null && cruxSpawn.block() != null) {
                Call.logicExplosion(Team.crux, cruxSpawn.x*8, cruxSpawn.y*8, 80, 999999, true, true, true, true);
            }
            Unit unit = type.spawn(Team.crux, cruxSpawn);

            if (unit != null && !unit.dead) {
                unit.health = Integer.MAX_VALUE;
                unit.apply(StatusEffects.overclock, 180);;

                if (unit.type == UnitTypes.obviate) {
                    if (RedVsBluePlugin.stage == 11) {
                        unit.apply(StatusEffects.shielded, 600f);
                        unit.apply(StatusEffects.overdrive, 99999f);
                    }
                }

                Timer.schedule(() -> {
                    if (unit.type == UnitTypes.crawler) {
                        unit.health = 20;
                        unit.addItem(Items.pyratite, 10);
                    } else if (unit.type == UnitTypes.merui) {
                        unit.health = 100;
                        unit.addItem(Items.pyratite, 99);
                    } else if (unit.type == UnitTypes.mace) {
                        unit.health = 100;
                    } else if (unit.type == UnitTypes.dagger) {
                        unit.health = 100;
                    } else {
                        unit.health = unit.type.health;
                    }

                }, 3);

                player.unit(unit);
                unit.spawnedByCore = true;
            }
        }
    }

    private static void spawnCrux() {
        UnitType unitType = UnitTypes.alpha;
        if (!ClassChooseMenu.units.isEmpty()) {
            unitType = ClassChooseMenu.units.keys().toSeq().get(Utils.getRandomInt(0, ClassChooseMenu.units.size)); //Utils.getRandomInt(0, ClassChooseMenu.units.size)
        }
        if (Utils.gameRun) {
            Tile cruxSpawn = RedVsBluePlugin.redSpawns.random();

            if (cruxSpawn != null && cruxSpawn.block() != null) {
                Call.logicExplosion(Team.crux, cruxSpawn.x*8, cruxSpawn.y*8, 80, 999999, true, true, true, true);
            }
            
            if (RedVsBluePlugin.stage >= 11) {
                Random rand = new Random();
                if (rand.nextInt(40) == 1) {
                    UnitTypes.latum.spawn(Team.crux, cruxSpawn);
                }
                if (rand.nextInt(50) == 1) {
                    UnitTypes.conquer.spawn(Team.crux, cruxSpawn);
                }
                if (rand.nextInt(25) == 1) {
                    UnitTypes.tecta.spawn(Team.crux, cruxSpawn);
                }
            }
            
            Unit unit = unitType.spawn(Team.crux, cruxSpawn);

            unit.apply(Vars.content.statusEffect("superShielded"), 60f);
            unit.apply(Vars.content.statusEffect("shielded"), 120f);

            if (unit.type == UnitTypes.obviate) {
                if (RedVsBluePlugin.stage == 11) {
                    unit.apply(StatusEffects.shielded, 600f);
                    unit.apply(StatusEffects.overdrive, 99999f);
                }
            }

            Timer.schedule(() -> {
                if (unit.type == UnitTypes.crawler) {
                    unit.health = 20;
                    unit.addItem(Items.pyratite, 10);
                } else if (unit.type == UnitTypes.merui) {
                    unit.health = 100;
                    unit.addItem(Items.pyratite, 99);
                } else if (unit.type == UnitTypes.mace) {
                    unit.health = 100;
                } else if (unit.type == UnitTypes.dagger) {
                    unit.health = 100;
                } else {
                    unit.health = unit.type.health;
                }
            }, 2);
        }
    };

    public static void checkUnitCount() {
        if (Utils.gameRun) {
            int players = Groups.player.size();
            int stage = RedVsBluePlugin.stage;

            int cruxUnitsCount = Math.round((players + stage) / (float) 3);

            final int[] cruxUnits = {0};
            final int[] cruxPlayersWithUnits = {0};

            Groups.player.each(p -> {
                if (!(p.unit() == null)) {
                    if (p.unit().team == Team.crux) {
                        cruxPlayersWithUnits[0]++;
                    }
                }
            });

            Groups.unit.each(u -> {
                if (u.team == Team.crux) {
                    cruxUnits[0]++;
                }
            });

            if (cruxUnitsCount > (cruxUnits[0]-cruxPlayersWithUnits[0])) {
                spawnCrux();
            }
        }
    }
}
