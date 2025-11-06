package net.voiddustry.redvsblue.game.crux;

import arc.graphics.Color;
import arc.struct.Seq;
import arc.util.Timer;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import net.voiddustry.redvsblue.Bundle;
import net.voiddustry.redvsblue.RedVsBluePlugin;
import net.voiddustry.redvsblue.domain.PlayerBoss;
import net.voiddustry.redvsblue.util.Utils;

import java.util.Locale;

public class Boss {
    public static Seq<PlayerBoss> bosses = new Seq<>();

    public static void spawnBoss(Player player) {
        boolean spawnBoss = true;

        for (PlayerBoss boss : bosses) {
            if (player == boss.player) {
                spawnBoss = false;
                break;
            }
        }

        if (spawnBoss) {
            Unit unit = UnitTypes.mace.spawn(Team.crux, RedVsBluePlugin.redSpawns.random());

            if (Utils.playerCount(Team.blue) <= 2) {
                unit.health = 700;
            } else {
                unit.health = (250 * Utils.playerCount(Team.blue)) * RedVsBluePlugin.stage;
            }

            RedVsBluePlugin.players.get(player.uuid()).setKills(0);

            player.unit(unit);
            bosses.add(new PlayerBoss(player));

            Groups.player.forEach(p -> {
                Locale locale = Bundle.findLocale(p.locale());
                String bossName = Bundle.get("boss.bosses.mace", locale);
                p.sendMessage(Bundle.format("boss.spawn", locale, player.name, bossName));
            });
        }
    }

    public static void forEachBoss() {
        Timer.schedule(() -> bosses.forEach(boss -> {
            Player p = boss.player;

            if (p == null) {
                return;
            }
            if (boss.unit.dead) {
                bosses.remove(boss);
            }

            if (boss.unitType.equals(UnitTypes.mace)) {
                for (int i = 0; i < 19; i++) {
                    Call.effect(Fx.vaporSmall, (float) (p.x + Math.sin(i) * 64), (float) (p.y + Math.cos(i) * 64), 0, Color.red);
                }
                Groups.player.each(pl -> {
                    if (!(pl == null)) {
                        if (pl.team() == Team.blue && p.dst(pl) <= 64) {
                            pl.unit().apply(StatusEffects.sapped, 30);
                        }
                    }
                });
            }
        }), 0, 0.3F);

    }
}
