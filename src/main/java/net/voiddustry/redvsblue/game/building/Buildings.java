package net.voiddustry.redvsblue.game.building;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.world.Block;

import java.util.HashMap;

// PATCH NEEDS TO BE EDITED TOO

public class Buildings {

    public static HashMap<Block, Integer> getPrices() {
        HashMap<Block, Integer> prices = new HashMap<>();

        prices.put(Blocks.bridgeConveyor, 3);
        prices.put(Blocks.bridgeConduit, 3);
        prices.put(Blocks.unloader, 9);
        prices.put(Blocks.powerNodeLarge, 3);
        prices.put(Blocks.pneumaticDrill, 9);
        prices.put(Blocks.container, 3);
        prices.put(Blocks.berylliumWall, 2);
        prices.put(Blocks.beryllumWallLarge, 6);
        prices.put(Blocks.thoriumWall, 4);
        prices.put(Blocks.thoriumWallLarge, 12);
        prices.put(Blocks.blastDoor, 30);
        prices.put(Blocks.combustionGenerator, 9);
        prices.put(Blocks.rtgGenerator, 30);
        prices.put(Blocks.laserDrill, 25);
        prices.put(Blocks.powerNode, 3);
        prices.put(Blocks.battery, 5);

        return prices;
    }
}
