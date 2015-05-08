package ch.sportchef.server;

import ch.sportchef.server.configuration.HealthCheckConfiguration;
import ch.sportchef.server.configuration.SportChefConfiguration;
import ch.sportchef.server.configuration.SportChefDataSourceFactory;
import ch.sportchef.server.configuration.UserServiceConfiguration;
import ch.sportchef.server.dao.UserDAO;
import ch.sportchef.server.services.LicenseService;
import ch.sportchef.server.services.ServiceRegistry;
import ch.sportchef.server.services.UserService;
import ch.sportchef.server.utils.UserGenerator;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.Bundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skife.jdbi.v2.DBI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(App.class)
public class AppShould {

    @Test
    public void startWithArguments() throws Exception {
        final App appMock = mock(App.class);
        whenNew(App.class).withNoArguments().thenReturn(appMock);
        App.main(new String[]{"server", "config-test.yaml"});
        verify(appMock, times(1)).run(anyVararg());
    }

    @Test
    public void initializeProperly() {
        final Bootstrap<SportChefConfiguration> bootstrap = mock(Bootstrap.class);

        new App().initialize(bootstrap);

        verify(bootstrap, times(2)).addBundle(any(Bundle.class));
    }

    @Test
    public void runProperly() throws Exception {
        final HealthCheckRegistry healthCheckRegistry = mock(HealthCheckRegistry.class);
        final JerseyEnvironment jerseyEnvironment = mock(JerseyEnvironment.class);
        final UserDAO userDAO = mock(UserDAO.class);

        final DBI dbi = mock(DBI.class);
        when(dbi.onDemand(any())).thenReturn(userDAO);

        final DBIFactory dbiFactory = mock(DBIFactory.class);
        whenNew(DBIFactory.class).withNoArguments().thenReturn(dbiFactory);
        when(dbiFactory.build(any(Environment.class), any(DataSourceFactory.class), anyString())).thenReturn(dbi);

        final SportChefDataSourceFactory dataSourceFactory = mock(SportChefDataSourceFactory.class);
        when(dataSourceFactory.isMigrateOnStart()).thenReturn(false);

        final UserServiceConfiguration userServiceConfiguration = mock(UserServiceConfiguration.class);
        when(userServiceConfiguration.getReferenceUser()).thenReturn(UserGenerator.getJohnDoe(1L));

        final HealthCheckConfiguration healthCheckConfiguration = mock(HealthCheckConfiguration.class);
        when (healthCheckConfiguration.getUserServiceConfiguration()).thenReturn(userServiceConfiguration);

        final SportChefConfiguration configuration = mock(SportChefConfiguration.class);
        when(configuration.getDataSourceFactory()).thenReturn(dataSourceFactory);
        when (configuration.getHealthCheckConfiguration()).thenReturn(healthCheckConfiguration);

        final Environment environment = mock (Environment.class);
        when(environment.healthChecks()).thenReturn(healthCheckRegistry);
        when(environment.jersey()).thenReturn(jerseyEnvironment);
        when(environment.metrics()).thenReturn(null);

        new App().run(configuration, environment);

        assertThat(ServiceRegistry.getService(LicenseService.class)).isNotNull();
        assertThat(ServiceRegistry.getService(UserService.class)).isNotNull();

        verify(healthCheckRegistry, times(2)).register(anyString(), any(HealthCheck.class));
        verify(jerseyEnvironment, times(2)).register(any(Object.class));
    }
}
