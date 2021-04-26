package net.ucrafts.ranks.managers;

import co.aikar.commands.VelocityCommandManager;
import com.velocitypowered.api.proxy.ProxyServer;
import net.ucrafts.ranks.RanksPlugin;
import net.ucrafts.ranks.commands.RankCommand;

public class CommandManager
{

    private final ProxyServer server;
    private final RanksPlugin plugin;
    private final VelocityCommandManager manager;

    public CommandManager(ProxyServer server, RanksPlugin plugin)
    {
        this.server = server;
        this.plugin = plugin;
        this.manager = new VelocityCommandManager(server, this.plugin);
    }

    public void registerCommands()
    {
        this.manager.registerCommand(new RankCommand(this.server, this.plugin));
    }
}
