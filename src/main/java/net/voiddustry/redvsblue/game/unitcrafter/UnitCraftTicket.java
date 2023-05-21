package net.voiddustry.redvsblue.game.unitcrafter;

import mindustry.gen.Player;
import mindustry.gen.Unit;

public record UnitCraftTicket(Player player, Unit unit, int craftTime) {}
