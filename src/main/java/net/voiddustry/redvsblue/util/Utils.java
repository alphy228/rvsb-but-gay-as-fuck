package net.voiddustry.redvsblue.util;

import arc.graphics.Color;

import arc.struct.Seq;
import arc.util.Time;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.Team;
import mindustry.gen.*;

import mindustry.type.UnitType;
import java.util.Locale;

import mindustry.type.Weapon;
import mindustry.world.Block;

import net.voiddustry.redvsblue.Bundle;
import net.voiddustry.redvsblue.PlayerData;
import net.voiddustry.redvsblue.game.building.BlocksTypes;
import net.voiddustry.redvsblue.game.building.BuildBlock;
import net.voiddustry.redvsblue.game.crux.StageUnits;
import net.voiddustry.redvsblue.game.starting_menu.StartingItems;
import net.voiddustry.redvsblue.game.starting_menu.StartingMenu;
import net.voiddustry.redvsblue.game.stations.*;

import static mindustry.Vars.*;
import static net.voiddustry.redvsblue.RedVsBluePlugin.*;

public class Utils {

    public static boolean voting;
    public static boolean gameRun;
    public static boolean gameover;
    public static boolean hardcore;
    public static int money_per_min = 3;

    public static void initRules() {
        for ( Block block : Vars.content.blocks()) {
            state.rules.bannedBlocks.add(block);
        }

        state.rules.waveSpacing = Integer.MAX_VALUE;
        state.rules.waves = true;
        state.rules.bannedUnits.add(UnitTypes.alpha);

        state.rules.hideBannedBlocks = true;

        state.rules.teams.get(Team.malis).blockHealthMultiplier = 2;

        Call.setRules(state.rules);
    }

    public static void launchGameStartTimer() {
        int[] i = {120};
        Timer.Task task = new Timer.Task() {
            @Override
            public void run() {
                announceBundled("game.game-starts-soon", 1, i[0]);
                i[0]--;
                if (i[0] <= 0) {
                    gameRun = true;
                    this.cancel();
                    StartingMenu.canOpenMenu = false;
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleTask(task, 0, 1);
    }

    public static void initStats() {
        // Health

        UnitTypes.stell.health = 400;
        UnitTypes.mono.health = 200;
        UnitTypes.locus.armor = -20;
        UnitTypes.retusa.health = 800;
        UnitTypes.pulsar.health = 500;
        UnitTypes.pulsar.armor = 6;
        // Inventory

        UnitTypes.mono.itemCapacity = 40;
        UnitTypes.pulsar.itemCapacity = 40;
        // Damage

        UnitTypes.mace.weapons.each(w -> w.name.equals("flamethrower"), w -> w.bullet.damage = 17);
        UnitTypes.avert.weapons.each(w -> w.name.equals("avert-weapon"), w -> w.bullet.damage = 35);
        UnitTypes.mega.weapons.each(w -> w.bullet.damage = 30);

        UnitTypes.mono.weapons.add(new Weapon("mount-weapon"){{
            reload = 30f;
            rotate = true;
            ejectEffect = Fx.casing1;
            bullet = new BasicBulletType(3f, 24){{
                width = 7f;
                height = 9f;
                lifetime = 59f;
                ammoMultiplier = 2;
            }};
        }});

        // Blocks

        Blocks.combustionGenerator.health = 320;
        Blocks.mender.health = 120;

        Blocks.tungstenWall.targetable = false;
        Blocks.tungstenWall.health = 99999;
        Blocks.tungstenWallLarge.targetable = false;
        Blocks.tungstenWallLarge.health = 99999;
    }

    public static void loadContent() {
        BlocksTypes.load();
        StartingItems.load();
    }

    public static void initTimers() {
        Miner.initTimer();
        RepairPoint.initTimer();
        Laboratory.initTimer();
        Booster.initTimer();
        ArmorWorkbench.initTimer();
        Recycler.initTimer();
        SuppressorTower.initTimer();
        BuildBlock.init();

        Timer.schedule(() -> {
            if (playing) {
                Groups.player.each(p -> {
                    if (p.team() == Team.blue) {
                        players.get(p.uuid()).setScore(players.get(p.uuid()).getScore() + money_per_min);
                        p.sendMessage(Bundle.format("game.salary", Bundle.findLocale(p.locale), money_per_min));

                    }
                });
            }
        }, 0, 60);

        Timer.schedule(() -> stageTimer--, 0, 1);

        Timer.schedule(() -> Groups.player.each(player -> {
            if (player.tileOn() != null && player.team() == Team.blue && player.unit() != null) {
                if (player.tileOn().block() != null && player.tileOn().block().id >= 98 && player.tileOn().block().id <= 125) {
                    return;
                }
                if (player.tileOn().build != null && player.tileOn().build.team != Team.blue ) {
                    if (player.unit().health <= 1) {
                        player.unit().kill();
                    }
                    player.unit().health -= player.unit().type.health/100;
                    Call.effect(Fx.burning, player.x, player.y, 1, Color.red);

                } else if (!player.tileOn().block().isAir() || player.tileOn().isDarkened()) {
                    if (player.unit().health <= 1) {
                        player.unit().kill();
                    }
                    if (!player.tileOn().block().canBeBuilt()) {
                        player.unit().health -= player.unit().type.health/100;
                        Call.effect(Fx.burning, player.x, player.y, 1, Color.red);
                    }
                }
            } else if (player.tileOn() == null && player.unit() != null) {
                if (player.unit().health <= 1) {
                    player.unit().kill();
                }
                player.unit().health -= player.unit().type.health/100;
                Call.effect(Fx.burning, player.x, player.y, 1, Color.red);
            }
        }), 0, 0.1F);
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

    public static void label(float x, float y, String text, float time, float fontsize) {
        WorldLabel label = WorldLabel.create();
        label.x(x);
        label.y(y + 4);

        label.fontSize = fontsize;
        label.text = text;

        label.add();

        Time.run(time, label::hide);
    }

    public static int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static Player getRandomPlayer(Team team) {
        Seq<Player> playerSeq = new Seq<>();
        Groups.player.each(p -> {
            if (p.team() == team) {
                playerSeq.add(p);
            }
        });
        return playerSeq.random();
    }

    public static Player getRandomPlayer() {
        return Groups.player.index(getRandomInt(0, Groups.player.size() - 1));
    }

    public static void spawnBoss() {
        Unit boss = StageUnits.bosses.get(stage).spawn(Team.crux, redSpawns.random());
        boss.health = boss.type.health + boss.type.health/3;

        if (!boss.dead()) {
            Player player = getRandomPlayer(Team.crux);
            if (player != null) {
                Call.unitControl(player, boss);
                sendBundled("game.boss.spawn", player.name());
            }
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
                return UnitTypes.dagger;
            }
            case 5, 6 -> {
                return UnitTypes.nova;
            }
            case 7, 8 -> {
                return UnitTypes.elude;
            }
            case 9 -> {
                return UnitTypes.flare;
            }
            case 10 -> {
                return UnitTypes.mono;
            }
        }
        return UnitTypes.dagger;
    }

    public static void announceBundled(String key, int duration) {
        Groups.player.forEach(p -> {
            Locale locale = Bundle.findLocale(p.locale);
            String text = Bundle.get(key, locale);
            Call.infoPopup(p.con,  text, duration, 0, 0, 0, -200, 0);
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
