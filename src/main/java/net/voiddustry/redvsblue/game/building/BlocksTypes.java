package net.voiddustry.redvsblue.game.building;

import mindustry.content.Blocks;

public class BlocksTypes {

    public static BlocksType
            berylliumWall, berylliumWallLarge, thoriumWall, thoriumWallLarge, doorLarge, drill, mine;


    public static void load() {
        berylliumWall = new BlocksType("Beryllium Wall", Blocks.berylliumWall, 2, "[#3a8f64]");
        berylliumWallLarge = new BlocksType("Large Beryllium Wall", Blocks.berylliumWallLarge, 8, "[#3a8f64]");

        thoriumWall = new BlocksType("Thoruim Wall", Blocks.thoriumWall, 3, "[#f9a3c7]");
        thoriumWallLarge = new BlocksType("Large Thorium Wall", Blocks.thoriumWallLarge, 12, "[#f9a3c7]");

        doorLarge = new BlocksType("Large Door", Blocks.blastDoor, 13, "[#768a9a]");
        drill = new BlocksType("Drill", Blocks.mechanicalDrill, 4, "[#d99d73]");

        mine = new BlocksType("Mine", Blocks.shockMine, 3, "[#53565c]");
    }
}
