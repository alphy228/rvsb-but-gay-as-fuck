package net.voiddustry.redvsblue.game.building;

import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.world.Tile;
import net.voiddustry.redvsblue.Bundle;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static net.voiddustry.redvsblue.RedVsBluePlugin.players;
import static net.voiddustry.redvsblue.RedVsBluePlugin.playing;

public class BuildBlock {
    public static Map<BuildTicket, Boolean> buildQueue = new ConcurrentHashMap<>();

    public static void init() {
        Timer.schedule(() -> {
            buildQueue.forEach(((buildTicket, remove) -> {
                if (playing && buildTicket.player() != null) {
                    Player player = buildTicket.player();
                    Tile position;
                    if (player.mouseX >= 0 && player.mouseX <= (Vars.state.map.width - 1) * 8 && player.mouseY >= 0 && player.mouseY <= (Vars.state.map.height - 1) * 8) {
                        position = Vars.world.tile(Math.round(player.mouseX / 8), Math.round(player.mouseY / 8));

                        boolean canBuild;
                        String text = "[gray][ ]";

                        if (position.block().isAir()) {
                            canBuild = true;

                            switch (buildTicket.block().size) {
                                case 1 -> {
                                    drawCursor(player, position, 1, text);
                                }

                                case 2 -> {
                                    int centerX = position.x;
                                    int centerY = position.y;

                                    for (int x = 0; x <= 1; x++) {
                                        for (int y = 0; y <= 1; y++) {
                                            if (Vars.world.tile(centerX + x, centerY + y) != null && Vars.world.tile(centerX + x, centerY + y).block() != Blocks.air) {
                                                canBuild = false;
                                            }
                                        }
                                    }
                                    if (canBuild) {
                                        drawCursor(player, position, 2, text);
                                    }
                                }
                                case 3 -> {
                                    drawCursor(player, position, 3, text);
                                }
                            }

                            if (players.get(player.uuid()).getScore() < buildTicket.cost()) {
                                text = Bundle.get("build.not-enough-money", player.locale);
                                canBuild = false;
                                drawCursor(player, position, buildTicket.block().size, text);
                            }

                            if (player.shooting && canBuild) {
                                build(buildTicket, position);
                                players.get(player.uuid()).subtractScore(buildTicket.cost());
                                removeTicket(buildTicket);
                                if (!buildTicket.single()) {
                                    add(buildTicket);
                                }
                            }
                        }
                    }
                }
            }));
        }, 0, .01F);
    }

    public static void add(BuildTicket buildEntry) {
        buildQueue.put(buildEntry, false);
    }

    public static void build(BuildTicket buildTicket, Tile position) {
        Call.constructFinish(position, buildTicket.block(), null, (byte) 0, Team.blue, null);
    }

    public static void removeTicket(BuildTicket buildTicket) {
        buildQueue.remove(buildTicket);
    }

    public static void clear() {
        buildQueue.clear();
    }

    public static void findAndRemove(Player player) {
        buildQueue.forEach(((buildTicket, remove) -> {
            if (buildTicket.player() == player) {
                removeTicket(buildTicket);
            }
        }));
    }

    public static void drawCursor(Player player, Tile position, Integer size, String text) {
        switch (size) {
            case 1 -> Call.label(player.con, text, 0.01F, (float) ((Math.round(player.mouseX / 8)) * 8), (float) ((Math.round(player.mouseY / 8)) * 8));

            case 2 -> {
                float centerX = position.x * 8;
                float centerY = position.y * 8;

                float centerTextX = (position.x + (float) 0.5);
                float centerTextY = (position.y + (float) 0.5);
                if (Objects.equals(text, Bundle.get("build.not-enough-money", player.locale))) {
                    Call.label(player.con, text, 0.01F, centerTextX * 8, centerTextY * 8);
                }
                for (int x = 0; x <= 1; x++) {
                    for (int y = 0; y <= 1; y++) {
                        Call.label(player.con, "[orange]#", 0.01F, centerX + x * 8, centerY + y * 8);
                    }
                }
            }

            case 3 -> {
                int centerX = position.x * 8;
                int centerY = position.y * 8;

                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        if (x == 0 && y == 0) {
                            Call.label(player.con, text, 0.01F, (float) ((Math.round(player.mouseX / 8)) * 8), (float) ((Math.round(player.mouseY / 8)) * 8));
                        } else {
                            Call.label(player.con, "[orange]#", 0.01F, centerX + x * 8, centerY + y * 8);
                        }
                    }
                }
            }
        }
    }
}
