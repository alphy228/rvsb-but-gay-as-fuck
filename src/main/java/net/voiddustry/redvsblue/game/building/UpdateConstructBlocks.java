package net.voiddustry.redvsblue.game.building;

import java.lang.Math;
import java.lang.Runnable;
import arc.util.Timer;
import java.util.HashMap;
import java.util.WeakHashMap;
import arc.*;
import arc.util.*;
import arc.struct.Seq;
import arc.math.geom.QuadTree.*;
import mindustry.*;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.*;
import mindustry.world.Block;
import mindustry.Vars;
import mindustry.game.Rules;
import mindustry.content.*;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.gen.Call;
import mindustry.game.Team;
import mindustry.mod.*;
import mindustry.net.Administration.*;
import mindustry.content.Blocks;

import net.voiddustry.redvsblue.RedVsBluePlugin;
import net.voiddustry.redvsblue.game.building.Buildings;


// updates construct blocks instead of the game doing it, original in usurvplugin
public class UpdateConstructBlocks implements Runnable {
  HashMap<Block, Integer> prices = Buildings.getPrices();
  @Override
  public void run() {
    try {
      
      for (Unit u : Groups.unit) {
        if (u.buildPlan() != null) {
          if (u.buildPlan().worldContext) { 
            if (u.isBuilding()) {
              bp = u.buildPlan()
                
              int cost = prices.get(bp.block);
                           
              if (u.getPlayer() != null && u.buildPlan().breaking == false) {
                Player player = u.getPlayer();
                if (buildspeed > 0f) {
                  Log.info("Attempting to consume " + cost+ " ,from player " + player);
                  Unit builderUnit = buildingUnits.random();
                  if (RedVsBluePlugin.players.get(player.uuid()).getScore()>=cost && (!(cost == null))) {
                    RedVsBluePlugin.players.get(player.uuid()).subtractScore(cost);
                    Log.info("finishing construction");
                    Tile tile = Vars.world.tile(bp.x,bp.y);
                    Call.constructFinish(tile,bp.block,null,(byte)bp.rotation,player.team,bp.config);
                    tile.build.placed();
                  } else {
                    Vars.world.tile((int)b.x,(int)b.y).setNet(Blocks.air,Team.derelict,0);
                  }
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      Log.info("goofy ahh exception in the building system");
      Log.info(e.toString());
    }
  }
}
