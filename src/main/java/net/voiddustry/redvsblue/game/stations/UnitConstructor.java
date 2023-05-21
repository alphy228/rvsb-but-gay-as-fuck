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
import static net.voiddustry.redvsblue.RedVsBluePlugin.playing;


public class UnitConstructor {

    private static final Map<String, StationData> unitConstructorsMap = new ConcurrentHashMap<>();

    public static void initTimer() {
        Timer.schedule(() -> Groups.player.each(p -> {
            if (playing) {
                players.get(p.uuid()).setCanConstruct(false);
                unitConstructorsMap.forEach((uuid, station) -> {
                    int centerX = station.tileOn().x;
                    int centerY = station.tileOn().y;


                    for (int x = -1; x <= 1; x++) {
                        for (int y = -1; y <= 1; y++) {
                            if (x == 0 && y == 0) {
                                String text = station.owner().name + "[red]'s\nUnit Constructor";
                                Call.labelReliable(text, 1, centerX, centerY);
                            } else {
                                Call.effect(Fx.bubble, centerX + (x * 3) * 8, centerY + (y * 3) * 8, 0, Color.lightGray);
                            }
                        }
                    }

                    render(station);

                    if (p.x >= (centerX-(4*8)) && p.x <= (centerX+(3*8)) && p.y >= (centerY-(3*8)) && p.y <= (centerY+(4*8))) {
                        players.get(p.uuid()).setCanConstruct(true);
                    }
                });
            }
        }), 0, 0.5F);
    }

    public static void buy(Player player) {
        if (players.get(player.uuid()).getScore() < 5) {
            player.sendMessage(Bundle.format("station.not-enough-money", Bundle.findLocale(player.locale), 5));
        } else {
            if (!unitConstructorsMap.containsKey(player.uuid())) {
                if (!player.dead()) {
                    Tile playerTileOn = player.tileOn();
                    Tile tileUnderPlayer = Vars.world.tile(playerTileOn.x, playerTileOn.y - 1);

                    if (!player.dead() && player.team() == Team.blue && tileUnderPlayer.block().isAir()) {
                        StationData unitConstructorData = new StationData(player, tileUnderPlayer);
                        unitConstructorsMap.put(player.uuid(), unitConstructorData);
                        Call.constructFinish(tileUnderPlayer, Blocks.memoryCell, null, (byte) 0, Team.blue, null);
                        Call.effect(Fx.regenParticle, tileUnderPlayer.x*8, tileUnderPlayer.y*8, 0, Color.red);
                        players.get(player.uuid()).subtractScore(5);
                    }
                }
            }
        }
    }

    public static void render(StationData stationData) {
        if (stationData.tileOn().block() != Blocks.memoryCell || stationData.owner().team() != Team.blue) {
            unitConstructorsMap.remove(stationData.owner().uuid());
            if (stationData.tileOn().block() == Blocks.memoryCell) stationData.tileOn().build.kill();
        }
    }

    public static void clearMap() {
        unitConstructorsMap.clear();
    }
}
