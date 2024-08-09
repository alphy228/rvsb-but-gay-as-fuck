package net.voiddustry.redvsblue.game.crux;

import arc.graphics.Color;
import arc.util.Timer;
import mindustry.content.Fx;
import mindustry.gen.Call;
import net.voiddustry.redvsblue.RedVsBluePlugin;
import net.voiddustry.redvsblue.util.Utils;

public class SpawnEffect {
    public static void initEffect() {
        Timer.schedule(() -> {
            if (!RedVsBluePlugin.redSpawns.isEmpty()) {
                RedVsBluePlugin.redSpawns.forEach(spawn -> {
                    int r = Utils.getRandomInt(-1,2);
                    for (int i = r; i < 19+r; i++) {
                        Call.effect(Fx.fire, (float) (spawn.x*8 + Math.sin(i) * 80), (float) (spawn.y*8 + Math.cos(i) * 80), 0, Color.red);
                    }
                });
            }
        }, 0, 0.5F);
    }
}
