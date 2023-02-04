package net.voiddustry.redvsblue;

import arc.Events;
import arc.util.CommandHandler;

import mindustry.Vars;
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
}
