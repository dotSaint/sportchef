package ch.sportchef.server;

import ch.sportchef.server.bundles.SportChefMigrationsBundle;
import ch.sportchef.server.configuration.SportChefConfiguration;
import ch.sportchef.server.dao.UserDAO;
import ch.sportchef.server.healthchecks.LicenseServiceHealthCheck;
import ch.sportchef.server.healthchecks.UserServiceHealthCheck;
import ch.sportchef.server.resources.LicenseResource;
import ch.sportchef.server.resources.UserResource;
import ch.sportchef.server.services.LicenseService;
import ch.sportchef.server.services.ServiceRegistry;
import ch.sportchef.server.services.UserService;
import ch.sportchef.server.utils.LiquibaseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class App extends Application<SportChefConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(@Nonnull final String[] args) throws Exception {
        LOGGER.info("Starting application with arguments: %s", new Object[]{args});
        new App().run(args);
    }

    @Override
    public void initialize(@Nonnull final Bootstrap<SportChefConfiguration> bootstrap) {
        // Configure Assets
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "html/index.html"));

        // Configure Migrations
        bootstrap.addBundle(new SportChefMigrationsBundle());

        // Register additional Jackson modules
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
    }

    @Override
    public void run(@Nonnull final SportChefConfiguration configuration, @Nonnull final Environment environment) throws Exception {

        // Setup database configuration
        final DBIFactory factory = new DBIFactory();
        final DBI dbi = factory.build(environment, configuration.getDataSourceFactory(), "default");

        // Migrate database if configured
        if (configuration.getDataSourceFactory().isMigrateOnStart()) {
            new LiquibaseUtil().migrate(configuration, environment);
        }

        // Prepare data access objects
        final UserDAO userDAO = dbi.onDemand(UserDAO.class);

        // Register services
        ServiceRegistry.register(new LicenseService());
        ServiceRegistry.register(new UserService(userDAO));

        // Initialize health checks
        environment.healthChecks().register("licenseService", new LicenseServiceHealthCheck());
        environment.healthChecks().register("userService", new UserServiceHealthCheck(configuration));

        // Initialize resources
        environment.jersey().register(new LicenseResource());
        environment.jersey().register(new UserResource());
    }
}
