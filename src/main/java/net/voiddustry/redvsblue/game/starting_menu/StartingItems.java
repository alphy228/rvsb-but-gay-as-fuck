package net.voiddustry.redvsblue.game.starting_menu;

public class StartingItems {

    public static StartingItems
            nova, flare, merui, stell, buildpoints_1, buildpoints_4, buildpoints_12;

    public static void load() {
        StartingItem nova = new StartingItem("nova", "", "units", "Unit Nova, heals allies.", true, 10);
        StartingItem flare = new StartingItem("flare", "", "units", "Unit Flare, flying, nice for killing meruis.", true, 15);
        StartingItem merui = new StartingItem("merui", "", "units", "Unit Merui, Artillery, many hp.", true, 10);
        StartingItem stell = new StartingItem("stell", "", "units", "Unit Stell, many HP, many DMG, nice unit", true, 18);
        // buildpoints_1, buildpoints_4, buildpoints_12
        StartingItem buildpoints_1 = new StartingItem("buildpoints_1", "", "building", "Just one build point.", false, 2);
        StartingItem buildpoints_4 = new StartingItem("buildpoints_4", "", "building", "Four building points, you can build a wall!", false, 6);
        StartingItem buildpoints_12 = new StartingItem("buildpoints_16", "", "building", "Bundle of build points!", false, 20);

    }
}
