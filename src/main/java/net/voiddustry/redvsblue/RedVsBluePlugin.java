package net.voiddustry.redvsblue;

import arc.Events;
import arc.util.CommandHandler;

import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.gen.Groups;
import mindustry.mod.Plugin;
import net.voiddustry.redvsblue.Admin.LogEntry;
import net.voiddustry.redvsblue.Admin.LogTypes.UnitKillEntry;
import net.voiddustry.redvsblue.Admin.Logs;

import java.util.HashMap;
import java.util.Locale;

import static net.voiddustry.redvsblue.Admin.Logs.*;

public class RedVsBluePlugin extends Plugin {
    private final HashMap<String, PlayerData> players = new HashMap<>();

    private int stage = 0;

    public static int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public void init() {
        Events.on(EventType.PlayerConnect.class, event -> {
            if (!players.containsKey(event.player.uuid())) {
                players.put(event.player.uuid(), new PlayerData(event.player));
            }
        });

        Events.on(EventType.UnitBulletDestroyEvent.class, event -> {
            if (event.unit != null && event.bullet.owner() instanceof Unit killer) {
                LogEntry entry = new UnitKillEntry(event.unit, killer);
                Logs.addLogEntry(entry);

                if (killer.isPlayer()) {
                    players.get(killer.getPlayer().uuid()).addScore(2);
                }
                if (event.unit.team() == Team.blue) {
                    event.unit.team(Team.crux);
                }
            }
        });

        Events.on(EventType.WaveEvent.class, event -> {
            stage = (int) Math.floor(Vars.state.wave / 6f) + 1;

            if (Vars.state.wave % 6 == 0 && stage > 1) {
                if (playerCount(Team.crux) >= 5) {
                    Unit boss = UnitTypes.antumbra.spawn(Team.crux, 8 * 8, 8 * 8);
                    boss.health(14000);
                    Player player = getRandomPlayer(Team.crux);
                    Call.unitControl(player, boss);
                    sendBundled("game.boss.spawn", player.name());
                }
            }
        });

        Events.run(EventType.Trigger.update, () -> Groups.player.each(player -> {
            Unit unit = player.unit();
            PlayerData data = players.get(player.uuid());

            Call.setHudText(player.con(), Bundle.format("game.hud", Bundle.findLocale(player.locale()), Math.floor(unit.health()), Math.floor(unit.shield()), data.getScore(), stage));
        }));
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("logs", "Open Logs, only for admins", ((args, player) -> {
            if (!player.admin) {
                player.sendMessage(Bundle.get("commands.no-admin", player.locale));
            } else openLogs(player);
        }));
    }

    private void sendBundled(String key, Object... format) {
        Groups.player.forEach(p -> {
            Locale locale = Bundle.findLocale(p.locale());
            p.sendMessage(Bundle.format(key, locale, format));
        });
    }

    private void sendBundled(String key) {
        Groups.player.forEach(p -> {
            Locale locale = Bundle.findLocale(p.locale());
            p.sendMessage(Bundle.get(key, locale));
        });
    }

    public int playerCount(Team team) {
        final int[] i = {0};
        Groups.player.each(p -> {
            if (p.team() == team) i[0]++;
        });
        return i[0];
    }

    public static int playerCount() {
        return Groups.player.size();
    }

    public Player getRandomPlayer(Team team) {
        Player[] teamPlayers = new Player[playerCount(team)];
        final int[] i = {0};
        Groups.player.each(player -> {
            if (player.team() == team) {
                teamPlayers[i[0]] = player;
                i[0]++;
            }
        });

        return teamPlayers[getRandomInt(0, teamPlayers.length - 1)];
    }

    public Player getRandomPlayer() {
        return Groups.player.index(getRandomInt(0, Groups.player.size() - 1));
    }
}
