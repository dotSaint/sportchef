package ch.sportchef.server.license;

import ch.sportchef.server.SportChefApp;
import ch.sportchef.server.SportChefConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class LicenseServiceHealthCheckShould {

    @ClassRule
    public static final DropwizardAppRule<SportChefConfiguration> RULE = new DropwizardAppRule<>(SportChefApp.class, "config-test.yaml");

    @Test
    public void returnHealthy() throws IOException {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/healthcheck", RULE.getAdminPort()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        final String body = response.readEntity(String.class);
        assertThat(body).contains("LicenseService is fine.");
    }

    @Test
    public void returnUnhealthy() throws IOException {
        final Path originalLicenseFile = Paths.get("LICENSE.md");
        final Path movedLicenseFile = Paths.get("LICENSE.md.moved");
        try {
            Files.move(originalLicenseFile, movedLicenseFile);

            final WebTarget target = ClientBuilder.newClient().target(
                    String.format("http://localhost:%d/healthcheck", RULE.getAdminPort()));

            final Response response = target
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get();

            assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

            final String body = response.readEntity(String.class);
            assertThat(body).contains("LicenseService could not find the license!");
        } finally {
            Files.move(movedLicenseFile, originalLicenseFile);
        }
    }
}
