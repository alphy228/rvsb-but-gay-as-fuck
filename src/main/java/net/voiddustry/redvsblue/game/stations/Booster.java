package net.voiddustry.redvsblue.game.stations;

import arc.graphics.Color;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
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

public class Booster {
    private static final Map<String, StationData> boostersMap = new ConcurrentHashMap<>();

    public static void initTimer() {
        Timer.schedule(() -> boostersMap.forEach((owner, pointData) -> {
            int centerX = pointData.tileOn().x * 8;
            int centerY = pointData.tileOn().y * 8;

            for (int i = 0; i < 19; i++) {
                Call.effect(Fx.vaporSmall, (float) (centerX + Math.sin(i) * 64), (float) (centerY + Math.cos(i) * 64), 1, Color.orange);
            }

            String text = pointData.owner().name + "[gold]'s\n[orange]Booster";
            StationUtils.drawStationName(pointData.tileOn(), text, 1.1F);

            Groups.unit.each(u -> {
                if (u.team == Team.blue) {
                    if (u.dst(centerX, centerY) <= 64) {
                        u.apply(StatusEffects.overclock, 70);
                    }
                }
            });

        }), 0, 1);
        Timer.schedule(Booster::renderBoosters, 0, 1);
    }

    public static void buyBooster(Player player) {
        if (players.get(player.uuid()).getScore() < 18) {
            player.sendMessage(Bundle.format("station.not-enough-money", Bundle.findLocale(player.locale), 18));
        } else {
            if (!boostersMap.containsKey(player.uuid())) {
                if (!player.dead()) {
                    Tile playerTileOn = player.tileOn();
                    Tile tileUnderPlayer = Vars.world.tile(playerTileOn.x, playerTileOn.y - 1);

                    if (!player.dead() && player.team() == Team.blue && tileUnderPlayer.block().isAir()) {
                        StationData boosterData = new StationData(player, tileUnderPlayer);
                        boostersMap.put(player.uuid(), boosterData);
                        Call.constructFinish(tileUnderPlayer, Blocks.beamNode, null, (byte) 0, Team.blue, null);
                        Call.effect(Fx.regenParticle, tileUnderPlayer.x*8, tileUnderPlayer.y*8, 0, Color.red);
                        players.get(player.uuid()).subtractScore(18);
                    }
                }
            }
        }
    }

    public static void renderBoosters() {
        boostersMap.forEach((owner, booster) -> {
            if (booster != null) {
                if (booster.tileOn().block() == Blocks.air || booster.owner().team() != Team.blue) {
                    boostersMap.remove(owner);
                    if (booster.tileOn().block() == Blocks.beamNode) {
                        booster.tileOn().build.kill();
                    }
                }
            }
        });
    }

    public static void clearBoosters() {
        boostersMap.clear();
    }
}
