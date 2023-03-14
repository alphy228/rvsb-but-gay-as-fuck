package net.voiddustry.redvsblue.util;

import arc.files.Fi;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import arc.util.serialization.JsonWriter;
import lombok.Getter;
import mindustry.Vars;

public class Config {
    @Getter
    private static String discordUrl = "";

    static {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
        Fi configFile = Vars.modDirectory.child("RedVsBlue.json");
        if (configFile.exists()) {
            JsonValue jsonValue = json.fromJson(null, configFile);
            Config.discordUrl = json.readValue("discordUrl", String.class, jsonValue);
        }
    }
}
