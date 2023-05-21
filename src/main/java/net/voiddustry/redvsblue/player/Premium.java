package net.voiddustry.redvsblue.player;


import arc.files.Fi;
import arc.util.Log;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import arc.util.serialization.JsonWriter;
import mindustry.Vars;
import mindustry.gen.Player;

import java.util.HashMap;
import java.util.Map;

public class Premium {
    private static final Map<String, Boolean> premiumPlayers = new HashMap<>();

    public static void init() {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        Fi configFile = Vars.modDirectory.child("RedVsBlue/premiums.json");
        JsonValue jsonValue = json.fromJson(null, configFile);
        String readValue = json.readValue("players", String.class, jsonValue);
        String[] uuids = readValue.split(",");
        Log.info("===== (Premium) =====");
        Log.info("Premium player's uuid's loaded:");
        Log.info("");
        for (String uuid : uuids) {
            Log.info(uuid);
            premiumPlayers.put(uuid, true);
        }
    }

    public static boolean isPremium(Player player){
        return premiumPlayers.containsKey(player.uuid());
    }
}
