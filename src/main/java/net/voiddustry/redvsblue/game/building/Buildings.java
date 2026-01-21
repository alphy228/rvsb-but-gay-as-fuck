package net.voiddustry.redvsblue.game.building;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.world.Block;

import java.util.HashMap;

public class Buildings {

    public static HashMap<Block, Integer> getPrices() {
        HashMap<Block, Integer> prices = new HashMap<>();

        prices.put(Blocks.conveyor, 5);
        prices.put(Blocks.router, 12);
        prices.put(Blocks.junction, 20);
        prices.put(Blocks.mechanicalDrill, 50);
        prices.put(Blocks.graphitePress, 120);
        prices.put(Blocks.duo, 75);
        prices.put(Blocks.powerNode, 30);
        prices.put(Blocks.battery, 60);

        return prices;
    }
}
