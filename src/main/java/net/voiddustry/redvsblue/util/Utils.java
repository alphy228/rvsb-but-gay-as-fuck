package net.voiddustry.redvsblue.util;

import arc.util.Log;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;

import mindustry.maps.MapException;
import mindustry.net.WorldReloader;
import mindustry.type.UnitType;
import mindustry.ui.Menus;
import java.util.Locale;

import mindustry.world.Block;
import net.voiddustry.redvsblue.Bundle;
import net.voiddustry.redvsblue.PlayerData;
import net.voiddustry.redvsblue.RedVsBluePlugin;

import static mindustry.Vars.*;
import static net.voiddustry.redvsblue.RedVsBluePlugin.*;

public class Utils {

    public static boolean voting;
    public static boolean gameover;

    public static void initRules() {
        for ( Block block : Vars.content.blocks()) {
            state.rules.bannedBlocks.add(block);
        }
        state.rules.hideBannedBlocks = true;

        Call.setRules(state.rules);
    }

    public static void initStats() {
        // Health

        UnitTypes.stell.health = 1200;
        UnitTypes.mono.health = 1000;

        // Damage

        UnitTypes.anthicus.weapons.each(w -> w.name.equals("anthicus-weapon"), w -> w.bullet.damage = 0);
    }

    public static void processLevel(Player player, PlayerData data) {
        if (data.getExp() >= data.getMaxExp()) {
            int expLimit = data.getExp();
            int expLimitToSet = expLimit + expLimit/4;
            Log.info(expLimitToSet);
            data.setMaxExp(expLimitToSet);
            data.setExp(0);
            data.setLevel(data.getLevel() + 1);
            sendBundled("game.level-up", player.name);
        }
    }

    public static int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static Player getRandomPlayer(Team team) {
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

    public static Player getRandomPlayer() {
        return Groups.player.index(getRandomInt(0, Groups.player.size() - 1));
    }

    public static void spawnBoss() {
        Unit boss = UnitTypes.antumbra.spawn(Team.crux, RedVsBluePlugin.redSpawnX, RedVsBluePlugin.redSpawnY);
        boss.health(14000);

        Player player = getRandomPlayer(Team.crux);

        if (!boss.dead()) {
            Call.unitControl(player, boss);

            sendBundled("game.boss.spawn", player.name());
        }
    }

    public static int playerCount(Team team) {
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

    public static void openBlockSelectMenu(Player player) {
        int menu = Menus.registerMenu((playerInMenu, option) -> {
            switch (option) {
                case 0 -> selectedBuildBlock.put(player.uuid(), Blocks.titaniumWall);
                case 1 -> selectedBuildBlock.put(player.uuid(), Blocks.door);
                case 2 -> selectedBuildBlock.put(player.uuid(), Blocks.powerNode);

                case 3 -> selectedBuildBlock.put(player.uuid(), Blocks.combustionGenerator);
                case 4 -> selectedBuildBlock.put(player.uuid(), Blocks.mender);
                case 5 -> selectedBuildBlock.put(player.uuid(), Blocks.battery);

                case 6 -> {
                    PlayerData data = players.get(player.uuid());
                    if (data.getScore() < 3) {
                        player.sendMessage(Bundle.get("build.not-enough-money", player.locale));
                    } else {
                        player.unit().addItem(Items.coal, 5);
                        data.setScore(data.getScore() - 3);
                    }
                }
                case 7 -> selectedBuildBlock.put(player.uuid(), Blocks.air);
            }
        });
        String[][] buttonsRow = {
                {
                        "\uF8AC", // titanium-wall
                        "\uF8A2", // door
                        "\uF87E", // power-node
                },
                {
                        "\uF879", // combustionGenerator
                        "\uF89B", // mender
                        "\uF87B" // battery
                },
                {
                        "[gray]Buy 5 Coal"
                },
                {
                        "[scarlet]Destroy Wall"
                }
        };
        Call.menu(player.con, menu, "[cyan]Select Block To Build", "", buttonsRow);

    }

    public static void sendBundled(String key, Object... format) {
        Groups.player.forEach(p -> {
            Locale locale = Bundle.findLocale(p.locale());
            p.sendMessage(Bundle.format(key, locale, format));
        });
    }

    public void sendBundled(String key) {
        Groups.player.forEach(p -> {
            Locale locale = Bundle.findLocale(p.locale());
            p.sendMessage(Bundle.get(key, locale));
        });
    }

    public static UnitType getStartingUnit() {
        switch (getRandomInt(1,11)) {
            case 1, 2, 3, 4 -> {
                return UnitTypes.nova;
            }
            case 5,6,7 -> {
                return UnitTypes.merui;
            }
            case 8,9 -> {
                return UnitTypes.flare;
            }
            case 10 -> {
                return UnitTypes.mono;
            }
        }
        return UnitTypes.alpha;
    }

    public static void spawnUnitForCrux(Player player) { // TODO: Переписать

            if (player.unit().dead) {
                switch (RedVsBluePlugin.stage) {
                    case 1, 2 -> { // Stage 1, 2
                        switch (getRandomInt(1, 3)) {
                            case 1 -> {
                                Unit spawned = UnitTypes.crawler.spawn(Team.crux, redSpawnX, redSpawnY);
                                Call.unitControl(player, spawned);
                                spawned.spawnedByCore = true;
                            }
                            case 2 -> {
                                Unit spawned = UnitTypes.dagger.spawn(Team.crux, redSpawnX, redSpawnY);
                                Call.unitControl(player, spawned);
                                spawned.spawnedByCore = true;
                            }
                        }
                    }
                    case 3, 4 -> { // Stage 3, 4
                        switch (getRandomInt(1, 4)) {
                            case 1 -> {
                                Unit spawned = UnitTypes.mace.spawn(Team.crux, redSpawnX, redSpawnY);
                                spawned.spawnedByCore = true;
                                spawned.health = 350;
                                Call.unitControl(player, spawned);
                            }
                            case 2 -> {
                                Unit spawned = UnitTypes.crawler.spawn(Team.crux, redSpawnX, redSpawnY);
                                spawned.spawnedByCore = true;
                                Call.unitControl(player, spawned);
                            }
                            case 3 -> {
                                Unit spawned = UnitTypes.dagger.spawn(Team.crux, redSpawnX, redSpawnY);
                                spawned.spawnedByCore = true;
                                Call.unitControl(player, spawned);
                            }
                        }
                    }
                    case 5, 6 -> { // Stage 5, 6
                        switch (getRandomInt(1, 4)) {
                            case 1 -> {
                                Unit spawned = UnitTypes.stell.spawn(Team.crux, redSpawnX, redSpawnY);
                                spawned.spawnedByCore = true;
                                Call.unitControl(player, spawned);
                            }
                            case 2 -> {
                                Unit spawned = UnitTypes.mace.spawn(Team.crux, redSpawnX, redSpawnY);
                                spawned.spawnedByCore = true;
                                Call.unitControl(player, spawned);
                            }
                            case 3 -> {
                                Unit spawned = UnitTypes.atrax.spawn(Team.crux, redSpawnX, redSpawnY);
                                spawned.spawnedByCore = true;
                                spawned.health = 400;
                                Call.unitControl(player, spawned);
                            }
                        }
                    }
                    case 7, 8, 9 -> {
                        switch (getRandomInt(1, 4)) {
                            case 1 -> {
                                Unit spawned = UnitTypes.fortress.spawn(Team.crux, redSpawnX, redSpawnY);
                                spawned.spawnedByCore = true;
                                spawned.health = 500;
                                Call.unitControl(player, spawned);
                            }
                            case 2 -> {
                                Unit spawned = UnitTypes.atrax.spawn(Team.crux, redSpawnX, redSpawnY);
                                spawned.spawnedByCore = true;
                                Call.unitControl(player, spawned);
                            }
                            case 3 -> {
                                Unit spawned = UnitTypes.cleroi.spawn(Team.crux, redSpawnX, redSpawnY);
                                spawned.spawnedByCore = true;
                                spawned.health = 800;
                                Call.unitControl(player, spawned);
                            }
                        }
                    }
                    case 10, 11, 12, 13 -> {
                        switch (getRandomInt(1, 3)) {
                            case 1 -> {
                                Unit spawned = UnitTypes.anthicus.spawn(Team.crux, redSpawnX, redSpawnY);
                                spawned.spawnedByCore = true;
                                spawned.health = 1000;
                                Call.unitControl(player, spawned);
                            }
                            case 2 -> {
                                Unit spawned = UnitTypes.spiroct.spawn(Team.crux, redSpawnX, redSpawnY);
                                spawned.spawnedByCore = true;
                                Call.unitControl(player, spawned);
                            }
                        }
                    }
                    case 14, 15, 16 -> {
                        switch (getRandomInt(1, 3)) {
                            case 1 -> {
                                Unit spawned = UnitTypes.precept.spawn(Team.crux, redSpawnX, redSpawnY);
                                spawned.spawnedByCore = true;
                                spawned.health = 3000;
                                Call.unitControl(player, spawned);
                            }
                            case 2 -> {
                                Unit spawned = UnitTypes.quasar.spawn(Team.crux, redSpawnX, redSpawnY);
                                spawned.spawnedByCore = true;
                                Call.unitControl(player, spawned);
                            }
                        }
                    }
                    case 17, 18, 19, 20 -> {
                        Unit spawned = UnitTypes.arkyid.spawn(Team.crux, redSpawnX, redSpawnY);
                        spawned.spawnedByCore = true;
                        Call.unitControl(player, spawned);
                    }
                }
            }
    }

    public static void reloadWorld(Runnable runnable) {
        try {
            var reloader = new WorldReloader();
            reloader.begin();

            runnable.run();
            state.rules = state.map.applyRules(state.rules.mode());
            logic.play();

            reloader.end();
        } catch (MapException e) {
            Log.err("@: @", e.map.name(), e.getMessage());
        }
    }
}
