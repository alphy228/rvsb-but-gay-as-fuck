package net.voiddustry.redvsblue.game.starting_menu;

public class StartingItems {

    public static StartingItem
            nova, flare, merui, stell;

    public static void load() {
        StartingItem nova = new StartingItem("nova", "", "units", "Unit Nova, heals allies.", true, 10);
        StartingItem flare = new StartingItem("flare", "", "units", "Unit Flare, flying, nice for killing meruis.", true, 15);
        StartingItem merui = new StartingItem("merui", "", "units", "Unit Merui, Artillery, many hp.", true, 10);
        StartingItem stell = new StartingItem("stell", "", "units", "Unit Stell, many HP, many DMG, nice unit", true, 18);

    }

    public static StartingItem getStartingItem(String string) {
        switch (string) {
            case "nova" -> {
                return nova;
            }
            case "flare" -> {
                return flare;
            }
            case "merui" -> {
                return merui;
            }
            case "stell" -> {
                return stell;
            }
        }
        return null;
    }
}
