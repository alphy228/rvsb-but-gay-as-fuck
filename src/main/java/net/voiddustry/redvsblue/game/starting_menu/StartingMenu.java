package net.voiddustry.redvsblue.game.starting_menu;

import arc.struct.ObjectMap;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.net.NetConnection;
import mindustry.ui.Menus;
import net.voiddustry.redvsblue.Bundle;

public class StartingMenu {

    private final ObjectMap<Integer ,StartingItem> selectedItemsList = new ObjectMap<>();

    public static boolean canOpenMenu;

    // settings

    public static void openMenu(Player player, int category) {
        NetConnection connection = player.con;
        if (canOpenMenu) {
            int menu = Menus.registerMenu((player1, option) -> {
                if (option <= 3) {
                    switch (option) {
                        case 0 -> openMenu(player, 0);
                        case 1 -> openMenu(player, 1);
                        case 2 -> openMenu(player, 2);
                        case 3 -> openMenu(player, 4);
                    }
                }
            });

            String[] categories = {"[gray]C", "[lime]Unit", "[royal]Build", "[red]Item"};
            String[] util = {"[cyan]Finish", "[lightgray]Close"};

            switch (category) {
                case 1 -> {

                    String[][] buttons = {
                            categories,
                            {},
                            {},
                            {},
                            {},
                            util
                    };
                    //menu(connection, menu, text, buttons);
                }
            }
        }

    }

    private static void menu(NetConnection connection, int menu, String text, String[][] buttons) {
        Call.menu(connection, menu, Bundle.get("menu.build.title", connection.player.locale), text, buttons);
    }
}
