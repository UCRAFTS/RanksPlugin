package net.ucrafts.ranks.utils;

import net.ucrafts.ranks.Config;
import net.ucrafts.ranks.types.ConfigType;

public class TimeUtils
{

    public static long getTimeNow()
    {
        return System.currentTimeMillis() / 1000;
    }

    public static String convertPlayTime(long time, Config config)
    {
        if (time >= (60 * 60)) {
            return ((time / 60) / 60) + config.getConfig().getString(ConfigType.TIME_TYPE_HOURS.getName());
        } else if (time >= 60) {
            return (time / 60) + config.getConfig().getString(ConfigType.TIME_TYPE_MINUTES.getName());
        } else {
            return time + config.getConfig().getString(ConfigType.TIME_TYPE_SECONDS.getName());
        }
    }
}
