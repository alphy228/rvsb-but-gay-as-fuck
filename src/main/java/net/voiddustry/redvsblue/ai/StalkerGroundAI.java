package net.voiddustry.redvsblue.ai;

import arc.math.Mathf;
import arc.util.Log;
import mindustry.entities.units.AIController;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;

import static net.voiddustry.redvsblue.RedVsBluePlugin.bluePlayerTargeting;

public class StalkerGroundAI extends AIController {
    @Override
    public void updateMovement() {
        Player closest = null;
        float closestDst = Float.MAX_VALUE;
        for (Player p : Groups.player) {
            if (p.team() != Team.blue || p.dead()) {
                continue;
            }

            float dst = unit.dst2(p);
            if (closestDst > dst) {
                closest = p;
                closestDst = dst;
            }
        }

        if (closest == null) {
            return;
        }

        target = closest.unit();

        if (unit.within(closest, unit.type.range * 0.125f)) {
            for (var mount : unit.mounts) {
                if (mount.weapon.controllable && mount.weapon.bullet.collidesGround) {
                    mount.target = closest.unit();
                }
            }
        } else {
            Log.debug("PATHFIND TRIGGERES");
            pathfind(bluePlayerTargeting);
            if (unit.type.canBoost && unit.elevation > 0.001f && !unit.onSolid()) {
                unit.elevation = Mathf.approachDelta(unit.elevation, 0f, unit.type.riseSpeed);
            }
        }

        faceTarget();
    }
}
