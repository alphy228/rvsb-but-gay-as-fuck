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
      
      for (Building b : Groups.build) {
        if (b.tile.block() instanceof ConstructBlock cb) {
          ConstructBlock.ConstructBuild cbuild = (ConstructBlock.ConstructBuild)b;
          Tile tile = cbuild.tile;
          float x = cbuild.x;
          float y = cbuild.y;
          Block previous = cbuild.previous;
          Block current = cbuild.current; 
          float buildspeed = 0f;
          int cost = prices.get(current);
          //calculate build speed
          Seq<Unit> buildingUnits = new Seq<>();
          buildingUnits.clear();

          Player player = null;
          
          for (Unit u : Groups.unit) {
              if (u.buildPlan() != null) {
                  if (u.buildPlan().worldContext) { 
                      if (u.buildPlan().build() == b && u.isBuilding()) {
                        
                          if (u.getPlayer() != null) {
                            player = u.getPlayer();
                          }
                        
                          buildingUnits.add(u);
                          if (u.buildPlan().breaking) {
                              buildspeed = buildspeed - u.type.buildSpeed;
                          } else {
                              buildspeed = buildspeed + u.type.buildSpeed;
                          }
                      }
                  }
              }
          }

          if (buildspeed > 0f) {
            Log.info("Attempting to consume " + cost+ " ,from player " + player);
            Unit builderUnit = buildingUnits.random();
            if (RedVsBluePlugin.players.get(player.uuid()).getScore()>=cost) {
              RedVsBluePlugin.players.get(player.uuid()).subtractScore(cost);
              int remainingItemCost2=0;
              Log.info("finishing construction");
              cbuild.progress=1.0001f;
              Call.constructFinish(tile,current,null,(byte)builderUnit.buildPlan().rotation,b.team,builderUnit.buildPlan().block.instantBuild ? builderUnit.buildPlan().config : null);
              tile.build.placed();
            } else {
              Vars.world.tile((int)b.x,(int)b.y).setNet(Blocks.air,Team.derelict,0);
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
