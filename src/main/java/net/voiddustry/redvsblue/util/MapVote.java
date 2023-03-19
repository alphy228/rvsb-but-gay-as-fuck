package net.voiddustry.redvsblue.util;

import arc.struct.Seq;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.maps.MapException;
import mindustry.net.WorldReloader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

public class MapVote {

    private static Map<String, Integer> playersVotesMap = new HashMap<>();

    private static int i;

    public static void callMapVoting() {
        playersVotesMap.clear();
        Utils.voting = true;
        int[] i = {30};

        Timer.Task task = new Timer.Task() {
            @Override
            public void run() {
                Seq<mindustry.maps.Map> maps = getMaps();

                StringBuilder mapsList = new StringBuilder();

                int[] mapVotes = getMapVotes();
                for (int j = 0; j < getMaps().size; j++) {
                    int mapNumber = j + 1;
                    mapsList.append("\n[lightgray]" + mapNumber + " [gray]| [gold]").append(mapVotes[j]).append(" [gray]| ").append(maps.get(j).file.name());
                }

                Call.infoPopup("[gray][ [royal]Vote []]\n[gray][ [cyan]Say []<map number> []in [gray]]\n[ chat to vote for map [gray]]\n" + mapsList + "\n\n[gold]Time left: " + Arrays.toString(i), 1, 0, 0, 400, 0, 0);

                i[0]--;
                if (i[0] == 0) {
                    this.cancel();
                    reloadWorld(() -> {
                        mindustry.maps.Map target = getMostVotedMap();
                        Vars.world.loadMap(target, target.applyRules(Vars.state.rules.mode()));
                    });
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleTask(task, 0, 1);
    }

    private static mindustry.maps.Map getMostVotedMap() {
        Seq<mindustry.maps.Map> maps = getMaps();
        int[] votes = getMapVotes();
        int largest = 0;

        for (int i = 0; i < votes.length; i++) {
            largest = votes[i] > votes[largest] ? i : largest;
        }

        return maps.get(largest);

    }

    private static int[] getMapVotes() {
        int[] votes = new int[getMaps().size];
        playersVotesMap.forEach((key, voteNumber) -> {
            votes[voteNumber]++;
        });
        return votes;
    }

    public static void registerVote(Player player, Integer voteNumber) {
        if (!playersVotesMap.containsKey(player.uuid())) {
            if (voteNumber >= 1 && voteNumber <= getMaps().size) {
                playersVotesMap.put(player.uuid(), voteNumber-1);
            }
        } else {
            // player sendMessage you alredy voted!
        }
    }

    public static Seq<mindustry.maps.Map> getMaps() {
        return Vars.maps.customMaps();
    }

    public static void reloadWorld(Runnable runnable) {
        try {
            var reloader = new WorldReloader();
            reloader.begin();

            runnable.run();
            Vars.state.rules = Vars.state.map.applyRules(Vars.state.rules.mode());
            Vars.logic.play();

            reloader.end();
        } catch (MapException e) {
            Log.err("@: @", e.map.name(), e.getMessage());
        }
    }

}
