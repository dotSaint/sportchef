package ch.sportchef.server.utils;

import ch.sportchef.server.SportChefConfiguration;
import ch.sportchef.server.SportChefDataSourceFactory;
import ch.sportchef.server.utils.SportChefMigrationsBundle;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SportChefMigrationsBundleShould {

    @Test
    public void returnDataSourceFactory() {
        final SportChefConfiguration configuration = mock(SportChefConfiguration.class);
        final SportChefDataSourceFactory factory = mock(SportChefDataSourceFactory.class);
        when(configuration.getDataSourceFactory()).thenReturn(factory);

        final SportChefMigrationsBundle bundle = new SportChefMigrationsBundle();

        assertThat(bundle.getDataSourceFactory(configuration)).isEqualTo(factory);
    }
}
