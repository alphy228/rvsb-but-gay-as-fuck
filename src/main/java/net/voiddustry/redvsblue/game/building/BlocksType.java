package net.voiddustry.redvsblue.game.building;

import mindustry.world.Block;

public class BlocksType {
    public String name;
    public Block block;
    public int cost;
    public String color;

    public BlocksType(String name, Block block, int cost, String color) {
        this.name = name;
        this.block = block;
        this.cost = cost;
        this.color = color;
    }
}
