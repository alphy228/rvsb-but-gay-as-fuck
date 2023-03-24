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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.voiddustry.redvsblue.RedVsBluePlugin.players;

public class AmmoBox {

    private static final Map<String, AmmoBoxData> ammoBoxesMap = new ConcurrentHashMap<>();

    public static void initTimer() {
        Timer.schedule(() -> ammoBoxesMap.forEach((owner, ammoBoxData) -> {
            int centerX = ammoBoxData.getTileOn().x * 8;
            int centerY = ammoBoxData.getTileOn().y * 8;

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (x == 0 && y == 0) {
                        String text = ammoBoxData.getOwner().name + "[gold]'s\n[accent]Ammo Box";
                        Call.labelReliable(text, 1, centerX, centerY);
                    } else {
                        Call.effect(Fx.absorb, centerX + (x * 3) * 8, centerY + (y * 3) * 8, 1, Color.red);
                    }
                }
            }
        }), 0, .5F);
        Timer.schedule(() -> ammoBoxesMap.forEach((owner, ammoBoxData) -> {
            PlayerData data = players.get(ammoBoxData.getOwner().uuid());
            int centerX = ammoBoxData.getTileOn().x * 8;
            int centerY = ammoBoxData.getTileOn().y * 8;

            Groups.player.each(p -> {
                if (p.team() == Team.blue && !p.dead()) {
                    if(p.unit().ammo <= (float) p.unit().type.ammoCapacity / 4) {
                        if (data.getScore() >= 1) {
                            if (p.x >= (centerX-(3*8)) && p.x <= (centerX+(3*8)) && p.y >= (centerY-(3*8)) && p.y <= (centerY+(3*8))) {
                                p.unit().ammo = p.unit().type.ammoCapacity;
                                Call.label("[accent][\uE86A]\n[scarlet][-1]", 5, p.x, p.y);
                                data.subtractScore(1);
                            }
                        }
                    } else {
                        Call.label(p.con, "", 1, p.x, p.y);
                    }
                }
            });

        }), 0, 1);
        Timer.schedule(AmmoBox::renderAmmoBoxes, 0, 1);
    }

    public static void buyAmmoBox(Player player) {
        if (players.get(player.uuid()).getScore() < 5) {
            player.sendMessage(Bundle.format("station.not-enough-money", Bundle.findLocale(player.locale), 15));
        } else {
            if (!ammoBoxesMap.containsKey(player.uuid())) {
                if (!player.dead()) {
                    Tile playerTileOn = player.tileOn();
                    Tile tileUnderPlayer = Vars.world.tile(playerTileOn.x, playerTileOn.y - 1);

                    if (!player.dead() && player.team() == Team.blue && tileUnderPlayer.block().isAir()) {
                        AmmoBoxData ammoBoxData = new AmmoBoxData(player, tileUnderPlayer);
                        ammoBoxesMap.put(player.uuid(), ammoBoxData);
                        Call.constructFinish(tileUnderPlayer, Blocks.berylliumWall, null, (byte) 0, Team.blue, null);
                        Call.effect(Fx.regenParticle, tileUnderPlayer.x*8, tileUnderPlayer.y*8, 0, Color.red);
                    }
                    players.get(player.uuid()).subtractScore(5);
                }
            }
        }
    }

    public static void renderAmmoBoxes() {
        ammoBoxesMap.forEach((owner, ammoBox) -> {
            if (ammoBox != null) {
                if (ammoBox.getTileOn().block() == Blocks.air || ammoBox.getOwner().team() != Team.blue) {
                    ammoBoxesMap.remove(owner);
                    if (ammoBox.getTileOn().block() == Blocks.berylliumWall) {
                        ammoBox.getTileOn().build.kill();
                    }
                }
            }
        });
    }

    public static void clearPoints() {
        ammoBoxesMap.clear();
    }
}
