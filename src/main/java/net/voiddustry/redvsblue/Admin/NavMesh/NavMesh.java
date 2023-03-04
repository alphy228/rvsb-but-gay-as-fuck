package net.voiddustry.redvsblue.Admin.NavMesh;

import mindustry.gen.Player;

import java.util.HashMap;
import java.util.Map;

public class NavMesh {
    private static Map<Player, Boolean> playerInEditModeMap = new HashMap<>();

    private Map<Integer, NavMeshPoint> navMeshPointsMap = new HashMap<>();
    private static Map<Integer, NavMeshLink> navMeshLinksMap = new HashMap<>();

    public static void init() {

    }

    public static void editMode(Player player, Boolean option) {
        if (option) {
            playerInEditModeMap.put(player, true);
            player.sendMessage("[lime]Navmesh Editor Enabled");
        } else {
            playerInEditModeMap.put(player, false);
            player.sendMessage("[scarlet]Navmesh Editor Disabled");
        }
    }

}
