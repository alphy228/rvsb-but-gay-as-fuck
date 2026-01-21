package net.voiddustry.redvsblue.game.building;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.world.Block;

public class Buildings {

    public static class BuildingPrice {
        public final Block block;
        public final int price;

        public BuildingPrice(Block block, int price) {
            this.block = block;
            this.price = price;
        }
    }

    public static Seq<BuildingPrice> create() {
        Seq<BuildingPrice> buildings = new Seq<>();

        buildings.add(new BuildingPrice(Blocks.conveyor, 5));
        buildings.add(new BuildingPrice(Blocks.router, 12));
        buildings.add(new BuildingPrice(Blocks.junction, 20));
        buildings.add(new BuildingPrice(Blocks.mechanicalDrill, 50));
        buildings.add(new BuildingPrice(Blocks.graphitePress, 120));
        buildings.add(new BuildingPrice(Blocks.duo, 75));
        buildings.add(new BuildingPrice(Blocks.powerNode, 30));
        buildings.add(new BuildingPrice(Blocks.battery, 60));

        return buildings;
    }
}
