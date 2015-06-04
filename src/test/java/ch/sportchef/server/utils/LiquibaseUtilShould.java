package ch.sportchef.server.utils;

import ch.sportchef.server.SportChefConfiguration;
import ch.sportchef.server.SportChefDataSourceFactory;
import com.codahale.metrics.MetricRegistry;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Environment;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LiquibaseUtil.class)
public class LiquibaseUtilShould {

    @Test
    public void migrateTheDatabase() throws Exception {

        final Connection connection = mock(Connection.class);

        final ManagedDataSource dataSource = mock(ManagedDataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);

        final SportChefDataSourceFactory dataSourceFactory = mock(SportChefDataSourceFactory.class);
        when(dataSourceFactory.isMigrateOnStart()).thenReturn(false);
        when(dataSourceFactory.build(any(MetricRegistry.class), anyString())).thenReturn(dataSource);

        final SportChefConfiguration configuration = mock(SportChefConfiguration.class);
        when(configuration.getDataSourceFactory()).thenReturn(dataSourceFactory);

        final Environment environment = mock (Environment.class);

        final JdbcConnection jdbcConnection = mock(JdbcConnection.class);
        whenNew(JdbcConnection.class).withAnyArguments().thenReturn(jdbcConnection);

        final Liquibase liquibase = mock(Liquibase.class);
        whenNew(Liquibase.class).withAnyArguments().thenReturn(liquibase);

        new LiquibaseUtil().migrate(configuration, environment);

        verify(liquibase, times(1)).update(anyString());
    }
}
