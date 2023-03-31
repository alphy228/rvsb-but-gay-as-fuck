package net.voiddustry.redvsblue.game;

import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;
import net.voiddustry.redvsblue.Bundle;
import net.voiddustry.redvsblue.game.stations.*;

public class StationsMenu {
    public static void openMenu(Player player) {
        if (!player.dead() && player.team() == Team.blue) {
            int menu = Menus.registerMenu((player1, option) -> {
                switch (option) {
                    case 0 -> Miner.buyMiner(player);
                    case 1 -> RepairPoint.buyRepairPoint(player);
                    case 2 -> AmmoBox.buyAmmoBox(player);
                    case 3 -> Laboratory.buyLab(player);
                    case 4 -> Turret.buyTurret(player);
                    case 5 -> Turret.buyClip(player);
                }
            });

            String title = Bundle.get("stations.title", player.locale);
            String text = Bundle.get("stations.text", player.locale);

            String[][] buttons = {
                    { Bundle.get("stations.buttons.miner", player.locale) },
                    { Bundle.get("stations.buttons.repair-point", player.locale) },
                    { Bundle.get("stations.buttons.ammo-box", player.locale)},
                    { Bundle.get("stations.buttons.lab", player.locale)},
                    { Bundle.get("stations.buttons.turret", player.locale)},
                    { Bundle.get("stations.buttons.turret-ammo", player.locale)},
                    { Bundle.get("stations.buttons.close", player.locale)}
            };

            Call.menu(player.con, menu, title, text, buttons);
        }
    }
}
