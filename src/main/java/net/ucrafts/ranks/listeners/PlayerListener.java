package net.ucrafts.ranks.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.ucrafts.ranks.RanksPlugin;
import net.ucrafts.ranks.fabrics.PlayerTimeFabric;
import net.ucrafts.ranks.managers.RankManager;
import net.ucrafts.ranks.objects.PlayerTime;
import net.ucrafts.ranks.utils.TimeUtils;

import java.util.UUID;

public class PlayerListener
{

    private final RankManager manager;
    private final RanksPlugin plugin;
    private final ProxyServer server;

    public PlayerListener(RankManager manager, RanksPlugin plugin, ProxyServer server)
    {
        this.manager = manager;
        this.plugin = plugin;
        this.server = server;
    }

    @Subscribe
    public void onLogin(LoginEvent e)
    {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerTime object = PlayerTimeFabric.create(player);

        this.server.getScheduler()
                .buildTask(this.plugin, () -> this.manager.addPlayer(
                        uuid, this.manager.getPlayerPlayTime(uuid, object))
                ).schedule();
    }

    @Subscribe
    public void onServerConnect(ServerConnectedEvent e)
    {
        UUID uuid = e.getPlayer().getUniqueId();

        if (this.manager.isPlayerExist(uuid)) {
            PlayerTime object = this.manager.getPlayer(uuid)
                    .setServer(e.getServer().getServerInfo().getName())
                    .setLastTime(TimeUtils.getTimeNow());

            this.manager.addPlayer(uuid, object);
        }
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent e)
    {
        this.server.getScheduler()
                .buildTask(this.plugin, () -> this.manager.save(e.getPlayer().getUniqueId())).schedule();
    }
}
