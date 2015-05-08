package ch.sportchef.server.healthchecks;

import ch.sportchef.server.representations.License;
import ch.sportchef.server.services.LicenseService;
import ch.sportchef.server.services.ServiceRegistry;
import com.codahale.metrics.health.HealthCheck;

import javax.management.ServiceNotFoundException;

public class LicenseServiceHealthCheck extends HealthCheck {

    private final LicenseService licenseService;

    public LicenseServiceHealthCheck() throws ServiceNotFoundException {
        this.licenseService = ServiceRegistry.getService(LicenseService.class);
    }

    @Override
    protected Result check() throws Exception {
        final License license = licenseService.readLicense();

        if (license != null) {
            return Result.healthy("LicenseService is fine.");
        }

        return Result.unhealthy("LicenseService has problems returning the correct license!");
    }
}
