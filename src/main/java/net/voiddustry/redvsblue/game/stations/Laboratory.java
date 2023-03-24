package net.voiddustry.redvsblue.game.stations;


import arc.graphics.Color;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.world.Tile;
import net.voiddustry.redvsblue.Bundle;
import net.voiddustry.redvsblue.PlayerData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.voiddustry.redvsblue.RedVsBluePlugin.players;

public class Laboratory {
    private static Map<String, LaboratoryData> labsMap = new ConcurrentHashMap<>();

    public static void initTimer() {

        Timer.schedule(() -> {
            renderLabs();
            labsMap.forEach((uuid, lab) -> {

                lab.getTileOn().build.liquids.add(Liquids.slag, 400);
                lab.getTileOn().build.liquids.add(Liquids.arkycite, 800);


                int centerX = lab.getTileOn().x * 8;
                int centerY = lab.getTileOn().y * 8;

                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        if (x == 0 && y == 0) {
                            String text = lab.getOwner().name + "[gold]'s\n[purple]Lab";
                            Call.label(text, 1, centerX, centerY);
                        } else {
                            Call.effect(Fx.vaporSmall, centerX + (x * 4) * 8, centerY + (y * 4) * 8, 1, Color.purple);
                        }
                    }
                }

                Groups.player.each(p -> {
                    PlayerData data = players.get(p.uuid());
                    if (p.team() == Team.blue) {
                        if (p.x >= (centerX-(4*8)) && p.x <= (centerX+(4*8)) && p.y >= (centerY-(4*8)) && p.y <= (centerY+(4*8))) {
                            data.setCanEvolve(true);
                            Call.infoPopup(p.con, Bundle.get("evolution.evolution-available", p.locale), 0.5F, 0, 0, 0, -200, 0);
                        } else {
                            data.setCanEvolve(false);
                        }
                    }
                });

            });
        }, 0, 0.5F);
    }

    public static void buyLab(Player player) {
        if (players.get(player.uuid()).getScore() < 4) {
            player.sendMessage(Bundle.format("station.not-enough-money", Bundle.findLocale(player.locale), 4));
        } else {
            if (!labsMap.containsKey(player.uuid())) {
                if (!player.dead()) {
                    Tile playerTileOn = player.tileOn();
                    Tile tileUnderPlayer = Vars.world.tile(playerTileOn.x, playerTileOn.y - 2);

                    if (!player.dead() && player.team() == Team.blue && tileUnderPlayer.block().isAir()) {
                        LaboratoryData laboratoryData = new LaboratoryData(player, tileUnderPlayer);
                        labsMap.put(player.uuid(), laboratoryData);
                        Call.constructFinish(tileUnderPlayer, Blocks.pyrolysisGenerator, null, (byte) 0, Team.blue, null);
                        Call.effect(Fx.regenParticle, tileUnderPlayer.x*8, tileUnderPlayer.y*8, 0, Color.red);
                    }
                    players.get(player.uuid()).subtractScore(4);
                }
            }
        }
    }

    public static void renderLabs() {
        labsMap.forEach((owner, lab) -> {
            if (lab != null) {
                if (lab.getTileOn().block() == Blocks.air || lab.getOwner().team() != Team.blue) {
                    labsMap.remove(owner);
                    if (lab.getTileOn().block() == Blocks.pyrolysisGenerator) {
                        lab.getTileOn().build.kill();
                    }
                }
            }
        });
    }

    public static void clearLabs() {
        labsMap.clear();
    }
}
