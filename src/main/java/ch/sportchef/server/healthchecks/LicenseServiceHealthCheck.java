package ch.sportchef.server.healthchecks;

import ch.sportchef.server.representations.License;
import ch.sportchef.server.services.LicenseService;
import ch.sportchef.server.services.ServiceRegistry;
import com.codahale.metrics.health.HealthCheck;

import javax.management.ServiceNotFoundException;
import javax.ws.rs.NotFoundException;

public class LicenseServiceHealthCheck extends HealthCheck {

    private final LicenseService licenseService;

    public LicenseServiceHealthCheck() throws ServiceNotFoundException {
        this.licenseService = ServiceRegistry.getService(LicenseService.class);
    }

    @Override
    protected Result check() throws Exception {
        try {
            licenseService.readLicense();
            return Result.healthy("LicenseService is fine.");
        } catch (final NotFoundException e) {
            return Result.unhealthy("LicenseService could not find the license!");
        }
    }
}
