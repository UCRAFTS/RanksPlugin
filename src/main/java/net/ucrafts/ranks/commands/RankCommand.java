package net.ucrafts.ranks.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.ucrafts.ranks.Config;
import net.ucrafts.ranks.RanksPlugin;
import net.ucrafts.ranks.fabrics.PlayerTimeFabric;
import net.ucrafts.ranks.managers.RankManager;
import net.ucrafts.ranks.objects.PlayerTime;
import net.ucrafts.ranks.types.ConfigType;
import net.ucrafts.ranks.utils.RankUtils;
import net.ucrafts.ranks.utils.TimeUtils;

import java.util.HashMap;
import java.util.Map;

@CommandAlias("rank")
public class RankCommand extends BaseCommand
{

    private final ProxyServer server;
    private final RanksPlugin plugin;
    private final Config config;
    private final RankManager manager;

    public RankCommand(ProxyServer server, RanksPlugin plugin)
    {
        this.server = server;
        this.plugin = plugin;
        this.config = this.plugin.getConfig();
        this.manager = this.plugin.getManager();
    }

    @Default
    public void onCommand(Player executor, @Optional String player)
    {
        this.server.getScheduler().buildTask(this.plugin, () -> {
            PlayerTime object = null;

            if (player != null) {
                if (this.server.getPlayer(player).isPresent()) {
                    object = this.manager.getPlayer(this.server.getPlayer(player).get().getUniqueId());
                } else {
                    User user = LuckPermsProvider.get().getUserManager().getUser(player);

                    if (user != null) {
                        object = PlayerTimeFabric.create(user.getUniqueId())
                                .setName(user.getUsername());

                        object = this.manager.getPlayerPlayTime(user.getUniqueId(), object);
                    }
                }
            } else {
                object = this.manager.getPlayer(executor.getUniqueId());
            }

            if (object == null) {
                executor.sendMessage(
                        Component.text(
                                this.config.getConfig().getString(ConfigType.MESSAGE_PREFIX.getName()) +
                                this.config.getConfig().getString(ConfigType.MESSAGE_PLAYER_NOT_FOUND.getName())
                        )
                );
            } else {
                this.sendPlayerInfo(executor, object);
            }
        }).schedule();
    }

    private void sendPlayerInfo(Player player, PlayerTime object)
    {
        HashMap<String, Map.Entry<String, HashMap<String, Object>>> calculateRanks = RankUtils.getRankForCalculate(
                object, this.config.getRanks().entrySet()
        );

        Map.Entry<String, HashMap<String, Object>> suitableRank = calculateRanks.get("suitable");
        Map.Entry<String, HashMap<String, Object>> nextRank = calculateRanks.get("next");
        long playTime = object.getPlayTime() + (object.getLastTime() - object.getJoinTime());
        String mainTime = TimeUtils.convertPlayTime(playTime, this.config);
        String sessionTime = TimeUtils.convertPlayTime(object.getLastTime() - object.getJoinTime(), this.config);

        player.sendMessage(
                Component.text(
                        this.config.getConfig().getString(ConfigType.MESSAGE_PREFIX.getName()) +
                                String.format(
                                        this.config.getConfig().getString(ConfigType.MESSAGE_PLAYER_NAME_LINE.getName()),
                                        object.getName()
                                )
                )
        );

        player.sendMessage(
                Component.text(
                        this.config.getConfig().getString(ConfigType.MESSAGE_PREFIX.getName()) +
                                String.format(
                                        this.config.getConfig().getString(ConfigType.MESSAGE_PLAYER_MAIN_TIME_LINE.getName()),
                                        mainTime
                                )
                )
        );

        player.sendMessage(
                Component.text(
                        this.config.getConfig().getString(ConfigType.MESSAGE_PREFIX.getName()) +
                                String.format(
                                        this.config.getConfig().getString(ConfigType.MESSAGE_PLAYER_SESSION_TIME_LINE.getName()),
                                        sessionTime
                                )
                )
        );

        if (suitableRank != null) {
            player.sendMessage(
                    Component.text(
                            this.config.getConfig().getString(ConfigType.MESSAGE_PREFIX.getName()) +
                                    String.format(
                                            this.config.getConfig().getString(ConfigType.MESSAGE_PLAYER_CURRENT_RANK_LINE.getName()),
                                            suitableRank.getValue().get("title")
                                    )
                    )
            );
        }

        if (nextRank != null) {
            long rankTime = Long.parseLong(String.valueOf(nextRank.getValue().get("time")));
            long timeNextRank = rankTime - playTime;

            if (timeNextRank > 0) {
                player.sendMessage(
                        Component.text(
                                this.config.getConfig().getString(ConfigType.MESSAGE_PREFIX.getName()) +
                                        String.format(
                                                this.config.getConfig().getString(ConfigType.MESSAGE_PLAYER_NEXT_RANK_LINE.getName()),
                                                TimeUtils.convertPlayTime(timeNextRank, this.config)
                                        )
                        )
                );
            } else {
                player.sendMessage(
                        Component.text(
                                this.config.getConfig().getString(ConfigType.MESSAGE_PREFIX.getName()) +
                                        String.format(
                                                this.config.getConfig().getString(ConfigType.MESSAGE_PLAYER_NEXT_RANK_LINE.getName()),
                                                this.config.getConfig().getString(ConfigType.MESSAGE_PLAYER_NEXT_RANK_WAIT_LINE.getName())
                                        )
                        )
                );
            }
        }
    }
}
