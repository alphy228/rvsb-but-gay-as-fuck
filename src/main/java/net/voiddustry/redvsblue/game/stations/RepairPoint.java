package net.voiddustry.redvsblue.game.stations;

import arc.graphics.Color;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;

import mindustry.world.Tile;
import net.voiddustry.redvsblue.Bundle;
import net.voiddustry.redvsblue.game.stations.stationData.StationData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.voiddustry.redvsblue.RedVsBluePlugin.players;

public class RepairPoint {

    private static final Map<String, StationData> repairPointsMap = new ConcurrentHashMap<>();

    public static void initTimer() {
        Timer.schedule(() -> repairPointsMap.forEach((owner, pointData) -> {
            int centerX = pointData.tileOn().x * 8;
            int centerY = pointData.tileOn().y * 8;

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (x != 0 && y != 0) {

                        Call.effect(Fx.healWaveDynamic, centerX + (x * 3) * 8, centerY + (y * 3) * 8, 1, Color.red);
                    }
                }
            }

            Groups.player.each(p -> {
                if (p.team() == Team.blue) {
                    if (p.x >= (centerX-(3*8)) && p.x <= (centerX+(3*8)) && p.y >= (centerY-(3*8)) && p.y <= (centerY+(3*8))) {
                        if (p.unit().health <= p.unit().type.health) {
                            p.unit().health += 3;
                            Call.label("[lime]+3", 1, p.x, p.y);
                        }
                        if (Vars.world.tile(Math.round(p.mouseX / 8), Math.round(p.mouseY / 8)) == pointData.tileOn()) {
                            String text = pointData.owner().name + "[gold]'s\n[cyan]Repair Point";
                            StationUtils.drawStationName(pointData.owner().con, pointData.tileOn(), text, 1F);
                        }
                    }
                }
            });

        }), 0, 1);
        Timer.schedule(RepairPoint::renderRepairPoints, 0, 1);
    }

    public static void buyRepairPoint(Player player) {
        if (players.get(player.uuid()).getScore() < 10) {
            player.sendMessage(Bundle.format("station.not-enough-money", Bundle.findLocale(player.locale), 10));
        } else {
            if (!repairPointsMap.containsKey(player.uuid())) {
                if (!player.dead()) {
                    Tile playerTileOn = player.tileOn();
                    Tile tileUnderPlayer = Vars.world.tile(playerTileOn.x, playerTileOn.y - 1);

                    if (!player.dead() && player.team() == Team.blue && tileUnderPlayer.block().isAir()) {
                        StationData repairPointsData = new StationData(player, tileUnderPlayer);
                        repairPointsMap.put(player.uuid(), repairPointsData);
                        Call.constructFinish(tileUnderPlayer, Blocks.mender, null, (byte) 0, Team.blue, null);
                        Call.effect(Fx.regenParticle, tileUnderPlayer.x*8, tileUnderPlayer.y*8, 0, Color.red);
                        players.get(player.uuid()).subtractScore(10);
                    }
                }
            }
        }
    }

    public static void renderRepairPoints() {
        repairPointsMap.forEach((owner, repairPoint) -> {
            if (repairPoint != null) {
                if (repairPoint.tileOn().block() == Blocks.air || repairPoint.owner().team() != Team.blue) {
                    repairPointsMap.remove(owner);
                    if (repairPoint.tileOn().block() == Blocks.mender) {
                        repairPoint.tileOn().build.kill();
                    }
                }
            }
        });
    }

    public static void clearPoints() {
        repairPointsMap.clear();
    }
}
