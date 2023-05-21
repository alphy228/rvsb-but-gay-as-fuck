package net.voiddustry.redvsblue.game.crux.nest;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.Player;
import mindustry.world.Tile;

public class Nest {

    private static NestData nest;

    public static void createNest(Player player) {
        if (nest == null) {
            if (player.tileOn().block().isAir()) {
                nest = new NestData(Vars.world.tile(player.tileOn().x, player.tileOn().y-1), player);

            }
        }
    }

    public static void checkNest() {
        Tile blockTile = Vars.world.tile(nest.tileOn.x-1, nest.tileOn.y);
        if (blockTile.block() != Blocks.copperWall) {

        }
    }
}
