package ch.sportchef.server.healthchecks;

import ch.sportchef.server.configuration.SportChefConfiguration;
import ch.sportchef.server.representations.User;
import ch.sportchef.server.services.ServiceRegistry;
import ch.sportchef.server.services.UserService;
import com.codahale.metrics.health.HealthCheck;

import javax.management.ServiceNotFoundException;
import javax.ws.rs.NotFoundException;

public class UserServiceHealthCheck extends HealthCheck {

    private final UserService userService;
    private final User referenceUser;

    public UserServiceHealthCheck(final SportChefConfiguration configuration) throws ServiceNotFoundException {
        this.userService = ServiceRegistry.getService(UserService.class);
        this.referenceUser = configuration.getHealthCheckConfiguration()
                .getUserServiceConfiguration().getReferenceUser();
    }

    @Override
    protected Result check() {
        final long userId = referenceUser.getUserId();

        try {
            final User checkUser = userService.readUserById(userId);

            if (checkUser.equals(referenceUser)) {
                return Result.healthy("UserService is fine.");
            }
            return Result.unhealthy("UserService could not find the correct user with id '%d'!", userId);
        } catch (final NotFoundException e) {
            return Result.unhealthy("UserService could not find any user with id '%d'!", userId);
        }
    }
}
