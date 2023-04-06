package net.voiddustry.redvsblue.util;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.Rand;
import arc.struct.Seq;
import arc.util.Log;

import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.game.Team;
import mindustry.gen.*;

import mindustry.logic.LExecutor;
import mindustry.maps.Map;
import mindustry.maps.MapException;
import mindustry.net.WorldReloader;
import mindustry.type.UnitType;
import mindustry.ui.Menus;

import java.util.Arrays;
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
    public static boolean gameRun;
    public static boolean gameover;
    public static boolean hardcore;
    public static int money_per_min = 2;

    public static void initRules() {
        for ( Block block : Vars.content.blocks()) {
            state.rules.bannedBlocks.add(block);
        }

        state.rules.waveSpacing = Integer.MAX_VALUE;
        state.rules.waves = true;
        state.rules.bannedUnits.add(UnitTypes.alpha);

        state.rules.hideBannedBlocks = true;
        state.rules.unitAmmo = true;

        state.rules.teams.get(Team.malis).blockHealthMultiplier = 2;

        money_per_min = 2;

        Call.setRules(state.rules);
    }

    public static void launchGameStartTimer() {
        int[] i = {60};
        Timer.Task task = new Timer.Task() {
            @Override
            public void run() {
                announceBundled("game.game-starts-soon", 1, i[0]);
                i[0]--;
                if (i[0] <= 0) {
                    gameRun = true;
                    this.cancel();
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleTask(task, 0, 1);
    }

    public static void initStats() {
        // Health

        UnitTypes.stell.health = 400;
        UnitTypes.mono.health = 1000;
        UnitTypes.retusa.health = 570;

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
        return UnitTypes.nova;
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
        state.rules.lighting = true;
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

    public static void announceBundled(String key, int duration) {
        Groups.player.forEach(p -> {
            Locale locale = Bundle.findLocale(p.locale);
            String text = Bundle.get(key, locale);
            Call.infoPopup(p.con, text, duration, 0, 0, 0, -200, 0);
        });
    }

    public static void announceBundled(String key, int duration, Object... format) {
        Groups.player.forEach(p -> {
            Locale locale = Bundle.findLocale(p.locale);
            String text = Bundle.format(key, locale, format);
            Call.infoPopup(p.con, text, duration, 0, 0, 0, -200, 0);
        });
    }

}
