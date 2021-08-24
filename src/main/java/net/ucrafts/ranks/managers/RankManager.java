package net.ucrafts.ranks.managers;

import co.aikar.commands.InvalidCommandArgument;
import net.ucrafts.ranks.Config;
import net.ucrafts.ranks.RanksPlugin;
import net.ucrafts.ranks.datasources.AbstractDataSource;
import net.ucrafts.ranks.objects.PlayerTime;
import net.ucrafts.ranks.types.ConfigType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

public class RankManager
{

    private final HashMap<UUID, PlayerTime> players = new HashMap<>();
    private final RanksPlugin plugin;
    private final AbstractDataSource dataSource;
    private final Config config;

    public RankManager(RanksPlugin plugin)
    {
        this.plugin = plugin;
        this.dataSource = this.plugin.getDataSource();
        this.config = this.plugin.getConfig();
    }

    public HashMap<UUID, PlayerTime> getPlayers()
    {
        return this.players;
    }

    public boolean isPlayerExist(UUID uuid)
    {
        return this.players.containsKey(uuid);
    }

    public PlayerTime getPlayer(UUID uuid)
    {
        if (this.isPlayerExist(uuid)) {
            return this.players.get(uuid);
        }

        return null;
    }

    public void addPlayer(UUID uuid, PlayerTime object)
    {
        this.players.put(uuid, object);
    }

    public void removePlayer(UUID uuid)
    {
        this.players.remove(uuid);
    }

    public void clear()
    {
        this.players.clear();
    }

    public void save(UUID uuid)
    {
        if (!this.isPlayerExist(uuid)) {
            return;
        }

        PlayerTime object = this.getPlayer(uuid);

        try {
            this.saveMainData(object);
            this.saveDetailData(object);
            this.removePlayer(uuid);
        } catch (Throwable e) {
            this.plugin.getLogger().error(e.getMessage());
        }
    }

    public PlayerTime getPlayerPlayTime(UUID uuid, PlayerTime object)
    {
        try (Connection connection = this.dataSource.getConnection()) {
            String query = "SELECT * FROM %s WHERE player = ? LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(
                    query.replace(
                            "%s",
                            this.config.getConfig().getString(ConfigType.DB_MAIN_TABLE.getName()))
            );

            preparedStatement.setString(1, uuid.toString());

            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                do {
                    return object.setPlayTime(result.getLong("play_time"));
                } while (result.next());
            }
        } catch (Exception e) {
            throw new InvalidCommandArgument("Cant get player data");
        }

        return object;
    }

    private void saveMainData(PlayerTime object)
    {
        try (Connection connection = this.dataSource.getConnection()) {
            String query = "SELECT * FROM %s WHERE player = ? LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(
                    query.replace(
                            "%s",
                            this.config.getConfig().getString(ConfigType.DB_MAIN_TABLE.getName()))
            );

            preparedStatement.setString(1, object.getUUID().toString());

            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                do {
                    query = "UPDATE %s SET play_time = play_time + ? WHERE player = ?";
                    preparedStatement = connection.prepareStatement(
                            query.replace(
                                    "%s",
                                    this.config.getConfig().getString(ConfigType.DB_MAIN_TABLE.getName()))
                    );

                    preparedStatement.setLong(1, object.getLastTime() - object.getJoinTime());
                    preparedStatement.setString(2, object.getUUID().toString());
                    preparedStatement.execute();
                } while (result.next());
            } else {
                query = "INSERT INTO %s VALUES(NULL, ?, ?)";
                preparedStatement = connection.prepareStatement(
                        query.replace(
                                "%s",
                                this.config.getConfig().getString(ConfigType.DB_MAIN_TABLE.getName()))
                );

                preparedStatement.setString(1, object.getUUID().toString());
                preparedStatement.setLong(2, object.getLastTime() - object.getJoinTime());
                preparedStatement.execute();
            }
        } catch (Exception e) {
            throw new InvalidCommandArgument("Cant save main data");
        }
    }

    private void saveDetailData(PlayerTime object)
    {
        try (Connection connection = this.dataSource.getConnection()) {
            String query = "SELECT * FROM %s WHERE player = ? AND server = ? and date = DATE(NOW()) LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(
                    query.replace(
                            "%s",
                            this.config.getConfig().getString(ConfigType.DB_DETAIL_TABLE.getName()))
            );

            preparedStatement.setString(1, object.getUUID().toString());
            preparedStatement.setString(2, object.getServer());

            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                do {
                    query = "UPDATE %s SET play_time = play_time + ? WHERE player = ? AND server = ? AND date = DATE(NOW())";
                    preparedStatement = connection.prepareStatement(
                            query.replace(
                                    "%s",
                                    this.config.getConfig().getString(ConfigType.DB_DETAIL_TABLE.getName()))
                    );

                    preparedStatement.setLong(1, object.getLastTime() - object.getJoinTime());
                    preparedStatement.setString(2, object.getUUID().toString());
                    preparedStatement.setString(3, object.getServer());
                    preparedStatement.execute();
                } while (result.next());
            } else {
                query = "INSERT INTO %s VALUES(NULL, ?, ?, DATE(NOW()), ?)";
                preparedStatement = connection.prepareStatement(
                        query.replace(
                                "%s",
                                this.config.getConfig().getString(ConfigType.DB_DETAIL_TABLE.getName()))
                );

                preparedStatement.setString(1, object.getUUID().toString());
                preparedStatement.setLong(2, object.getLastTime() - object.getJoinTime());
                preparedStatement.setString(3, object.getServer());
                preparedStatement.execute();
            }
        } catch (Exception e) {
            throw new InvalidCommandArgument("Cant save detail data");
        }
    }
}
