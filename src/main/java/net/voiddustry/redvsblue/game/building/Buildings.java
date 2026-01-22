package net.voiddustry.redvsblue.game.building;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.world.Block;

import java.util.HashMap;

// PATCH NEEDS TO BE EDITED TOO

public class Buildings {

    public static HashMap<Block, Integer> getPrices() {
        HashMap<Block, Integer> prices = new HashMap<>();

        prices.put(Blocks.conveyor, 1);
        prices.put(Blocks.router, 2);
        prices.put(Blocks.junction, 2);
        prices.put(Blocks.mechanicalDrill, 3);
        prices.put(Blocks.graphitePress, 5);
        prices.put(Blocks.duo, 5);
        prices.put(Blocks.powerNode, 3);
        prices.put(Blocks.battery, 6);

        return prices;
    }
}
