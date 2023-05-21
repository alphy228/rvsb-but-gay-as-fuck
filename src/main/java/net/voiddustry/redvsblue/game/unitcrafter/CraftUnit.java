package net.voiddustry.redvsblue.game.unitcrafter;

import java.util.HashMap;
import java.util.Map;

public class CraftUnit {
    private static final Map<String, UnitCraftTicket> unitCraftMap = new HashMap<>();

    public static void newCraft(UnitCraftTicket craftTicket) {
        if (!unitCraftMap.containsKey(craftTicket.player().uuid())) {
            unitCraftMap.put(craftTicket.player().uuid(), craftTicket);
        }
    }
}
