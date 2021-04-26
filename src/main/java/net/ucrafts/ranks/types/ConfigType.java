package net.ucrafts.ranks.types;

import org.jetbrains.annotations.NotNull;

public enum ConfigType
{

    DB_HOST("db.host"),
    DB_PORT("db.port"),
    DB_USER("db.user"),
    DB_PASS("db.pass"),
    DB_BASE("db.base"),
    DB_MAIN_TABLE("db.mainTable"),
    DB_DETAIL_TABLE("db.detailTable"),
    PERIOD("period"),
    TIME_TYPE_SECONDS("timeTypes.seconds"),
    TIME_TYPE_MINUTES("timeTypes.minutes"),
    TIME_TYPE_HOURS("timeTypes.hours"),
    MESSAGE_PREFIX("messages.prefix"),
    MESSAGE_PLAYER_NOT_FOUND("messages.playerNotFound"),
    MESSAGE_PLAYER_NAME_LINE("messages.playerNameLine"),
    MESSAGE_PLAYER_MAIN_TIME_LINE("messages.playerMainTimeLine"),
    MESSAGE_PLAYER_SESSION_TIME_LINE("messages.playerSessionTimeLine"),
    MESSAGE_PLAYER_CURRENT_RANK_LINE("messages.playerCurrentRankLine"),
    MESSAGE_PLAYER_NEXT_RANK_LINE("messages.playerNextRankLine"),
    MESSAGE_PLAYER_NEXT_RANK_WAIT_LINE("messages.playerNextRankWaitLine"),
    MESSAGE_RANKUP_TITLE("messages.rankUpTitle"),
    MESSAGE_RANKUP_SUBTITLE("messages.rankUpSubTitle"),
    MESSAGE_RANKUP("messages.rankUp"),
    BYPASS_PERMISSION("bypassPermission"),
    RANKS("ranks");

    private final String name;

    ConfigType(@NotNull final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
}
