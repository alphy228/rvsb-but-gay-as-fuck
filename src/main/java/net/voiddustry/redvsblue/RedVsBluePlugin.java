package net.voiddustry.redvsblue;

import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.mod.Plugin;
import mindustry.net.Administration;
import mindustry.type.UnitType;
import mindustry.ui.Menus;
import mindustry.world.Block;
import mindustry.world.Tile;
import net.voiddustry.redvsblue.admin.Admin;
import net.voiddustry.redvsblue.ai.AirAI;
import net.voiddustry.redvsblue.ai.BluePlayerTarget;
import net.voiddustry.redvsblue.ai.StalkerGroundAI;
import net.voiddustry.redvsblue.ai.StalkerSuicideAI;
import net.voiddustry.redvsblue.evolution.Evolution;
import net.voiddustry.redvsblue.evolution.Evolutions;
import net.voiddustry.redvsblue.game.StationsMenu;
import net.voiddustry.redvsblue.game.building.BuildBlock;
import net.voiddustry.redvsblue.game.building.BuildTicket;
import net.voiddustry.redvsblue.game.stations.*;
import net.voiddustry.redvsblue.util.MapVote;
import net.voiddustry.redvsblue.util.UnitsConfig;
import net.voiddustry.redvsblue.util.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static net.voiddustry.redvsblue.util.MapVote.callMapVoting;
import static net.voiddustry.redvsblue.util.Utils.*;
import static net.voiddustry.redvsblue.util.WebhookUtils.*;

@SuppressWarnings("unused")
public class RedVsBluePlugin extends Plugin {
    public static final int bluePlayerTargeting;

    static {
        Pathfinder.fieldTypes.add(BluePlayerTarget::new);
        bluePlayerTargeting = Pathfinder.fieldTypes.size - 1;
    }

    public static final HashMap<String, Boolean> playerInBuildMode = new HashMap<>();
    public static final HashMap<String, Block> selectedBuildBlock = new HashMap<>();
    public static final HashMap<String, PlayerData> players = new HashMap<>();

    public Map<Player, Integer> timer = new HashMap<>();

    public float blueSpawnX;
    public float blueSpawnY;
    public static float redSpawnX;
    public static float redSpawnY;

    public static int stage = 0;
    public static boolean playing = false;

    private final int evolutionMenu = Menus.registerMenu((player, option) -> {
        if (option == -1) return;

        Evolution evolution = Evolutions.evolutions.get(player.unit().type().name);
        Evolution evolutionOption = Evolutions.evolutions.get(evolution.evolutions[option]);

        PlayerData playerData = players.get(player.uuid());

        if (playerData.getScore() >= evolutionOption.cost) {
            if (player.tileOn().block() == Blocks.air) {
                Unit unit = evolutionOption.unitType.spawn(Team.blue, player.x(), player.y());
                unit.health = unit.type.health/2;

                if (!unit.dead()) {
                    Unit oldUnit = playerData.getUnit();
                    playerData.setUnit(unit);

                    player.unit(unit);
                    oldUnit.kill();

                    playerData.subtractScore(evolutionOption.cost);
                    playerData.setEvolutionStage(evolutionOption.tier);

                    Utils.sendBundled("game.evolved", player.name(), evolution.evolutions[option]);
                }
            }
        }
    });

    @Override
    public void init() {
        sendServerStartMessage();

        Utils.initStats();
        Utils.initTimers();
        Utils.loadContent();

        for (UnitType unit : Vars.content.units()) {
            if (unit == UnitTypes.crawler) {
                unit.aiController = StalkerSuicideAI::new;
            }
            if (!unit.flying) {
                unit.aiController = StalkerGroundAI::new;
            }
            if (unit.flying) {
                unit.aiController = AirAI::new;
            }
            if (unit.naval) {
                unit.flying = true;
            }


        }

        Timer.schedule(() -> timer.replaceAll((player, time) -> time = time + 1), 0, 1);

        Timer.schedule(() -> Groups.player.each(p -> {
            if (p.team() == Team.blue) {
                players.get(p.uuid()).setScore(players.get(p.uuid()).getScore() + money_per_min);
                p.sendMessage(Bundle.format("game.salary", Bundle.findLocale(p.locale), money_per_min));

            }
        }), 0, 60);

        Events.on(EventType.PlayerJoin.class, event -> {
            Player player = event.player;

            if (players.containsKey(player.uuid())) {
                PlayerData data = players.get(player.uuid());
                player.team(data.getTeam());
                if (player.team() == Team.blue) {
                    player.unit(data.getUnit());
                }

            } else {
                players.put(player.uuid(), new PlayerData(player));
                PlayerData data = players.get(player.uuid());
                Unit unit = getStartingUnit().spawn(Team.blue, blueSpawnX, blueSpawnY);

                data.setUnit(unit);
                player.unit(unit);
            }
            if (!playerInBuildMode.containsKey(player.uuid())) {
                playerInBuildMode.put(player.uuid(), false);
            }
            if (!selectedBuildBlock.containsKey(player.uuid())) {
                selectedBuildBlock.put(player.uuid(), Blocks.thoriumWall);
            }
            if (!timer.containsKey(player)) {
                timer.put(player, 0);
            }

            if (playing && player.team() == Team.crux) {
                spawnUnitForCrux(event.player);
            }

            Call.openURI(player.con, "https://discord.gg/KkBjRmb5Db");

           sendPlayerJoinMessage(player.plainName());
        });

        Events.on(EventType.PlayerLeave.class, event -> sendPlayerLeaveMessage(event.player.plainName()));

        Events.on(EventType.PlayerChatEvent.class, event -> {
            sendPlayerChatMessage(event.message, event.player.plainName());
            Call.sound(Sounds.chatMessage, 2, 2, 1);
            if (Utils.voting) {
                if (Strings.canParseInt(event.message)) {
                    MapVote.registerVote(event.player, Strings.parseInt(event.message));
                }
            }
        });

        Events.on(EventType.UnitBulletDestroyEvent.class, event -> {
            if (event.unit != null && event.bullet.owner() instanceof Unit killer) {
                if (killer.isPlayer()) {
                    PlayerData data = players.get(killer.getPlayer().uuid());
                    players.get(killer.getPlayer().uuid()).addScore(killer.team() == Team.blue ? data.getLevel() : 1);
                    Call.label(killer.getPlayer().con, killer.team() == Team.blue ? "[lime]+" + data.getLevel() : "[lime]+1", 2, event.unit.x, event.unit.y);
                    data.addExp(1);
                    processLevel(killer.getPlayer(), data);
                    if (event.unit.isPlayer()) {
                        sendPlayerKillMessage(killer.getPlayer().plainName(), event.unit.getPlayer().plainName());
                    }
                }
            }
        });

        Events.on(EventType.PlayerBanEvent.class, event -> sendPlayerBanMessage(event.player));

        Events.on(EventType.UnitDestroyEvent.class, event -> {
            if (event.unit.isPlayer()) {
                if (event.unit.team() == Team.blue) {
                    event.unit.getPlayer().team(Team.crux);
                    PlayerData data = players.get(event.unit.getPlayer().uuid());
                    data.setTeam(Team.crux);
                    data.setScore(0);
                } else if (playing && event.unit.getPlayer().team() == Team.crux) {
                    spawnUnitForCrux(event.unit.getPlayer());
                }
            }
            gameOverCheck();
        });

        Events.on(EventType.WaveEvent.class, event -> {
            stage = (int) Math.floor(Vars.state.wave / 6f) + 1;

            if (Vars.state.wave % 6 == 0 && stage > 1) {
                if (playerCount(Team.crux) >= 5)
                    Utils.spawnBoss();
            }
            if (Vars.state.wave == 102) {
                sendGameWinMessage();
            }
        });

        Events.on(EventType.GameOverEvent.class, event -> sendGameOverMessage());

        Events.on(EventType.WorldLoadEvent.class, event -> {
            Miner.clearMiners();
            RepairPoint.clearPoints();
            AmmoBox.clearPoints();
            Laboratory.clearLabs();
            Turret.clearTurrets();

            initRules();
            Groups.player.each(p -> {
                PlayerData data = players.get(p.uuid());
                data.setUnit(null);
                data.setExp(0);
                data.setLevel(1);
            });
        });

        Events.on(EventType.WorldLoadEvent.class, event -> Timer.schedule(() -> {
            players.clear();
            String mapname = Vars.state.map.file.file().getName();
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
                    players.put(player.uuid(), new PlayerData(player));

                    Unit unit = getStartingUnit().spawn(Team.blue, blueSpawnX, blueSpawnY);
                    PlayerData data = players.get(player.uuid());
                    data.setUnit(unit);

                }
            });

            playing = true;
            gameover = false;
            hardcore = false;
            sendGameStartMessage();
            initRules();

            Call.setRules(Vars.state.rules);
        }, 5));

        Events.run(EventType.Trigger.update, () -> {
            if (playing) {

                Groups.unit.each(u -> {
                    if (u.team == Team.crux) {
                        u.ammo = u.type.ammoCapacity;
                    }
                });
                Groups.player.each(player -> {

                    Unit unit = player.unit();
                    if(!players.containsKey(player.uuid())) {
                        players.put(player.uuid(), new PlayerData(player));
                    }
                    PlayerData data = players.get(player.uuid());

                    String textHud = (data.getLevel() == 5)? "[scarlet]Max" : "[accent]" + data.getExp() + " / " + data.getMaxExp();

                    Call.label(player.con, "[scarlet]+", 0.01F, player.x, player.y);

                    Call.setHudText(player.con(), Bundle.format("game.hud", Bundle.findLocale(player.locale()), Administration.Config.serverName.get(), Math.floor(unit.health()), Math.floor(unit.shield()), data.getScore(), stage, data.getLevel(), textHud));

                    if (playing && data.getUnit() != null) {
                        if (data.getUnit().dead) {
                            data.setTeam(Team.crux);
                            player.team(data.getTeam());
                        }
                    }

                    if (playing && data.getUnit() != null) {
                        if (!data.getUnit().dead) {
                            player.unit(data.getUnit());
                        }
                    }


                });
            }
        });

    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("report", "<player> <text...>","[cyan]- Report player to discord server. For false report you will get ban.", ((args, player) -> {
            if (Objects.equals(args[0], " ")) return;
            sendReport(args[0], player, args);
            player.sendMessage(Bundle.get("report.reportSend", player.locale));
        }));

        handler.<Player>register("e", "Open evolution menu", ((args, player) -> {
            PlayerData data = players.get(player.uuid());
            if (data.isCanEvolve()) {
                if (player.team() != Team.blue) return;

                Locale locale = Bundle.findLocale(player.locale());

                Evolution evolution = Evolutions.evolutions.get(player.unit().type().name);

                String[][] buttons = new String[evolution.evolutions.length][1];

                for (int i = 0; i < evolution.evolutions.length; i++) {
                    buttons[i][0] = Bundle.format("menu.evolution.evolve", locale, evolution.evolutions[i], Evolutions.evolutions.get(evolution.evolutions[i]).cost);
                }

                Call.menu(player.con, evolutionMenu, Bundle.get("menu.evolution.title", locale), Bundle.format("menu.evolution.message", locale, players.get(player.uuid()).getEvolutionStage(), Bundle.get("evolution.branch.initial", locale)), buttons);

            } else {
                player.sendMessage(Bundle.get("evolution.no-lab", player.locale));
            }
        }));

        handler.<Player>register("b", "Open Building menu", (args, player) -> {
            if (playing) {
                BuildBlock.add(new BuildTicket(player, Blocks.berylliumWall, true, false, 2));
            }
        });

        handler.<Player>register("s", "Open stations selecting menu", (args, player) -> {
            StationsMenu.openMenu(player);
        });

        handler.<Player>register("vote-map", "<map-number>", "Vote for specific map", (args, player) -> {
            if (Strings.canParseInt(args[0])) {
                MapVote.registerVote(player, Integer.valueOf(args[0]));
            } else {
                player.sendMessage(Bundle.get("not-a-number", player.locale));
            }
        });

        handler.<Player>register("ap", "Open a Admin Panel, only for admins", (args, player) -> {
            if (player.admin) {
                Admin.openAdminPanelMenu(player);
            }

        });

        handler.<Player>register("gameover", "Only for admins", (args, player) -> {
            if (player.admin) {
                callMapVoting();
            }
        });

        handler.<Player>register("hardcore", "Enable [scarlet]HARDCORE[] mode. Only for admins.", (args, player) -> {
            if (player.admin) {
                enableHardCore();
            }
        });

        handler.<Player>register("discord","Join to discord server", (args, player) -> Call.openURI(player.con, "https://discord.gg/KkBjRmb5Db"));
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("reload", "<config-name>", "Reload config", (args) -> {
            if (Objects.equals(args[0], "units")) {
                int multipler = UnitsConfig.getPrices_multipler();
                Log.info(multipler);
            }
        });

    }

    public static void gameOverCheck() {
        if (playerCount(Team.blue) == 0) {
            gameOver(Team.crux);
        }
    }

    public static void gameOver(Team winner) {
        if (winner == Team.crux) {
            RedVsBluePlugin.playing = false;
            if (!gameover) {
                gameover = true;
                callMapVoting();
                Groups.player.each(p -> {
                    int randomInt = getRandomInt(1, 255);
                    players.get(p.uuid()).setTeam(Team.get(randomInt));
                    p.team(Team.get(randomInt));
                });
            }
        } else if (winner == Team.blue) {
            // TODO: sudo rm rf
        }
    }
}
