package net.ucrafts.ranks.tasks;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.messaging.MessagingService;
import net.luckperms.api.node.Node;
import net.ucrafts.ranks.Config;
import net.ucrafts.ranks.managers.RankManager;
import net.ucrafts.ranks.objects.PlayerTime;
import net.ucrafts.ranks.types.ConfigType;
import net.ucrafts.ranks.utils.RankUtils;

import java.util.*;

public class CheckRankTask implements Runnable
{

    private final RankManager manager;
    private final Config config;
    private final ProxyServer server;

    public CheckRankTask(RankManager manager, Config config, ProxyServer server)
    {
        this.manager = manager;
        this.config = config;
        this.server = server;
    }

    @Override
    public void run()
    {
        Set<Map.Entry<UUID, PlayerTime>> players = this.manager.getPlayers().entrySet();
        Set<Map.Entry<String, HashMap<String, Object>>> ranks = this.config.getRanks().entrySet();

        for (Map.Entry<UUID, PlayerTime> entry : players) {
            if (!this.server.getPlayer(entry.getKey()).isPresent()) {
                continue;
            }

            Player player = this.server.getPlayer(entry.getKey()).get();

            if (player.hasPermission(this.config.getConfig().getString(ConfigType.BYPASS_PERMISSION.getName()))) {
                continue;
            }

            HashMap<String, Map.Entry<String, HashMap<String, Object>>> calculateRanks = RankUtils.getRankForCalculate(
                    entry.getValue(), ranks
            );

            if (!calculateRanks.containsKey("suitable")) {
                continue;
            }

            Map.Entry<String, HashMap<String, Object>> suitableRank = calculateRanks.get("suitable");

            if (!player.hasPermission("group." + suitableRank.getKey())) {
                LuckPermsProvider.get().getUserManager().modifyUser(player.getUniqueId(), user -> {
                    user.data().add(Node.builder("group." + suitableRank.getKey()).build());
                });

                LuckPermsProvider.get().getMessagingService().ifPresent(MessagingService::pushUpdate);

                if (player.getCurrentServer().isPresent()) {
                    final Component mainTitle = Component.text(
                            this.config.getConfig().getString(ConfigType.MESSAGE_RANKUP_TITLE.getName())
                    );

                    final Component subtitle = Component.text(
                            String.format(
                                    this.config.getConfig().getString(ConfigType.MESSAGE_RANKUP_SUBTITLE.getName()),
                                    suitableRank.getValue().get("title")
                            )
                    );

                    Title title = Title.title(mainTitle, subtitle);

                    // todo: play sound
                    player.showTitle(title);
                    player.getCurrentServer().get()
                            .getServer()
                            .sendMessage(
                                    Component.text(String.format(
                                            this.config.getConfig().getString(ConfigType.MESSAGE_PREFIX.getName()) +
                                            this.config.getConfig().getString(ConfigType.MESSAGE_RANKUP.getName()),
                                            player.getUsername(),
                                            suitableRank.getValue().get("title")
                                    ))
                            );
                }
            }
        }
    }
}
