package ch.sportchef.server.healthchecks;

import ch.sportchef.server.configuration.SportChefConfiguration;
import ch.sportchef.server.representations.User;
import ch.sportchef.server.services.ServiceRegistry;
import ch.sportchef.server.services.UserService;
import com.codahale.metrics.health.HealthCheck;

import javax.management.ServiceNotFoundException;
import java.util.Optional;

public class UserServiceHealthCheck extends HealthCheck {

    private final UserService userService;
    private final User referenceUser;

    public UserServiceHealthCheck(final SportChefConfiguration configuration) throws ServiceNotFoundException {
        this.userService = ServiceRegistry.getService(UserService.class);
        this.referenceUser = configuration.getHealthCheckConfiguration()
                .getUserServiceConfiguration().getReferenceUser();
    }

    @Override
    protected Result check() throws Exception {
        final long userId = referenceUser.getId();
        final Optional<User> checkUser = userService.readUserById(userId);

        if (checkUser.isPresent() && checkUser.get().equals(referenceUser)) {
            return Result.healthy("UserService is fine.");
        }

        return Result.unhealthy("UserService has problems returning the correct reference user!");
    }
}
