package net.voiddustry.redvsblue.ai;

import arc.struct.IntSeq;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;

public class BluePlayerTarget extends Pathfinder.Flowfield {

    public BluePlayerTarget() {
        this.refreshRate = 800;
    }

    @Override
    public void getPositions(IntSeq out) {
        for (Player p : Groups.player) {
            if (p.team() != Team.blue || p.dead()) {
                continue;
            }

            out.add(Vars.world.packArray(p.tileX(), p.tileY()));
        }
    }
}
