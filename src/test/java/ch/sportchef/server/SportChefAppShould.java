package ch.sportchef.server;

import ch.sportchef.server.user.UserServiceConfiguration;
import ch.sportchef.server.user.UserDAO;
import ch.sportchef.server.license.LicenseService;
import ch.sportchef.server.utils.ServiceRegistry;
import ch.sportchef.server.user.UserService;
import ch.sportchef.server.user.UserGenerator;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Verifier;
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
@PrepareForTest({SportChefApp.class, ServiceRegistry.class})
public class SportChefAppShould {

    @Test
    public void startWithArguments() throws Exception {
        final SportChefApp appMock = mock(SportChefApp.class);
        whenNew(SportChefApp.class).withNoArguments().thenReturn(appMock);
        SportChefApp.main(new String[]{"server", "config-test.yaml"});
        verify(appMock, times(1)).run(anyVararg());
    }

    @Test
    public void initializeProperly() {
        final Bootstrap<SportChefConfiguration> bootstrap = mock(Bootstrap.class);

        new SportChefApp().initialize(bootstrap);

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

        final SportChefHealthCheckConfiguration healthCheckConfiguration = mock(SportChefHealthCheckConfiguration.class);
        when (healthCheckConfiguration.getUserServiceConfiguration()).thenReturn(userServiceConfiguration);

        final SportChefConfiguration configuration = mock(SportChefConfiguration.class);
        when(configuration.getDataSourceFactory()).thenReturn(dataSourceFactory);
        when (configuration.getHealthCheckConfiguration()).thenReturn(healthCheckConfiguration);

        final Environment environment = mock (Environment.class);
        when(environment.healthChecks()).thenReturn(healthCheckRegistry);
        when(environment.jersey()).thenReturn(jerseyEnvironment);
        when(environment.metrics()).thenReturn(null);

        final HmacSHA512Verifier hmacSHA512Verifier = mock(HmacSHA512Verifier.class);
        whenNew(HmacSHA512Verifier.class).withAnyArguments().thenReturn(hmacSHA512Verifier);

        /*
        final UserService userService = Mockito.mock(UserService.class);
        mockStatic(ServiceRegistry.class);
        when(ServiceRegistry.getService(eq(UserService.class))).thenReturn(userService);
        */

        new SportChefApp().run(configuration, environment);

        assertThat(ServiceRegistry.getService(LicenseService.class)).isNotNull();
        assertThat(ServiceRegistry.getService(UserService.class)).isNotNull();

        verify(healthCheckRegistry, times(2)).register(anyString(), any(HealthCheck.class));
        verify(jerseyEnvironment, times(5)).register(any(Object.class));
    }
}
