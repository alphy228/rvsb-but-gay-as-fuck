package net.voiddustry.redvsblue;

import arc.Events;

import arc.util.*;
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
import mindustry.world.Tile;
import net.voiddustry.redvsblue.admin.Admin;
import net.voiddustry.redvsblue.ai.AirAI;
import net.voiddustry.redvsblue.ai.BluePlayerTarget;
import net.voiddustry.redvsblue.ai.StalkerGroundAI;
import net.voiddustry.redvsblue.ai.StalkerSuicideAI;
import net.voiddustry.redvsblue.game.StationsMenu;
import net.voiddustry.redvsblue.game.building.BuildBlock;
import net.voiddustry.redvsblue.game.building.BuildMenu;
import net.voiddustry.redvsblue.game.crux.ClassChooseMenu;
import net.voiddustry.redvsblue.game.crux.CruxUnit;
import net.voiddustry.redvsblue.game.starting_menu.StartingMenu;
import net.voiddustry.redvsblue.game.stations.*;
import net.voiddustry.redvsblue.logic.AnthicusRadar;
import net.voiddustry.redvsblue.player.Premium;
import net.voiddustry.redvsblue.util.MapVote;
import net.voiddustry.redvsblue.util.UnitsConfig;
import net.voiddustry.redvsblue.util.Utils;

import java.util.HashMap;
import java.util.Objects;

import static net.voiddustry.redvsblue.util.MapVote.callMapVoting;
import static net.voiddustry.redvsblue.util.Utils.*;
import static net.voiddustry.redvsblue.util.WebhookUtils.*;

public class RedVsBluePlugin extends Plugin {
    public static final int bluePlayerTargeting;

    static {
        Pathfinder.fieldTypes.add(BluePlayerTarget::new);
        bluePlayerTargeting = Pathfinder.fieldTypes.size - 1;
    }

    public static final HashMap<String, PlayerData> players = new HashMap<>();

    public float blueSpawnX;
    public float blueSpawnY;

    public static float redSpawnX;
    public static float redSpawnY;

    public static int stage = 0;
    public static float stageTimer = 0;
    public static boolean playing = false;



    static Timer.Task task = new Timer.Task() {
        @Override
        public void run() {
            stage++;
            stageTimer = 300;
            spawnBoss();
            announceBundled("game.new-stage", 15, stage);
            ClassChooseMenu.updateUnitsMap();
            if (stage >= 11) {
                gameOver(Team.blue);
            }
        }
    };

    @Override
    public void init() {

        sendServerStartMessage();

        Log.info("&gRedVsBlue Plugin &rStarted!");

        Utils.initStats();
        Utils.initRules();
        Utils.initTimers();
        Utils.loadContent();
        Premium.init();
        AnthicusRadar.init();

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


        Events.on(EventType.PlayerJoin.class, event -> {
            Player player = event.player;
            if (Objects.equals(player.uuid(), "voiddustrycAAAAAd7OpXA==")) {
                player.name = "[cyan]< \uE80F > " + player.name;
            } else if (player.admin) {
                player.name = "[scarlet]< \uE82C > " + player.name;
            } else if (Premium.isPremium(player)) {
                player.name = "[gold]< \uE809 > " + player.name;
            } else {
                player.name = "[white]< \uE872 > " + player.name;
            }
            if (playing) {
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
                if (playing && player.team() == Team.crux) {
                    CruxUnit.callSpawn(player);
                }
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

        Events.on(EventType.BlockDestroyEvent.class, event -> {
        });

        Events.on(EventType.PlayerBanEvent.class, event -> sendPlayerBanMessage(event.player));

        Events.on(EventType.UnitDestroyEvent.class, event -> {
            if (event.unit.isPlayer()) {
                if (event.unit.team() == Team.blue) {
                    event.unit.getPlayer().team(Team.crux);
                    PlayerData data = players.get(event.unit.getPlayer().uuid());
                    data.setTeam(Team.crux);
                    data.setScore(0);

                    UnitTypes.renale.spawn(Team.malis, event.unit.x, event.unit.y).kill();

                    ClassChooseMenu.selectedUnit.put(event.unit.getPlayer().uuid(), UnitTypes.crawler);
                    CruxUnit.callSpawn(event.unit.getPlayer());
                } else if (event.unit.getPlayer().team() == Team.crux) {
                  CruxUnit.callSpawn(event.unit.getPlayer());
                }
            }
            gameOverCheck();
        });

        Events.on(EventType.GameOverEvent.class, event -> sendGameOverMessage());

        Events.on(EventType.WorldLoadEvent.class, event -> {
            Miner.clearMiners();
            RepairPoint.clearPoints();
            AmmoBox.clearPoints();
            Laboratory.clearLabs();
            BuildBlock.clear();
            UnitConstructor.clearMap();

            initRules();

            StartingMenu.canOpenMenu = true;

            Groups.player.each(p -> {
                PlayerData data = players.get(p.uuid());
                data.setUnit(null);
                data.setExp(0);
                data.setLevel(1);
            });
        });
// TODO:
//        Events.on(EventType.BlockBuildBeginEvent.class, event -> {
//            if (!event.tile.block().isAir()) {
//                Vars.world.tile(event.tile.pos()).removeNet();
//            }
//        });

        Events.on(EventType.WorldLoadEvent.class, event -> Timer.schedule(() -> {

            players.clear();

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

            Timer timer = new Timer();
            timer.scheduleTask(task, 300, 300);

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
            gameRun = false;
            gameover = false;
            hardcore = false;
            stage = 1;
            stageTimer = 420;
            voting = false;

            ClassChooseMenu.selectedUnit.clear();
            ClassChooseMenu.updateUnitsMap();

            sendGameStartMessage();
            initRules();
            launchGameStartTimer();

            Call.setRules(Vars.state.rules);

        }, 10));

        Events.run(EventType.Trigger.update, () -> {
            CruxUnit.checkUnitCount();

            int minutes = (int) Math.floor(stageTimer / 60);
            int seconds = (int) (stageTimer - minutes * 60);

            String minutesString = ((minutes < 10)? "0" : "") + minutes;
            String secondsString = ((seconds < 10)? "0" : "") + seconds;

            String time = minutesString + ":" + secondsString;
            String playersText = "[royal]\uE872 " + playerCount(Team.blue) + " [scarlet]\uE872 " + playerCount(Team.crux) + "[gray] | [white]\uE872 " + playerCount();

            if (playing) {
                Groups.unit.each(u -> {
                    if (u.team == Team.crux) {
                        u.ammo = u.type.ammoCapacity;
                    }

                });
                Groups.player.each(player -> {

                    Groups.unit.each(u -> {
                        if (u.getPlayer() != player) {
                            Call.label(player.con, "[orange]X", 0.01F, u.x, u.y);
                        }
                    });
                    Unit unit = player.unit();
                    if(!players.containsKey(player.uuid())) {
                        players.put(player.uuid(), new PlayerData(player));
                    }
                    PlayerData data = players.get(player.uuid());

                    String textHud = (data.getLevel() == 5)? "[scarlet]Max" : "[accent]" + data.getExp() + " / " + data.getMaxExp();

                    Call.label(player.con, "[scarlet]+", 0.01F, player.x, player.y);
                    Call.label(player.con, "[scarlet]X", 0.01F, player.mouseX, player.mouseY);

                    String hudText = Bundle.format("game.hud", Bundle.findLocale(player.locale()), Administration.Config.serverName.get(), Math.floor(unit.health()), Math.floor(unit.shield()), data.getScore(), stage, time, playersText, data.getLevel(), textHud);
                    Call.infoPopupReliable(player.con, hudText, .03F, Align.topLeft, (player.con.mobile)? 160 : 90, 5, 0, 0);

                    if (playing && data.getUnit() != null) {
                        if (data.getUnit().dead) {
                            data.setTeam(Team.crux);
                            player.team(data.getTeam());
                        }
                    }

                    if (playing && data.getUnit() != null && player.team() == Team.blue) {
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

        handler.<Player>register("b", "Open Building menu", (args, player) -> {
            if (playing && player.team() == Team.blue) {
                BuildMenu.openMenu(player);
            }
        });

        handler.<Player>register("c", "Open Class Choose menu, only for crux", (args, player) -> {
            if (player.team() == Team.crux) {
                ClassChooseMenu.openMenu(player);
            } else {
                player.sendMessage(Bundle.get("", player.locale));
            }
        });

        handler.<Player>register("s", "Open stations selecting menu", (args, player) -> StationsMenu.openMenu(player));

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
                task.cancel();
            }
        });

        handler.<Player>register("blue", "Makes you blue, works only before stage 3", (args, player) -> {
            if (stage <= 3 && playing) {
                Unit oldUnit = player.unit();

                if (player.unit() != null) oldUnit.kill();

                player.team(Team.blue);
                players.put(player.uuid(), new PlayerData(player));
                PlayerData data = players.get(player.uuid());
                Unit unit = getStartingUnit().spawn(Team.blue, blueSpawnX, blueSpawnY);

                data.setUnit(unit);
                player.unit(unit);
                sendBundled("game.redeemed", player.name);
            } else {
                player.sendMessage(Bundle.get("game.late", player.locale));
            }
        });

        handler.<Player>register("reset-data", "Use that if you blue and dont have unit.", (args, player) -> {
            if (player.team() == Team.blue) {
                players.put(player.uuid(), new PlayerData(player));
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
            } else if (Objects.equals(args[0], "premium")) {
                Premium.init();
            }
        });

        handler.register("restart", "ae", (args) -> Groups.player.each(p -> p.kick("[scarlet]Server is going to restart")));
    }

    public static void gameOverCheck() {
        if (playerCount(Team.blue) == 0) {
            gameOver(Team.crux);
        }
    }

    public static void gameOver(Team winner) {
        if (winner == Team.crux) {
            if (playing) {
                RedVsBluePlugin.playing = false;
                if (!gameover) {
                    gameover = true;
                    callMapVoting();

                    task.cancel();

                    Groups.player.each(p -> {
                        int randomInt = getRandomInt(1, 255);
                        players.get(p.uuid()).setTeam(Team.get(randomInt));
                        p.team(Team.get(randomInt));
                    });
                }
            }
        } else if (winner == Team.blue) {
            sendGameWinMessage();
            if (!gameover) {
                gameover = true;
                callMapVoting();

                task.cancel();

                Groups.player.each(p -> {
                    int randomInt = getRandomInt(1, 255);
                    players.get(p.uuid()).setTeam(Team.get(randomInt));
                    p.team(Team.get(randomInt));
                });
            }
        }
    }
}
