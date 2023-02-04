package net.voiddustry.redvsblue.Admin;

import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;

import java.util.ArrayList;

public class Logs {

    private static final ArrayList<LogEntry> logs = new ArrayList<>();

    public static void addLogEntry(LogEntry entry) {
        logs.add(entry);
    }

    public static LogEntry getLogEntry(int index) {
        return logs.get(index);
    }

    public static ArrayList<LogEntry> getLogs() {
        return logs;
    }

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
