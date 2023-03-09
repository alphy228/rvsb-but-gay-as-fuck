package net.voiddustry.redvsblue;

import arc.Events;
import arc.graphics.Color;
import arc.util.CommandHandler;

import arc.util.Reflect;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.mod.Plugin;
import mindustry.type.UnitType;

import mindustry.ui.Menus;
import mindustry.world.Block;
import mindustry.world.Tile;
import net.voiddustry.redvsblue.Admin.NavMesh.NavMesh;

import java.util.*;

@SuppressWarnings("unused")
public class RedVsBluePlugin extends Plugin {

    public final HashMap<String, Boolean> playerInBuildMode = new HashMap<>();
    public final HashMap<String, Block> selectedBuildBlock = new HashMap<>();
    public static final HashMap<String, PlayerData> players = new HashMap<>();

    public Map<Player, Integer> timer = new HashMap<>();

    public float blueSpawnX, blueSpawnY, redSpawnX, redSpawnY;

    private int stage = 0;

    public void init() {

        Timer.schedule(() -> timer.replaceAll((player, time) -> time++), 0, 1);

        Timer.schedule(() -> Groups.player.each(p -> {
            if (p.team() == Team.blue) {
                players.get(p.uuid()).setScore(players.get(p.uuid()).getScore() + 2);
                p.sendMessage(Bundle.get("game.salary", p.locale));

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
                Unit unit = getRandomStartingUnit().spawn(Team.blue, blueSpawnX, blueSpawnY);

                data.setUnit(unit);
                player.unit(unit);
            }
            if (!playerInBuildMode.containsKey(player.uuid())) {
                playerInBuildMode.put(player.uuid(), false);
            }
            if (!selectedBuildBlock.containsKey(player.uuid())) {
                selectedBuildBlock.put(player.uuid(), Blocks.scrapWall);
            }
            if (!timer.containsKey(player)) {
                timer.put(player, 0);
            }
        });

        Events.on(EventType.UnitBulletDestroyEvent.class, event -> {
            if (event.unit != null && event.bullet.owner() instanceof Unit killer) {
                if (killer.isPlayer()) {
                    players.get(killer.getPlayer().uuid()).addScore(killer.team() == Team.blue ? 2 : 1);
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
                if (playerCount(Team.crux) >= 5)
                    spawnBoss();
            }
        });

        Events.on(EventType.WorldLoadEvent.class, event -> Timer.schedule(() -> {
            NavMesh.init();
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

                    Unit unit = getRandomStartingUnit().spawn(Team.blue, blueSpawnX, blueSpawnY);

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

            Groups.unit.each(u -> Call.label(player.con, "[scarlet]+", 0.01F, u.x, u.y));

            Call.setHudText(player.con(), Bundle.format("game.hud", Bundle.findLocale(player.locale()), Math.floor(unit.health()), Math.floor(unit.shield()), data.getScore(), stage));

            if (playerInBuildMode.get(player.uuid())) {
                Tile position = Vars.world.tile(Math.round(player.mouseX / 8), Math.round(player.mouseY / 8));

                String text = "[gray][\uE805]";
                String textAnnounce = "[gray]";

                if (selectedBuildBlock.get(player.uuid()) != Blocks.air) {
                    if (Objects.equals(position.block().name, "air")) {
                        if (timer.get(player) >= 2) {
                            text = "[lime][\uE805]";
                            textAnnounce = String.valueOf(selectedBuildBlock.get(player.uuid()));
                        } else {
                            text = "[yellow][\uE805]";
                            textAnnounce = Bundle.get("build.cooldown", player.locale);
                        }
                    } else {
                        text = "[scarlet][\uE868]";
                    }
                } else if (selectedBuildBlock.get(player.uuid()) == Blocks.air) {
                    if (timer.get(player) >= 2) {
                        text = "[lime][\uE805]";
                        textAnnounce = Bundle.get("build.destroy-wall", player.locale);
                    } else {
                        text = "[yellow][\uE805]";
                        textAnnounce = Bundle.get("build.cooldown", player.locale);
                    }
                }

                Call.label(player.con, text, 0.1F, (float) ((Math.round(player.mouseX / 8)) * 8), (float) ((Math.round(player.mouseY / 8)) * 8));
                Call.label(player.con, textAnnounce, 0.1F, (float) ((Math.round(player.mouseX / 8)) * 8), (float) (((Math.round(player.mouseY / 8)) * 8) - 5));

                if (player.shooting && timer.get(player) >= 2) {
                    if (Objects.equals(position.block().name, "air")) {
                        Vars.world.tile(Math.round(player.mouseX / 8), Math.round(player.mouseY / 8)).setNet(selectedBuildBlock.get(player.uuid()), player.team(), 0);
                        Call.effect(Reflect.get(Fx.class, "dynamicExplosion"), position.x * 8, position.y * 8, 0.5F, Color.blue);
                        timer.put(player, 0);
                    } else if (selectedBuildBlock.get(player.uuid()) == Blocks.air) {
                        Vars.world.tile(Math.round(player.mouseX / 8), Math.round(player.mouseY / 8)).setNet(selectedBuildBlock.get(player.uuid()), player.team(), 0);
                        Call.effect(Reflect.get(Fx.class, "heal"), position.x * 8, position.y * 8, 1, Color.blue);
                        timer.put(player, 0);
                    }
                }
                if (player.unit() != data.getUnit()) {
                    player.unit(data.getUnit());
                }
                // Other
                if (Objects.equals(player.uuid(), "MFuSMtDs7JgAAAAAwMFaPA==")) {
                    player.name = "[#7]" + randomChar() + " [#7]P[#8]o[#9]z[#A]i[#B]t[#C]i[#D]ve? [#7]" + randomChar();
                }
            }

        }));
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("b", "", "Open block select menu", ((args, player) -> openBlockSelectMenu(player)));

        handler.<Player>register("build", "", "Toggle build mode", ((args, player) -> {
            if (playerInBuildMode.get(player.uuid())) {
                playerInBuildMode.put(player.uuid(), false);
                player.sendMessage("[scarlet]Building Disabled");
            } else {
                playerInBuildMode.put(player.uuid(), true);
                player.sendMessage("[lime]Building Enabled");
            }
        }));

        handler.<Player>register("navmesh", "<name> <value>", "NavMesh editor", ((args, player) -> {
            if (!player.admin) {
                player.sendMessage(Bundle.get("commands.no-admin", player.locale));
            } else {
                switch (args[1]) {
                    case "edit" -> {
                        if (Objects.equals(args[2], "enable")) {
                            NavMesh.editMode(player, true);
                        } else if (Objects.equals(args[2], "disable")) {
                            NavMesh.editMode(player, false);
                        } else {
                            player.sendMessage(Bundle.get("commands.admin.navmesh.invalid", player.locale));
                        }
                    }
                    case "save" -> {
                    } // TODO:
                    case "reload" -> {
                    }
                }
            }
        }));
    }


    public static int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private static char randomChar() {
        Random r = new Random();
        return (char) (r.nextInt(26) + 'A');
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
        final int[] i = { 0 };
        Groups.player.each(p -> {
            if (p.team() == team)
                i[0]++;
        });
        return i[0];
    }

    public static int playerCount() {
        return Groups.player.size();
    }

    public Player getRandomPlayer(Team team) {
        Player[] teamPlayers = new Player[playerCount(team)];
        final int[] i = { 0 };
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

    public UnitType getRandomStartingUnit() {
        switch (getRandomInt(1, 10)) {
            case 1, 2, 3, 4 -> {
                return UnitTypes.nova;
            }
            case 5, 6, 7 -> {
                return UnitTypes.merui;
            }
            case 8, 9 -> {
                return UnitTypes.flare;
            }
            case 10 -> {
                return UnitTypes.mono;
            }

        }
        return null;
    }

    // Events | Logic

    public void initRules() {
        Vars.state.rules.hideBannedBlocks = true;
        Vars.state.rules.bannedBlocks.addAll();
    }

    public void spawnBoss() {
        Unit boss = UnitTypes.antumbra.spawn(Team.crux, redSpawnX, redSpawnY);
        boss.health(14000);

        Player player = getRandomPlayer(Team.crux);

        if (!boss.dead()) {
            Call.unitControl(player, boss);

            sendBundled("game.boss.spawn", player.name());
        }
    }

    public void openBlockSelectMenu(Player player) {
        int menu = Menus.registerMenu((playerInMenu, option) -> {
            switch (option) {
                case 0 -> selectedBuildBlock.put(player.uuid(), Blocks.scrapWall);
                case 1 -> selectedBuildBlock.put(player.uuid(), Blocks.copperWall);
                case 2 -> selectedBuildBlock.put(player.uuid(), Blocks.titaniumWall);

                case 3 -> selectedBuildBlock.put(player.uuid(), Blocks.thoriumWall);
                case 4 -> selectedBuildBlock.put(player.uuid(), Blocks.door);
                case 5 -> selectedBuildBlock.put(player.uuid(), Blocks.phaseWall);

                case 6 -> selectedBuildBlock.put(player.uuid(), Blocks.air);
            }
        });
        String[][] buttonsRow = {
            {
                "\uF8A0", // scrap-wall
                "\uF8AE", // copper-wall
                "\uF8AC", // titanium-wall
            },
            {
                "\uF8A8", // thorium-wall
                "\uF8A2", // door-wall
                "\uF8A6" // phase-wall
            },
            {
                "[scarlet]Destroy Wall"
            }
        };
        Call.menu(player.con, menu, "[cyan]Select Block To Build", "", buttonsRow);

    }


}
