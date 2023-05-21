package net.voiddustry.redvsblue.logic;

import arc.struct.Seq;
import arc.util.Timer;
import mindustry.game.Team;
import mindustry.gen.*;
import net.voiddustry.redvsblue.Bundle;

import java.util.HashMap;
import java.util.Map;

public class AnthicusRadar {

    public static void init() {
        Timer.schedule(() -> anthicusRadar.forEach((player, status) -> {
            if (player == null) {
                anthicusRadar.remove(null, status);
            } else {
                Seq<Unit> cruxFlyingUnits = new Seq<>();

                Groups.unit.each(u -> {
                    if(u.isFlying() && u.team == Team.crux) cruxFlyingUnits.add(u);
                });

                StringBuilder units = new StringBuilder();

                cruxFlyingUnits.forEach(unit -> units.append(units).append("[lime]").append(unit.type).append("[gray] - [scarlet]").append(Math.floor(player.dst(unit))).append("\n"));

                units.append("\n").append(Bundle.get("game.radar", player.locale));

                Call.infoPopup(player.con, units.toString(), 0.1F, 0, player.con.mobile? 700 : 0, player.con.mobile? -300 : -600, 0, 0);
            }
        }), 0, 0.1F);
    }

    public static void toggleRadar(Player player) {
        if (anthicusRadar.containsKey(player)) {
            if (anthicusRadar.get(player)) {
                anthicusRadar.put(player, false);
            } else {
                anthicusRadar.put(player, true);
            }
        } else {
            anthicusRadar.put(player, true);
        }
    }

    private static final Map<Player, Boolean> anthicusRadar = new HashMap<>();

}
