package net.voiddustry.redvsblue.game.stations;

import arc.Events;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Player;

import java.util.HashMap;
import java.util.Map;

/** Handles configurable station buttons defined by the server content assets. */
public final class StationButtons {
    private static final long menuCooldownMillis = 1_000L;
    private static final Map<String, Long> menuCooldowns = new HashMap<>();

    private StationButtons() {
    }

    public static void init() {
        Events.on(EventType.ConfigEvent.class, event -> {
            if (!(event.value instanceof Boolean enabled) || !enabled || event.player == null || !isStationButton(event.tile)) {
                return;
            }

            // The switch must always return to its off state, even for an invalid/out-of-range press.
            Timer.schedule(() -> {
                if (event.tile.isValid() && isStationButton(event.tile)) {
                    event.tile.configure(false);
                }
            }, 1f);

            Player player = event.player;
            if (player.unit() == null || player.dst(event.tile) > Vars.buildingRange) return;

            long now = System.currentTimeMillis();
            Long lastOpen = menuCooldowns.get(player.uuid());
            if (lastOpen != null && now - lastOpen < menuCooldownMillis) return;
            menuCooldowns.put(player.uuid(), now);

            if (event.tile.block.name.endsWith("laboratory-station")) {
                Laboratory.openStationMenu(player);
            } else {
                ArmorWorkbench.openStationMenu(player);
            }
        });
    }

    private static boolean isStationButton(Building building) {
        String name = building.block.name;
        return name.endsWith("laboratory-station") || name.endsWith("workbench-station");
    }
}
