package net.voiddustry.redvsblue.Admin;

import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;

import java.util.ArrayList;
import java.util.List;

public class Logs {

    public static ArrayList<Integer> logs = new ArrayList<>();
    // Logs
    public static void openLogs(Player player) {
        int menu = Menus.registerMenu((playerInMenu, option) -> {
            switch (option) {
                case 1 -> {

                }
            }
        });

        String[][] buttonsRow  = {
                {
                        "[scarlet]Kills",
                        "[lime]Purchases"
                }
        };

        Call.menu(player.con, menu, "[cyan]Logs", "", buttonsRow);
    }
}
