package net.voiddustry.redvsblue.game.stations;

import arc.graphics.Color;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Player;

import mindustry.world.Tile;
import net.voiddustry.redvsblue.Bundle;
import net.voiddustry.redvsblue.PlayerData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.voiddustry.redvsblue.RedVsBluePlugin.players;

public class Miner {

    private static final Map<String, MinerData> minersMap = new ConcurrentHashMap<>();

    public static void initTimer() {
        Timer.schedule(() -> minersMap.forEach((owner, minerData) -> {
            PlayerData data = players.get(minerData.getOwner().uuid());
            data.addScore(minerData.getLvl());
            minerData.addExp(1);
            Call.label("[lime]+" + minerData.getLvl(), 5, minerData.getTileOn().centerX()*8, minerData.getTileOn().centerY()*8);
            if (minerData.getExp() >= minerData.getMaxExp()) {
                minerData.addLvl();
                int expLimit = minerData.getMaxExp();
                int expLimitToSet = expLimit + expLimit/2;
                minerData.setMaxExp(expLimitToSet);
                minerData.setExp(0);
            }
        }), 0, 30);
        Timer.schedule(Miner::renderMiners, 0, 1);
    }

    public static void buyMiner(Player player) {
        if (players.get(player.uuid()).getScore() < 20) {
            player.sendMessage(Bundle.format("station.not-enough-money", 20));
        } else {
            if (!minersMap.containsKey(player.uuid())) {
                if (!player.dead()) {
                    Tile playerTileOn = player.tileOn();
                    Tile tileUnderPlayer = Vars.world.tile(playerTileOn.x, playerTileOn.y - 1);

                    if (!player.dead() && player.team() == Team.blue && tileUnderPlayer.block().isAir()) {
                        MinerData minerData = new MinerData(player, tileUnderPlayer);
                        minersMap.put(player.uuid(), minerData);
                        Call.constructFinish(tileUnderPlayer, Blocks.combustionGenerator, null, (byte) 0, Team.blue, null);
                        Call.effect(Fx.explosion, tileUnderPlayer.x*8, tileUnderPlayer.y*8, 5, Color.red);
                        players.get(player.uuid()).subtractScore(20);
                    }
                }
            }
        }
    }

    public static void renderMiners() {
        minersMap.forEach((owner, miner) -> {
            if (miner != null) {
                if (miner.getTileOn().block() == Blocks.air || miner.getOwner().team() != Team.blue) {
                    minersMap.remove(owner, miner);
                    if (miner.getTileOn().block() == Blocks.combustionGenerator) {
                        miner.getTileOn().build.kill();
                    }
                } else {
                    String text = miner.getOwner().name + "[gold]'s Miner\n[gray][ [gold]" + miner.getLvl() + "[] | [accent]" + miner.getExp() + " / " + miner.getMaxExp() + "[gray] ]";
                    Call.label(text, 1, miner.getTileOn().centerX()*8, (miner.getTileOn().centerY()+1)*8);
                }
            }
        });
    }

    public static void clearMiners() {
        minersMap.clear();
    }
}
