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
  HashMap<Tile, Integer> remainingCost = new HashMap<>();
  HashMap<Block, Integer> prices = Buildings.getPrices();
  @Override
  public void run() {
    try {
      remainingCost.keySet().removeIf(tile -> tile.build == null || !(tile.build instanceof ConstructBlock.ConstructBuild));
      
      for (Building b : Groups.build) {
        if (b.tile.block() instanceof ConstructBlock cb) {
          ConstructBlock.ConstructBuild cbuild = (ConstructBlock.ConstructBuild)b;
          Tile tile = cbuild.tile;
          float x = cbuild.x;
          float y = cbuild.y;
          Block previous = cbuild.previous;
          Block current = cbuild.current; 
          float buildspeed = 0f;
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
          boolean nothinghappens = false;
          // 0.5 because of buildspeed rule, 12 because 60/5
          float steps = ((cbuild.buildCost / 12f) / buildspeed) / 0.5f;
          float finalprogress = 1f / steps;
          if (buildspeed == 0f) {
              nothinghappens = true;
          }
          if ((buildspeed < 0f ) && (nothinghappens==false)) {
            cbuild.progress=cbuild.progress+finalprogress;
            nothinghappens=true;
          }
          int remainingItemCost = 0;
          if (!nothinghappens) {
            if (remainingCost.containsKey(tile)) {
              remainingItemCost = remainingItemCost+remainingCost.get(tile);
            } else {
            remainingItemCost=0;
            }
            if (!remainingCost.containsKey(tile) || remainingCost.get(tile) == null || cbuild.progress == 0f) {
              remainingCost.put(tile, prices.get(current));
            }
            int totaliItemCost = 0;
            float stepCost = 0;
            
            totaliItemCost = totaliItemCost+prices.get(current);
            stepCost = ((float)(remainingCost.get(tile)))/((float)((1f-cbuild.progress)*steps));

            //consume items and progress if items were succesfully consumed
              int consumeAmount=0;
              int remc = remainingCost.get(tile);
              consumeAmount = (int)Math.ceil(stepCost);
              if (!(remc>consumeAmount)) {
                consumeAmount = remc;
              }
              Log.info("Attempting to consume " + consumeAmount + " ,from " + remainingCost.get(tile) + " remaining" + "progress: " + cbuild.progress);
              Unit builderUnit = buildingUnits.random();
              if (totaliItemCost != 0 || consumeAmount==0) { 
                if (RedVsBluePlugin.players.get(player.uuid()).getScore()>=consumeAmount || consumeAmount==0) {
                  RedVsBluePlugin.players.get(player.uuid()).subtractScore(consumeAmount);
                  remc = remc-consumeAmount;
                  remainingCost.put(tile,remc);
                  int remainingItemCost2=0;
                  remainingItemCost2 = remainingItemCost2+remc;
                  if (remainingItemCost2==0 || cbuild.progress >= 0.999999f) {
                    Log.info("finishing construction");
                    cbuild.progress=1.0001f;
                    Call.constructFinish(tile,current,null,(byte)builderUnit.buildPlan().rotation,b.team,builderUnit.buildPlan().block.instantBuild ? builderUnit.buildPlan().config : null);
                    tile.build.placed();
                  } else {
                    cbuild.progress=1.0001f-(((float)remainingItemCost2)/((float)totaliItemCost));
                    Log.info("updating progress");
                  }
                }
              }
            }
          }
        }
    } catch (Exception e) {
      Log.info("goofy ahh exception in the building system,you will see this a lot,nothing should break though");
      Log.info(e.toString());
    }
  }
}
