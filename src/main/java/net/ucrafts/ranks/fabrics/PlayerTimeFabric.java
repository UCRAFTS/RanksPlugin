package net.ucrafts.ranks.fabrics;

import com.velocitypowered.api.proxy.Player;
import net.ucrafts.ranks.objects.PlayerTime;
import net.ucrafts.ranks.utils.TimeUtils;

import java.util.UUID;

public class PlayerTimeFabric
{

    public static PlayerTime create(Player player)
    {
        return new PlayerTime(player.getUniqueId(), TimeUtils.getTimeNow(), player.getUsername());
    }

    public static PlayerTime create(UUID uuid)
    {
        return new PlayerTime(uuid);
    }
}
