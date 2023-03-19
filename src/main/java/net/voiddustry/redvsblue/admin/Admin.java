package net.voiddustry.redvsblue.admin;

import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;

public class Admin {
    public static void openAdminPanelMenu(Player player) {
        int menu = Menus.registerMenu((playerInMenu, option) -> {
            switch (option) {

            }
        });
//        Call.menu(player.con, menu, );
    }
}
