package net.voiddustry.redvsblue.game.starting_menu;

public class StartingItem {
    public String name;
    public String displayNameKey;
    public String category;
    public String desc;
    public boolean isUnit;
    public int cost;

    public StartingItem(String name, String displayNameKey, String category, String desc, boolean isUnit, int cost) {
        this.name = name;
        this.displayNameKey = displayNameKey;
        this.category = category;
        this.desc = desc;
        this.isUnit = isUnit;
        this.cost = cost;
    }
}
