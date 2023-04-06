package net.voiddustry.redvsblue.game.building;

import mindustry.gen.Player;
import mindustry.world.Block;

public record BuildTicket(Player player, Block block, Boolean single, Boolean station, int cost) {}
