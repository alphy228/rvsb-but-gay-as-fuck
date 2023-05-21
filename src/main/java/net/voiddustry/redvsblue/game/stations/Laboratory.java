package net.voiddustry.redvsblue.game.stations;


import arc.graphics.Color;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.ui.Menus;
import mindustry.world.Tile;
import net.voiddustry.redvsblue.Bundle;
import net.voiddustry.redvsblue.PlayerData;
import net.voiddustry.redvsblue.evolution.Evolution;
import net.voiddustry.redvsblue.evolution.Evolutions;
import net.voiddustry.redvsblue.game.stations.stationData.StationData;
import net.voiddustry.redvsblue.util.Utils;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.voiddustry.redvsblue.RedVsBluePlugin.players;

public class Laboratory {
    private static final Map<String, StationData> labsMap = new ConcurrentHashMap<>();

    private static final int evolutionMenu = Menus.registerMenu((player, option) -> {
        if (option == -1) return;

        Evolution evolution = Evolutions.evolutions.get(player.unit().type().name);
        Evolution evolutionOption = Evolutions.evolutions.get(evolution.evolutions[option]);

        PlayerData playerData = players.get(player.uuid());

        if (playerData.getScore() >= evolutionOption.cost) {
            if (player.tileOn().block() == Blocks.air) {
                Unit unit = evolutionOption.unitType.spawn(Team.blue, player.x(), player.y());
                unit.health = unit.type.health/2;

                if (!unit.dead()) {
                    Unit oldUnit = playerData.getUnit();
                    playerData.setUnit(unit);

                    player.unit(unit);
                    oldUnit.kill();

                    playerData.subtractScore(evolutionOption.cost);
                    playerData.setEvolutionStage(evolutionOption.tier);

                    Utils.sendBundled("game.evolved", player.name(), evolution.evolutions[option]);
                }
            }
        }
    });

    public static void initTimer() {

        Timer.schedule(() -> {
            renderLabs();

            Groups.player.each(p -> labsMap.forEach((uuid, lab) -> {
                int centerX = lab.tileOn().x * 8;
                int centerY = lab.tileOn().y * 8;

                if (p.team() == Team.blue) {
                    if (p.x >= (centerX-(6*8)) && p.x <= (centerX+(5*8)) && p.y >= (centerY-(5*8)) && p.y <= (centerY+(6*8))) {
                        if(Vars.world.tile(Math.round(p.mouseX / 8), Math.round(p.mouseY / 8)) != null) {
                            if (Vars.world.tile(Math.round(p.mouseX / 8), Math.round(p.mouseY / 8)).block() == Blocks.carbideWall) {
                                if (p.shooting) {
                                    Locale locale = Bundle.findLocale(p.locale());

                                    Evolution evolution = Evolutions.evolutions.get(p.unit().type().name);

                                    String[][] buttons = new String[evolution.evolutions.length][1];

                                    for (int i = 0; i < evolution.evolutions.length; i++) {
                                        buttons[i][0] = Bundle.format("menu.evolution.evolve", locale, evolution.evolutions[i], Evolutions.evolutions.get(evolution.evolutions[i]).cost);
                                    }

                                    Call.menu(p.con, evolutionMenu, Bundle.get("menu.evolution.title", locale), Bundle.format("menu.evolution.message", locale, players.get(p.uuid()).getEvolutionStage(), Bundle.get("evolution.branch.initial", locale)), buttons);
                                }
                                StationUtils.drawStationName(p.con, lab.tileOn(), lab.owner().name + "[gold]'s\n[purple]Lab", 0.5F);
                            }
                        }
                        Call.infoPopup(p.con, Bundle.get("evolution.evolution-available", p.locale), 0.5F, 0, 0, 0, -200, 0);
                    }
                }
            }));

            labsMap.forEach((uuid, lab) -> {

                int centerX = lab.tileOn().x * 8;
                int centerY = lab.tileOn().y * 8;

                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        if (x != 0 && y != 0) {
                            Call.effect(Fx.vaporSmall, centerX + (x * 5) * 8, centerY + (y * 5) * 8, 1, Color.purple);
                        }
                    }
                }
            });
        }, 0, 0.5F);
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
