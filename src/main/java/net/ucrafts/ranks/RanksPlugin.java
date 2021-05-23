package net.ucrafts.ranks;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.ucrafts.ranks.datasources.AbstractDataSource;
import net.ucrafts.ranks.datasources.MySQLDataSource;
import net.ucrafts.ranks.listeners.PlayerListener;
import net.ucrafts.ranks.managers.CommandManager;
import net.ucrafts.ranks.managers.RankManager;
import net.ucrafts.ranks.tasks.CheckRankTask;
import net.ucrafts.ranks.tasks.UpdateRankTask;
import net.ucrafts.ranks.types.ConfigType;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "ranks",
        name = "RanksPlugin",
        version = "1.0.2",
        url = "https://ucrafts.net",
        description = "Change permission group by play time",
        authors = {
                "Alexander Repin / oDD1"
        },
        dependencies = {
                @Dependency(id = "luckperms")
        }
)
public class RanksPlugin
{

    private final HashSet<ScheduledTask> tasks = new HashSet<>();
    private final ProxyServer server;
    private final Config config;
    private final Logger logger;
    private final AbstractDataSource dataSource;
    private final RankManager manager;

    @Inject
    public RanksPlugin(ProxyServer server, Config config, Logger logger)
    {
        this.server = server;
        this.config = config;
        this.logger = logger;
        this.dataSource = new MySQLDataSource(this.config, this.logger);
        this.dataSource.createTables();

        this.manager = new RankManager(this);
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent e)
    {
        this.server.getEventManager().register(this, new PlayerListener(this.manager, this, this.server));
        this.tasks.add(
                this.server.getScheduler()
                        .buildTask(this, new UpdateRankTask(this.manager))
                        .repeat(1L, TimeUnit.SECONDS)
                        .schedule()
        );

        this.tasks.add(
                this.server.getScheduler()
                        .buildTask(this, new CheckRankTask(this.manager, this.config, this.server))
                        .repeat(this.config.getConfig().getInt(ConfigType.PERIOD.getName()), TimeUnit.MINUTES)
                        .schedule()
        );

        CommandManager manager = new CommandManager(this.server, this);
        manager.registerCommands();
    }

    public AbstractDataSource getDataSource()
    {
        return this.dataSource;
    }

    public Config getConfig()
    {
        return this.config;
    }

    public Logger getLogger() { return this.logger; }

    public RankManager getManager() { return this.manager; }

    @Subscribe
    public void onProxyShutdownEvent(ProxyShutdownEvent e)
    {
        for (ScheduledTask task : this.tasks) {
            task.cancel();
        }

        // todo: wait save tasks

        this.tasks.clear();
        this.manager.clear();
        this.dataSource.close();
    }
}
