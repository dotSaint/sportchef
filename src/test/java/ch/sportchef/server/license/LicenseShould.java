package ch.sportchef.server.license;

import ch.sportchef.server.license.License;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class LicenseShould {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private final String shortName;
    private final String longName;
    private final String shortVersion;
    private final String longVersion;
    private final String text;
    private final String link;

    public LicenseShould() throws IOException {
        shortName = "AGPL";
        longName = "GNU AFFERO GENERAL PUBLIC LICENSE";
        shortVersion = "3";
        longVersion = "Version 3, 19 November 2007";
        text = new String(Files.readAllBytes(Paths.get("LICENSE.md")));
        link = "https://www.gnu.org/licenses/agpl-3.0.html";
    }

    @Test
    public void serializeToJSON() throws IOException {
        final License license = new License(shortName, longName, shortVersion, longVersion, text, link);
        assertThat(MAPPER.writeValueAsString(license)).isEqualTo(fixture("fixtures/license.json"));
    }

    @Test
    public void deserializeFromJSON() throws IOException {
        final License license = MAPPER.readValue(fixture("fixtures/license.json"), License.class);
        assertThat(license).isNotNull();
        assertThat(license.getShortName()).isEqualTo(shortName);
        assertThat(license.getLongName()).isEqualTo(longName);
        assertThat(license.getShortVersion()).isEqualTo(shortVersion);
        assertThat(license.getLongVersion()).isEqualTo(longVersion);
        assertThat(license.getText()).isEqualTo(text);
        assertThat(license.getLink()).isEqualTo(link);
    }

    @Test
    public void haveUsefulToStringImplementation() {
        final License license = new License(shortName, longName, shortVersion, longVersion, text, link);
        final String toString = license.toString();
        assertThat(toString).contains(shortName);
        assertThat(toString).contains(longName);
        assertThat(toString).contains(shortVersion);
        assertThat(toString).contains(longVersion);
        assertThat(toString).contains(text);
        assertThat(toString).contains(link);
    }
}
