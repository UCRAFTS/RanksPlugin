package net.ucrafts.ranks.tasks;

import net.ucrafts.ranks.managers.RankManager;
import net.ucrafts.ranks.objects.PlayerTime;
import net.ucrafts.ranks.utils.TimeUtils;

import java.util.Map;
import java.util.UUID;

public class UpdateRankTask implements Runnable
{

    private final RankManager manager;

    public UpdateRankTask(RankManager manager)
    {
        this.manager = manager;
    }

    @Override
    public void run()
    {
        for (Map.Entry<UUID, PlayerTime> object : this.manager.getPlayers().entrySet()) {
            this.manager.addPlayer(object.getKey(), object.getValue().setLastTime(TimeUtils.getTimeNow()));
        }
    }
}
