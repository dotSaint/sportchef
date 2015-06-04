package ch.sportchef.server.license;

import ch.sportchef.server.utils.Service;
import com.google.common.base.Charsets;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LicenseService implements Service {

    public License readLicense() {
        try {
            final String text = new String(
                    Files.readAllBytes(Paths.get("LICENSE.md")),
                    Charsets.UTF_8);
            final License license = new License(
                    "AGPL", "GNU AFFERO GENERAL PUBLIC LICENSE",
                    "3", "Version 3, 19 November 2007",
                    text,
                    "https://www.gnu.org/licenses/agpl-3.0.html");
            return license;
        } catch (final IOException e) {
            throw new NotFoundException(e);
        }
    }
}
