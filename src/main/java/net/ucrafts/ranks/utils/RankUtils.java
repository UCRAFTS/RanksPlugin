package net.ucrafts.ranks.utils;

import net.ucrafts.ranks.objects.PlayerTime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RankUtils
{

    public static HashMap<String, Map.Entry<String, HashMap<String, Object>>> getRankForCalculate(
            PlayerTime object,
            Set<Map.Entry<String, HashMap<String, Object>>> ranks
            )
    {
        HashMap<String, Map.Entry<String, HashMap<String, Object>>> result = new HashMap<>();
        long playTime = object.getPlayTime() + (object.getLastTime() - object.getJoinTime());

        for (Map.Entry<String, HashMap<String, Object>> rank : ranks) {
            if (!rank.getValue().containsKey("time") || !rank.getValue().containsKey("title")) {
                continue;
            }

            long rankTime = Long.parseLong(String.valueOf(rank.getValue().get("time")));

            if (playTime >= rankTime) {
                result.put("suitable", rank);
            } else {
                result.put("next", rank);
                break;
            }
        }

        return result;
    }
}
