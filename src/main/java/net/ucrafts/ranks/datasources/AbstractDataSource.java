package net.ucrafts.ranks.datasources;

import java.sql.Connection;
import java.sql.SQLException;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariDataSource;
import net.ucrafts.ranks.Config;

public abstract class AbstractDataSource implements DataSourceInterface
{

    protected Config config;
    protected HikariDataSource dataSource;

    @Inject
    public AbstractDataSource(Config config)
    {
        this.config = config;
    }


    public void close()
    {
        try {
            this.dataSource.close();
        } catch (Throwable e) {
            ;
        }
    }


    public Connection getConnection() throws SQLException
    {
        return this.dataSource.getConnection();
    }

    @Override
    abstract public void createTables();
}
