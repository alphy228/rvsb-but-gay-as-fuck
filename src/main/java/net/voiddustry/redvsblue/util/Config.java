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
    @Getter
    private static String discordBansUrl = "";
    @Getter
    private static String discordReportsUrl = "";
    @Getter
    private static String discordReportsRoleID = "";

    static {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
        Fi configFile = Vars.modDirectory.child("RedVsBlue/RedVsBlue.json");
        if (configFile.exists()) {
            JsonValue jsonValue = json.fromJson(null, configFile);
            Config.discordUrl = json.readValue("discordUrl", String.class, jsonValue);
            Config.discordBansUrl = json.readValue("discordBansUrl", String.class, jsonValue);
            Config.discordReportsUrl = json.readValue("discordReportsUrl", String.class, jsonValue);
            Config.discordReportsRoleID = json.readValue("discordReportsRoleID", String.class, jsonValue);
        }
    }

}
