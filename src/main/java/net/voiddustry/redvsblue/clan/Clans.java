package net.voiddustry.redvsblue.clan;

import arc.files.Fi;
import arc.util.serialization.Json;
import arc.util.serialization.JsonWriter;
import lombok.Getter;
import mindustry.Vars;

public class Clans {

    @Getter
    private static String clan = "";

    static {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
        Fi configFile = Vars.modDirectory.child("RedVsBlue/Clans/");

    }
}
