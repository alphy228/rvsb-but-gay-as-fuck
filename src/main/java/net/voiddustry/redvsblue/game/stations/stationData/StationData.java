package net.voiddustry.redvsblue.game.stations.stationData;

import mindustry.gen.Player;
import mindustry.world.Tile;

public record StationData(Player owner, Tile tileOn) {}