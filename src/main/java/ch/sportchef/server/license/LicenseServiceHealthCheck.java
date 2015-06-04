package ch.sportchef.server.license;

import ch.sportchef.server.utils.ServiceRegistry;
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
