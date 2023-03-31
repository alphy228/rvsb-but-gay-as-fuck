package net.voiddustry.redvsblue.util;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Log;

import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.*;

import mindustry.logic.LExecutor;
import mindustry.maps.MapException;
import mindustry.net.WorldReloader;
import mindustry.type.UnitType;
import mindustry.ui.Menus;
import java.util.Locale;
import java.util.Random;

import mindustry.world.Block;
import mindustry.world.Tile;
import net.voiddustry.redvsblue.Bundle;
import net.voiddustry.redvsblue.PlayerData;
import net.voiddustry.redvsblue.RedVsBluePlugin;
import net.voiddustry.redvsblue.game.building.BlocksTypes;
import net.voiddustry.redvsblue.game.building.BuildBlock;
import net.voiddustry.redvsblue.game.stations.*;

import static mindustry.Vars.*;
import static net.voiddustry.redvsblue.RedVsBluePlugin.*;

public class Utils {

    public static boolean voting;
    public static boolean gameover;
    public static boolean hardcore;
    public static int money_per_min = 2;

    public static void initRules() {
        for ( Block block : Vars.content.blocks()) {
            state.rules.bannedBlocks.add(block);
        }
        state.rules.bannedUnits.add(UnitTypes.alpha);

        state.rules.hideBannedBlocks = true;
        state.rules.unitAmmo = true;

        state.rules.teams.get(Team.malis).blockHealthMultiplier = 2;

        money_per_min = 2;

        Call.setRules(state.rules);
    }

    public static void initStats() {
        // Health

        UnitTypes.stell.health = 400;
        UnitTypes.mono.health = 1000;

        // Damage

//        UnitTypes.anthicus.weapons.each(w -> w.name.equals("anthicus-weapon"), w -> w.bullet.damage = 0);

        // Blocks

        Blocks.combustionGenerator.health = 320;
    }

    public static void loadContent() {
        BlocksTypes.load();
    }

    public static void initTimers() {
        Miner.initTimer();
        RepairPoint.initTimer();
        AmmoBox.initTimer();
        Turret.initTimer();
        Laboratory.initTimer();
        BuildBlock.init();
    }

    public static void processLevel(Player player, PlayerData data) {
        if (data.getLevel() < 5) {
            if (data.getExp() >= data.getMaxExp()) {
                int expLimit = data.getExp();
                int expLimitToSet = expLimit + expLimit/4;
                data.setMaxExp(expLimitToSet);
                data.setExp(0);
                data.setLevel(data.getLevel() + 1);
                sendBundled("game.level-up", player.name);
            }
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
                case 0 -> selectedBuildBlock.put(player.uuid(), Blocks.thoriumWall);
                case 1 -> selectedBuildBlock.put(player.uuid(), Blocks.door);

                case 2 -> selectedBuildBlock.put(player.uuid(), Blocks.mender);
                case 3 -> selectedBuildBlock.put(player.uuid(), Blocks.powerNode);
                case 4 -> selectedBuildBlock.put(player.uuid(), Blocks.air);
            }
        });
        String[][] buttonsRow = {
                {
                        "\uF8A8", // thorium-wall
                        "\uF8A2", // door
                },
                {
                        "\uF89B", // mender
                        "\uF87E", // power-node
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

    public static Tile randomTile() {
        int x = Mathf.random(0, Vars.world.width() - 1);
        int y = Mathf.random(0, Vars.world.height() - 1);
        Tile tile = Vars.world.tile(x, y);
        if (tile == null || tile.build != null || !tile.block().isAir() || tile.floor().isLiquid) {
            return randomTile();
        }
        return tile;
    }

    public static void enableHardCore() {
        hardcore = true;
        Vars.state.rules.ambientLight.set(0.2F, 0F, 0F, 0.95F);
        Vars.state.rules.waveSpacing = 1200;
        money_per_min = 6;

        final int[] i = {0};
        Call.setRules(Vars.state.rules);

        Call.announce("[scarlet]HARDCORE mode has been enabled.");

        Timer.Task sounds = new Timer.Task() {
            @Override
            public void run() {
                i[0]++;
                Call.sound(Sounds.explosionbig, 20, 1, 1);
                Call.effect(Fx.dynamicExplosion, randomTile().x*8, randomTile().y*8, 7, Color.red);
                if (i[0] >= 7) {
                    this.cancel();
                }
            }
        };

        Timer.schedule(sounds, 0, 0.3F);

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

}
