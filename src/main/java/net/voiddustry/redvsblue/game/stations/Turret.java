package net.voiddustry.redvsblue.game.stations;

import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import net.voiddustry.redvsblue.Bundle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.voiddustry.redvsblue.RedVsBluePlugin.players;

public class Turret {
    private static Map<String, TurretData> turretsMap = new ConcurrentHashMap<>();

    private static void renderTurrets() {
        turretsMap.forEach((owner, turret) -> {
            if (turret.getTileOn().block() == Blocks.air || turret.getOwner().team() != Team.blue) {
                turretsMap.remove(owner);
                if (turret.getTileOn().block() == Blocks.duo) {
                    turret.getTileOn().build.kill();
                }
            } else {
                Call.label(turret.getOwner().name + "[gold]'s [red]Turret\n[gray][ [gold]" + turret.getClips() + "[gray] ]", 0.5F, turret.getTileOn().x*8, turret.getTileOn().y*8);
                if (turret.getTileOn().build.items.get(Items.graphite) <= 0) {
                    if (turret.getClips() >= 1) {
                        Call.transferItemTo(null, Items.graphite, 15, 0, 0, turret.getTileOn().build);
                        turret.removetAmmoCLips(1);
                    }
                }
            }
        });
    }

    public static void initTimer() {
        Timer.schedule(Turret::renderTurrets, 0, 0.5F);
    }

    public static void buyClip(Player player) {
        if (players.get(player.uuid()).getScore() < 1) {
            player.sendMessage(Bundle.format("station.not-enough-money", Bundle.findLocale(player.locale), 1));
        } else {
            turretsMap.get(player.uuid()).addAmmoClips(1);
            players.get(player.uuid()).subtractScore(1);
        }
    }

    public static void buyTurret(Player player) {
        if (players.get(player.uuid()).getScore() < 15) {
            player.sendMessage(Bundle.format("station.not-enough-money", Bundle.findLocale(player.locale), 15));
        } else {
            if (!turretsMap.containsKey(player.uuid())) {
                Tile playerTileOn = player.tileOn();
                Tile tileUnderPlayer = Vars.world.tile(playerTileOn.x, playerTileOn.y - 1);

                Call.constructFinish(tileUnderPlayer, Blocks.duo, null, (byte) 0, Team.blue, null);

                TurretData turretData = new TurretData(player, tileUnderPlayer);
                turretsMap.put(player.uuid(), turretData);
                players.get(player.uuid()).subtractScore(15);
            }
        }
    }

    public static void clearTurrets() {
        turretsMap.clear();
    }
}
