package net.voiddustry.redvsblue.game.stations;

import arc.util.Time;
import arc.util.Timer;
import mindustry.gen.Call;
import mindustry.gen.WorldLabel;
import mindustry.net.NetConnection;
import mindustry.world.Tile;

public class StationUtils {
    public static void drawStationName(Tile tile, String text, float time) {
        WorldLabel label = WorldLabel.create();
        label.x(tile.x * 8);
        label.y(tile.y * 8 + 4);

        label.fontSize = 0.8F;
        label.text = text;

        label.add();

        Timer.schedule(label::hide, time);
    }
}
