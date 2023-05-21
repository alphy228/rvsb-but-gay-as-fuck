package net.voiddustry.redvsblue.game.stations;

import mindustry.gen.Call;
import mindustry.net.NetConnection;
import mindustry.world.Tile;

public class StationUtils {
    public static void drawStationName(NetConnection con, Tile tile, String text, float time) {
        Call.labelReliable(con, text, time, tile.x*8, tile.y*8);
    }
}
