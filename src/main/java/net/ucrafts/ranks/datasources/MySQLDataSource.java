package net.ucrafts.ranks.datasources;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ucrafts.ranks.Config;
import net.ucrafts.ranks.types.ConfigType;
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class MySQLDataSource extends AbstractDataSource implements DataSourceInterface
{

    protected String driverClassName = "com.mysql.cj.jdbc.Driver";
    private final Logger logger;

    @Inject
    public MySQLDataSource(Config config, Logger logger)
    {
        super(config);

        this.logger = logger;

        if (this.dataSource != null) {
            this.close();
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(this.driverClassName);
        hikariConfig.setUsername(this.config.getConfig().getString(ConfigType.DB_USER.getName()));
        hikariConfig.setPassword(this.config.getConfig().getString(ConfigType.DB_PASS.getName()));
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setPoolName("RanksPool");
        hikariConfig.setJdbcUrl(
                String.format(
                        "jdbc:mysql://%s:%s/%s?useSSL=false&verifyServerCertificate=false&autoReconnect=true&serverTimezone=UTC",
                        this.config.getConfig().getString(ConfigType.DB_HOST.getName()),
                        this.config.getConfig().getInt(ConfigType.DB_PORT.getName()),
                        this.config.getConfig().getString(ConfigType.DB_BASE.getName())
                )
        );

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("logWriter", new PrintWriter(System.out));

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public void createTables()
    {
        this.createMainTable();
        this.createDetailTable();
    }

    private void createMainTable()
    {
        String query = "create table if not exists %s (id int not null auto_increment primary key, player varchar(36) not null, play_time int not null, index %s_check_index (player, play_time));";

        try (Connection connection = this.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query.replace("%s", this.config.getConfig().getString(ConfigType.DB_MAIN_TABLE.getName())));
            preparedStatement.execute();
        } catch (Exception e) {
            this.logger.error("Cant create main table: " + e.getMessage());
        }
    }

    private void createDetailTable()
    {
        String query = "create table if not exists %s (id int not null auto_increment primary key, player varchar(36) not null, play_time int not null, date date not null, server varchar(255) not null, index %s_check_index (player, play_time, date, server), index %s_check_by_server_index (date, server));";

        try (Connection connection = this.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query.replace("%s", this.config.getConfig().getString(ConfigType.DB_DETAIL_TABLE.getName())));
            preparedStatement.execute();
        } catch (Exception e) {
            this.logger.error("Cant create detail table: " + e.getMessage());
        }
    }
}
