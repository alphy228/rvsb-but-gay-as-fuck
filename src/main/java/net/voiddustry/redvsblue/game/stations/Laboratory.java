package net.voiddustry.redvsblue.game.stations;


import arc.graphics.Color;
import arc.util.Timer;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.ui.Menus;
import mindustry.world.Tile;
import net.voiddustry.redvsblue.RedVsBluePlugin;
import net.voiddustry.redvsblue.Bundle;
import net.voiddustry.redvsblue.PlayerData;
import net.voiddustry.redvsblue.evolution.Evolution;
import net.voiddustry.redvsblue.evolution.Evolutions;
import net.voiddustry.redvsblue.game.stations.stationData.StationData;
import net.voiddustry.redvsblue.util.Utils;
import mindustry.graphics.Layer;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import static net.voiddustry.redvsblue.RedVsBluePlugin.players;

public class Laboratory {
    private static final Map<String, StationData> labsMap = new ConcurrentHashMap<>();

    public static final int evolutionMenu = Menus.registerMenu((player, option) -> {
        if (option == -1) return;

        Evolution evolution = Evolutions.evolutions.get(player.unit().type().name);
        Evolution evolutionOption = Evolutions.evolutions.get(evolution.evolutions[option]);

        PlayerData playerData = players.get(player.uuid());

        if (playerData.getScore() >= evolutionOption.cost) {
            if (player.unit() != null && (player.tileOn().block() == Blocks.air || evolutionOption.unitType.flying==true || evolutionOption.unitType.canBoost == true || evolutionOption.unitType.groundLayer==Layer.legUnit)) {
                Unit unit = evolutionOption.unitType.spawn(Team.blue, player.x(), player.y());
                unit.health = unit.type.health/2;

                if (!unit.dead()) {
                    Unit oldUnit = playerData.getUnit();
                    playerData.setUnit(unit);

                    player.unit(unit);
                    oldUnit.kill();

                    playerData.subtractScore((int)(getMultiplier(evolutionOption)*evolutionOption.cost));
                    playerData.setEvolutionStage(evolutionOption.tier);

                    Utils.sendBundled("game.evolved", player.name(), evolution.evolutions[option]);
                }
            }
        }
    });

    public static void initTimer() {

        Timer.schedule(() -> {
            renderLabs();


            Groups.player.each(p -> {
                if (players.containsKey(p.uuid())) {
                    players.get(p.uuid()).setCanEvolve(false);
                }

                labsMap.forEach((uuid, lab) -> {
                    int centerX = lab.tileOn().x * 8;
                    int centerY = lab.tileOn().y * 8;

                    if (p.team() == Team.blue && !(p.unit() == null)) {
                        if (p.dst(centerX, centerY) <= 48) {
                            if(Vars.world.tile(Math.round(p.mouseX / 8), Math.round(p.mouseY / 8)) != null && Vars.world.tile(Math.round(p.mouseX / 8), Math.round(p.mouseY / 8)).block() == Blocks.carbideWall && p.shooting) {
                                Locale locale = Bundle.findLocale(p.locale());

                                Evolution evolution = Evolutions.evolutions.get(p.unit().type().name);

                                if (!(evolution == null)) {
    
                                    String[][] buttons = new String[evolution.evolutions.length][1];
    
                                    for (int i = 0; i < evolution.evolutions.length; i++) {
                                        int multiplier = getMultiplier(evolution.evolutions[i]);
                                        int cost = (Evolutions.evolutions.get(evolution.evolutions[i]).cost*multiplier);

                                        String textColor = "";

                                        if (cost>Evolutions.evolutions.get(evolution.evolutions[i]).cost) {
                                            textColor = "[red]";
                                        } else if (cost<Evolutions.evolutions.get(evolution.evolutions[i]).cost) {
                                            textColor = "[green]";
                                        } else {
                                            textColor = "[yellow]";
                                        }
                                        buttons[i][0] = Bundle.format("menu.evolution.evolve", locale, evolution.evolutions[i],(textColor+cost+" - "+(multiplier*100)+"%"));
                                    }
    
                                    Call.menu(p.con, evolutionMenu, Bundle.get("menu.evolution.title", locale), Bundle.format("menu.evolution.message", locale, players.get(p.uuid()).getEvolutionStage(), Bundle.get("evolution.branch.initial", locale)), buttons);
                                }
                            }
                            if (!(players.get(p.uuid()) == null)) {
                            players.get(p.uuid()).setCanEvolve(true);
                            }
                            Call.infoPopup(p.con, Bundle.get("evolution.evolution-available", p.locale), 0.5F, 0, 0, 0, -200, 0);
                        }
                    }
                });
            });

            labsMap.forEach((uuid, lab) -> {

                int centerX = lab.tileOn().x * 8;
                int centerY = lab.tileOn().y * 8;

                for (int i = 0; i < 19; i++) {
                    Call.effect(Fx.vaporSmall, (float) (centerX + Math.sin(i) * 48), (float) (centerY + Math.cos(i) * 48), 1, Color.purple);
                }
                StationUtils.drawStationName(lab.tileOn(), lab.owner().name + "[gold]'s\n[purple]Lab", 0.6F);
            });
        }, 0, 0.5F);
    }

    private static float getMultiplier(Evolution evo) {
        int stage = evo.stage;
        float multiplier;
        if (RedVsBluePlugin.stage == stage) {
            multiplier = 1f;
        } else if (RedVsBluePlugin.stage > stage) {
            multiplier = 0.75f;
        } else {
            multiplier = (2^(stage-RedVsBluePlugin.stage));
        }
        Log.info("stage for evolution "+evo+" - "+stage+" multiplier:"+multiplier);
        return multiplier;
    }

    private static float getMultiplier(String evolution) {
        return getMultiplier(Evolutions.evolutions.get(evolution));
    }

    public static void buyLab(Player player) {
        if (players.get(player.uuid()).getScore() < 7) {
            player.sendMessage(Bundle.format("station.not-enough-money", Bundle.findLocale(player.locale), 7));
        } else {
            if (!labsMap.containsKey(player.uuid())) {
                if (!player.dead()) {
                    Tile playerTileOn = player.tileOn();
                    Tile tileUnderPlayer = Vars.world.tile(playerTileOn.x, playerTileOn.y - 1);

                    if (!player.dead() && player.team() == Team.blue && tileUnderPlayer.block().isAir()) {
                        StationData laboratoryData = new StationData(player, tileUnderPlayer);
                        labsMap.put(player.uuid(), laboratoryData);
                        Call.constructFinish(tileUnderPlayer, Blocks.carbideWall, null, (byte) 0, Team.blue, null);
                        Call.effect(Fx.regenParticle, tileUnderPlayer.x*8, tileUnderPlayer.y*8, 0, Color.red);
                        players.get(player.uuid()).subtractScore(7);
                    }
                }
            }
        }
    }

    public static void renderLabs() {
        labsMap.forEach((owner, lab) -> {
            if (lab != null) {
                if (lab.tileOn().block() == Blocks.air || lab.owner().team() != Team.blue) {
                    labsMap.remove(owner);
                    if (lab.tileOn().block() == Blocks.carbideWall) {
                        lab.tileOn().build.kill();
                    }
                }
            }
        });
    }

    public static void clearLabs() {
        labsMap.clear();
    }
}
