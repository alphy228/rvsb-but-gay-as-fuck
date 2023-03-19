package net.voiddustry.redvsblue.game;

import arc.graphics.Color;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Player;

import mindustry.world.Tile;
import net.voiddustry.redvsblue.PlayerData;

import javax.swing.undo.CannotUndoException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.voiddustry.redvsblue.RedVsBluePlugin.players;

public class Miner {

    private static final Map<String, MinerData> minersMap = new ConcurrentHashMap<>();

    public static void initTimer() {
        Timer.schedule(() -> minersMap.forEach((owner, minerData) -> {
            PlayerData data = players.get(minerData.getOwner().uuid());
            data.addScore(500);
            Call.label("[lime]+500", 5, minerData.getTileOn().centerX()*8, minerData.getTileOn().centerY()*8);
        }), 0, 30);
        Timer.schedule(Miner::renderMiners, 0, 1);
    }

    public static void buyMiner(Player player) {
        if (!minersMap.containsKey(player.uuid())) {
            if (!player.dead()) {
                Tile playerTileOn = player.tileOn();
                Tile tileUnderPlayer = Vars.world.tile(playerTileOn.x, playerTileOn.y - 1);

                if (!player.dead() && player.team() == Team.blue && tileUnderPlayer.block().isAir()) {
                    MinerData minerData = new MinerData(player, tileUnderPlayer);
                    minersMap.put(player.uuid(), minerData);
                    Call.constructFinish(tileUnderPlayer, Blocks.combustionGenerator, null, (byte) 0, Team.blue, null);
                    Call.effect(Fx.explosion, tileUnderPlayer.x*8, tileUnderPlayer.y*8, 5, Color.red);
                }
            }
        }
    }

    public static void renderMiners() {
        minersMap.forEach((owner, miner) -> {
            if (miner != null) {
                if (miner.getTileOn().block() == Blocks.air) {
                    minersMap.remove(owner, miner);
                } else {
                    Call.label(miner.getOwner().name + "[gold]'s Miner", 1, miner.getTileOn().centerX()*8, (miner.getTileOn().centerY()+1)*8);
                }
            }
        });
    }

    public static void clearMiners() {
        minersMap.clear();
    }
}
