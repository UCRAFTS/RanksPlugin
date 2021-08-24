package net.ucrafts.ranks.objects;

import java.util.UUID;

public class PlayerTime
{

    private final UUID uuid;
    private long joinTime;
    private String name;
    private long lastTime;
    private long playTime;
    private String server;

    public PlayerTime(UUID uuid, long joinTime, String name)
    {
        this.uuid = uuid;
        this.joinTime = joinTime;
        this.lastTime = this.joinTime;
        this.name = name;
    }

    public PlayerTime(UUID uuid)
    {
        this.uuid = uuid;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public PlayerTime setName(String name)
    {
        this.name = name;

        return this;
    }

    public String getName()
    {
        return this.name;
    }

    public PlayerTime setLastTime(long time)
    {
        this.lastTime = time;

        return this;
    }

    public long getLastTime()
    {
        return this.lastTime;
    }

    public long getJoinTime()
    {
        return this.joinTime;
    }

    public PlayerTime setServer(String server)
    {
        this.server = server;

        return this;
    }

    public String getServer()
    {
        return this.server;
    }

    public PlayerTime setPlayTime(long playTime)
    {
        this.playTime = playTime;

        return this;
    }

    public long getPlayTime()
    {
        return this.playTime;
    }
}
