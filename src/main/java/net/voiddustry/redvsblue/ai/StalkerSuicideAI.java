package net.voiddustry.redvsblue.ai;

import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BlockGroup;
import net.voiddustry.redvsblue.RedVsBluePlugin;

public class StalkerSuicideAI extends AIController {
    static boolean blockedByBlock;

    @Override
    public void updateUnit() {
        if (Units.invalidateTarget(target, unit.team, unit.x, unit.y, Float.MAX_VALUE)) {
            target = null;
        }

        if (retarget()) {
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

            if (closest != null) {
                target = closest.unit();
            } else {
                target = target(unit.x, unit.y, unit.range(), unit.type.targetAir, unit.type.targetGround);
            }
        }

        boolean rotate = false, shoot = false, moveToTarget = false;

        if (!Units.invalidateTarget(target, unit, unit.range()) && unit.hasWeapons()) {
            rotate = true;
            shoot = unit.within(target, unit.type.weapons.first().bullet.range +
                    (target instanceof Building b ? b.block.size * Vars.tilesize / 2f : ((Hitboxc) target).hitSize() / 2f));

            //do not move toward walls or transport blocks
            if (!(target instanceof Building build && !(build.block instanceof CoreBlock) && (
                    build.block.group == BlockGroup.walls ||
                            build.block.group == BlockGroup.liquids ||
                            build.block.group == BlockGroup.transportation
            ))) {
                blockedByBlock = false;

                //raycast for target
                boolean blocked = World.raycast(unit.tileX(), unit.tileY(), target.tileX(), target.tileY(), (x, y) -> {
                    for (Point2 p : Geometry.d4c) {
                        Tile tile = Vars.world.tile(x + p.x, y + p.y);
                        if (tile != null && tile.build == target) return false;
                        if (tile != null && tile.build != null && tile.build.team != unit.team()) {
                            blockedByBlock = true;
                            return true;
                        } else {
                            return tile == null || tile.solid();
                        }
                    }
                    return false;
                });

                //shoot when there's an enemy block in the way
                if (blockedByBlock) {
                    shoot = true;
                }

                if (!blocked) {
                    moveToTarget = true;
                    //move towards target directly
                    unit.movePref(vec.set(target).sub(unit).limit(unit.speed()));
                }
            }
        }

        if (!moveToTarget) {
            pathfind(RedVsBluePlugin.bluePlayerTargeting);
        }

        unit.controlWeapons(rotate, shoot);

        faceTarget();
    }

    @Override
    public Teamc target(float x, float y, float range, boolean air, boolean ground) {
        return Units.closestTarget(unit.team, x, y, range, u -> u.checkTarget(air, ground), t -> ground &&
                !(t.block instanceof Conveyor || t.block instanceof Conduit)); //do not target conveyors/conduits
    }
}
