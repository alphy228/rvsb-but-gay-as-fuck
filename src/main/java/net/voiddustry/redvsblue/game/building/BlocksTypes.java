package net.voiddustry.redvsblue.game.building;

import mindustry.content.Blocks;

public class BlocksTypes {

    public static BlocksType
            berylliumWall, berylliumWallLarge, thoriumWall, thoriumWallLarge, doorLarge, drill, laserDrill, powerNode, battery, combustion, rtg, repair, pump;


    public static void load() {
        berylliumWall = new BlocksType("Beryllium Wall", Blocks.berylliumWall, 2, "[#3a8f64]");
        berylliumWallLarge = new BlocksType("Large Beryllium Wall", Blocks.berylliumWallLarge, 8, "[#3a8f64]");

        thoriumWall = new BlocksType("Thoruim Wall", Blocks.thoriumWall, 3, "[#f9a3c7]");
        thoriumWallLarge = new BlocksType("Large Thorium Wall", Blocks.thoriumWallLarge, 12, "[#f9a3c7]");

        doorLarge = new BlocksType("Large Door", Blocks.blastDoor, 16, "[#768a9a]");

        drill = new BlocksType("Pneumatic Drill", Blocks.pneumaticDrill, 3, "[#989AA4]");
        laserDrill = new BlocksType("Laser Drill", Blocks.laserDrill, 25, "[#665C9F]");

        powerNode = new BlocksType("Power Node", Blocks.powerNode, 2, "[#fbd267]");
        battery = new BlocksType("Battery", Blocks.battery, 5, "[#fbd267]");
        combustion = new BlocksType("Combustion Generator", Blocks.combustionGenerator, 8, "[#F1BE6B]");
        rtg = new BlocksType("RTG", Blocks.rtgGenerator, 20, "[#8A73C6]");

        repair = new BlocksType("Mender", Blocks.mender, 8, "[#84F490]");
        pump = new BlocksType("Pump", Blocks.mechanicalPump, 6, "[#8F665B]");

    }
}
