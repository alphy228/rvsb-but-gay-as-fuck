package net.voiddustry.redvsblue;

import arc.Events;
import arc.util.CommandHandler;

import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.mod.Plugin;
import mindustry.world.Tile;
import net.voiddustry.redvsblue.Admin.LogEntry;
import net.voiddustry.redvsblue.Admin.LogTypes.UnitKillEntry;
import net.voiddustry.redvsblue.Admin.Logs;

import java.util.HashMap;
import java.util.Locale;

import static net.voiddustry.redvsblue.Admin.Logs.*;

public class RedVsBluePlugin extends Plugin {
    private final HashMap<String, PlayerData> players = new HashMap<>();

    private float blueSpawnX, blueSpawnY, redSpawnX, redSpawnY;

    private int stage = 0;

    public static int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public void init() {
        Events.on(EventType.PlayerJoin.class, event -> {
            Player player = event.player;
            if (players.containsKey(player.uuid())) {
                PlayerData data = players.get(player.uuid());
                player.team(data.getTeam());
            } else {
                Unit unit = UnitTypes.nova.spawn(Team.blue, blueSpawnX, blueSpawnY);

                if (!unit.dead) {
                    Call.unitControl(player, unit);

                    unit.spawnedByCore(true);
                }

                players.put(player.uuid(), new PlayerData(player));
            }
        });

        Events.on(EventType.UnitBulletDestroyEvent.class, event -> {
            if (event.unit != null && event.bullet.owner() instanceof Unit killer) {
                LogEntry entry = new UnitKillEntry(event.unit, killer);
                Logs.addLogEntry(entry);

                if (killer.isPlayer()) {
                    players.get(killer.getPlayer().uuid()).addScore(killer.team() == Team.blue? 2 : 1);
                }

                if (event.unit.isPlayer()) {
                    if (event.unit.team() == Team.blue) {
                        event.unit.getPlayer().team(Team.crux);
                        PlayerData data = players.get(event.unit.getPlayer().uuid());
                        data.setTeam(Team.crux);
                        data.setScore(0);
                    }
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

        Events.on(EventType.WorldLoadEvent.class, event -> Timer.schedule(() -> {
            Vars.state.rules.canGameOver = false;
            Vars.state.rules.unitCap = 32;

            Building core = Vars.state.teams.cores(Team.blue).first();

            blueSpawnX = core.x();
            blueSpawnY = core.y();

            Vars.state.teams.cores(Team.blue).each(Building::kill);

            for (int x = 0; x < Vars.state.map.width; x++) {
                for (int y = 0; y < Vars.state.map.height; y++) {
                    Tile tile = Vars.world.tile(x, y);
                    if (tile.overlay() == Blocks.spawn) {
                        redSpawnX = tile.getX();
                        redSpawnY = tile.getY();
                        break;
                    }
                }
            }

            Groups.player.each(player -> {
                if (player != null) {
                    player.team(Team.blue);

                    Unit unit = UnitTypes.nova.spawn(Team.blue, blueSpawnX, blueSpawnY);

                    if (!unit.dead) {
                        Call.unitControl(player, unit);

                        unit.spawnedByCore(true);
                    }
                }
            });
        }, 1));

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
