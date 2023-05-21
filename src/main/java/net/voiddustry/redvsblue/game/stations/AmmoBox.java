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
import net.voiddustry.redvsblue.PlayerData;
import net.voiddustry.redvsblue.game.stations.stationData.StationData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.voiddustry.redvsblue.RedVsBluePlugin.players;

public class AmmoBox {

    private static final Map<String, StationData> ammoBoxesMap = new ConcurrentHashMap<>();

    public static void initTimer() {
        Timer.schedule(() -> ammoBoxesMap.forEach((owner, ammoBoxData) -> {
            int centerX = ammoBoxData.tileOn().x * 8;
            int centerY = ammoBoxData.tileOn().y * 8;

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (x != 0 && y != 0) {
                        Call.effect(Fx.absorb, centerX + (x * 3) * 8, centerY + (y * 3) * 8, 1, Color.red);
                    }
                }
            }
        }), 0, .5F);
        Timer.schedule(() -> ammoBoxesMap.forEach((owner, ammoBoxData) -> {
            PlayerData data = players.get(ammoBoxData.owner().uuid());
            int centerX = ammoBoxData.tileOn().x * 8;
            int centerY = ammoBoxData.tileOn().y * 8;

            Groups.player.each(p -> {
                if (p.team() == Team.blue && !p.dead()) {
                        if (data.getScore() >= 1) {
                            if (p.x >= (centerX-(3*8)) && p.x <= (centerX+(3*8)) && p.y >= (centerY-(3*8)) && p.y <= (centerY+(3*8))) {
                                if (Vars.world.tile(Math.round(p.mouseX / 8), Math.round(p.mouseY / 8)) == ammoBoxData.tileOn()) {
                                    if (p.shooting) {
                                        p.unit().ammo = p.unit().type.ammoCapacity;
                                        Call.label("[accent][\uE86A]\n[scarlet][-1]", 5, p.x, p.y);
                                        data.subtractScore(1);
                                    }
                                    StationUtils.drawStationName(p.con, ammoBoxData.tileOn(), ammoBoxData.owner().name + "[gold]'s\n[accent]Ammo Box\n[gray][ [accent]Shoot to get ammo[gray] ]", 0.5F);
                                }
                        }
                    }
                }
            });

        }), 0, 0.5F);
        Timer.schedule(AmmoBox::renderAmmoBoxes, 0, 1);
    }

    public static void buyAmmoBox(Player player) {
        if (players.get(player.uuid()).getScore() < 5) {
            player.sendMessage(Bundle.format("station.not-enough-money", Bundle.findLocale(player.locale), 5));
        } else {
            if (!ammoBoxesMap.containsKey(player.uuid())) {
                if (!player.dead()) {
                    Tile playerTileOn = player.tileOn();
                    Tile tileUnderPlayer = Vars.world.tile(playerTileOn.x, playerTileOn.y - 1);

                    if (!player.dead() && player.team() == Team.blue && tileUnderPlayer.block().isAir()) {
                        StationData ammoBoxData = new StationData(player, tileUnderPlayer);
                        ammoBoxesMap.put(player.uuid(), ammoBoxData);
                        Call.constructFinish(tileUnderPlayer, Blocks.berylliumWall, null, (byte) 0, Team.blue, null);
                        Call.effect(Fx.regenParticle, tileUnderPlayer.x*8, tileUnderPlayer.y*8, 0, Color.red);
                        players.get(player.uuid()).subtractScore(5);
                    }
                }
            }
        }
    }

    public static void renderAmmoBoxes() {
        ammoBoxesMap.forEach((owner, ammoBox) -> {
            if (ammoBox != null) {
                if (ammoBox.tileOn().block() == Blocks.air || ammoBox.owner().team() != Team.blue) {
                    ammoBoxesMap.remove(owner);
                    if (ammoBox.tileOn().block() == Blocks.berylliumWall) {
                        ammoBox.tileOn().build.kill();
                    }
                }
            }
        });
    }

    public static void clearPoints() {
        ammoBoxesMap.clear();
    }
}
