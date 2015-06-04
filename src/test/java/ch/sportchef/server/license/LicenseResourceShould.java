package ch.sportchef.server.license;

import ch.sportchef.server.SportChefApp;
import ch.sportchef.server.SportChefConfiguration;
import ch.sportchef.server.license.License;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.junit.DropwizardAppRule;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.exception.LockException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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
import java.sql.SQLException;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class LicenseResourceShould {

    @ClassRule
    public static final DropwizardAppRule<SportChefConfiguration> RULE = new DropwizardAppRule<>(SportChefApp.class, "config-test.yaml");

    @BeforeClass
    public static void setup() throws SQLException, LiquibaseException, IOException {
        agpl = MAPPER.readValue(fixture("fixtures/license.json"), License.class);
    }

    @AfterClass
    public static void tearDown() throws DatabaseException, LockException {
        agpl = null;
    }

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private static License agpl = null;

    @Test
    public void returnLicenseInformation() {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/license", RULE.getLocalPort()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        final License license = response.readEntity(License.class);
        assertThat(license).isEqualTo(agpl);
    }

    @Test
    public void returnLicenseNotFound() throws IOException {
        final Path originalLicenseFile = Paths.get("LICENSE.md");
        final Path movedLicenseFile = Paths.get("LICENSE.md.moved");
        try {
            Files.move(originalLicenseFile, movedLicenseFile);

            final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/license", RULE.getLocalPort()));

            final Response response = target
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get();

            assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        } finally {
            Files.move(movedLicenseFile, originalLicenseFile);
        }
    }
}
