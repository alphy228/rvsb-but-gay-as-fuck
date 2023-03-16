package net.voiddustry.redvsblue.util;

import arc.files.Fi;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import arc.util.serialization.JsonWriter;
import lombok.Getter;
import mindustry.Vars;

public class UnitsConfig {

    @Getter
    private static int prices_multipler = 1;

    static {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
        Fi configFile = Vars.modDirectory.child("RedVsBlue/Units.json");
        if (configFile.exists()) {
            JsonValue jsonValue = json.fromJson(null, configFile);
            UnitsConfig.prices_multipler= json.readValue("prices_multipler", Integer.class, jsonValue);
        }
    }

    public static int multp = getPrices_multipler();

}
