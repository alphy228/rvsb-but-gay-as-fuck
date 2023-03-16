package net.voiddustry.redvsblue.util;

import arc.util.Timer;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Player;

import java.util.HashMap;
import java.util.Map;

public class MapVote {

    private static Map<String, Integer> playersVotesMap = new HashMap<>();

    public static void callMapVoting() {
        playersVotesMap.clear();

        Utils.voting = true;
        final int[] i = {0};
        Timer.Task task = new Timer.Task() {
            @Override
            public void run() {
                final String[] mapsText = new String[1];
                Vars.maps.all().each(m -> {
                    if (m.custom) {
                        mapsText[0] = mapsText[0] + "\n" + m.name();
                    }
                });
                Call.infoPopup("[gray][ [royal]Vote []]\n" + mapsText[0], 1, 0, 0, 400, 0, 0);

                i[0]++;
                if (i[0] == 30) {
                    this.cancel();

                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleTask(task, 0, 1);
    }

    public static void registerVote(Player player, Integer voteNumber) {
        if (!playersVotesMap.containsKey(player.uuid())) {
            playersVotesMap.put(player.uuid(), voteNumber);
        } else {
            // player.sendMessage you alredy voted!!!11
        }
    }

    public static mindustry.maps.Map[] getMaps() {
        mindustry.maps.Map[] maps = null;
        Vars.maps.all().each(m -> {
            if (m.custom) {
                maps[1] = m;
            }
        });
        return maps;
    }

}
