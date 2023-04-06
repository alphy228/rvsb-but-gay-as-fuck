package net.voiddustry.redvsblue.game.building;

import mindustry.content.Blocks;
import mindustry.content.Items;

public class BlocksTypes {

    public static BlocksType
            berylliumWall, berylliumWallLarge, tungstenWall, tungstenWallLarge, doorLarge, drill, mine;


    public static void load() {
        berylliumWall = new BlocksType("Beryllium Wall", Blocks.berylliumWall, 2, "[#3a8f64]");
        berylliumWallLarge = new BlocksType("Large Beryllium Wall", Blocks.berylliumWallLarge, 8, "[#3a8f64]");

        tungstenWall = new BlocksType("Tungsten Wall", Blocks.tungstenWall, 3, "[#768a9a]");
        tungstenWallLarge = new BlocksType("Large Tungsten Wall", Blocks.tungstenWallLarge, 12, "[#768a9a]");

        doorLarge = new BlocksType("Large Door", Blocks.blastDoor, 13, "[#768a9a]");
        drill = new BlocksType("Drill", Blocks.mechanicalDrill, 4, "[#d99d73]");

        mine = new BlocksType("Mine", Blocks.shockMine, 3, "[#53565c]");
    }
}
