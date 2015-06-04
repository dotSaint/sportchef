package ch.sportchef.server.license;

import ch.sportchef.server.utils.ServiceRegistry;
import com.codahale.metrics.annotation.Timed;

import javax.management.ServiceNotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("license")
public class LicenseResource {

    private final LicenseService licenseService;

    public LicenseResource() throws ServiceNotFoundException {
        this.licenseService = ServiceRegistry.getService(LicenseService.class);
    }

    @GET
    @Timed
    public Response readLicense() throws IOException {
        final License license = licenseService.readLicense();
        return Response.ok(license).build();
    }

}
