package net.voiddustry.redvsblue.util;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.ui.Menus;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import static net.voiddustry.redvsblue.RedVsBluePlugin.selectedBuildBlock;
import net.voiddustry.redvsblue.Bundle;
import net.voiddustry.redvsblue.RedVsBluePlugin;

public class Utils {

    public void initRules() {
        Vars.state.rules.hideBannedBlocks = true;
        Vars.state.rules.bannedBlocks.addAll();
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

    private static char randomChar() {
        Random r = new Random();
        return (char) (r.nextInt(26) + 'A');
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

}
