package ch.sportchef.server.utils;

import ch.sportchef.server.SportChefConfiguration;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Environment;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;

public class LiquibaseUtil {
    public void migrate(final SportChefConfiguration configuration, final Environment environment) throws LiquibaseException, SQLException {
        final ManagedDataSource ds = configuration.getDataSourceFactory().build(environment.metrics(), "migrations");
        final Connection connection = ds.getConnection();
        final Liquibase liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(connection));
        if (configuration.getDataSourceFactory().isDropAllBeforeMigration()) {
            liquibase.dropAll();
        }
        liquibase.update("");
    }
}
