package ch.sportchef.server.services;

import ch.sportchef.server.representations.License;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class LicenseServiceShould {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private License agpl = null;
    private LicenseService licenseService;

    @Before
    public void setUp() throws IOException {
        agpl = MAPPER.readValue(fixture("fixtures/license.json"), License.class);
        licenseService = new LicenseService();
    }

    @After
    public void tearDown() {
        licenseService = null;
        agpl = null;
    }

    @Test
    public void returnLicense() throws IOException {
        final License license = licenseService.readLicense();
        assertThat(license).isEqualTo(agpl);
    }

    @Test(expected = NotFoundException.class)
    public void returnLicenseNotFound() throws IOException {
        final Path originalLicenseFile = Paths.get("LICENSE.md");
        final Path movedLicenseFile = Paths.get("LICENSE.md.moved");
        try {
            Files.move(originalLicenseFile, movedLicenseFile);
            licenseService.readLicense();
        } finally {
            Files.move(movedLicenseFile, originalLicenseFile);
        }
    }
}
